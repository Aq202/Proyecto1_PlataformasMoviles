package com.example.proyecto_final_apps.ui.fragments.account_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.databinding.FragmentAccountDetailsBinding
import com.example.proyecto_final_apps.helpers.DATE_FORMAT
import com.example.proyecto_final_apps.helpers.getDecimal
import com.example.proyecto_final_apps.helpers.twoDigits
import com.example.proyecto_final_apps.ui.Components.PieChart
import com.example.proyecto_final_apps.ui.Components.PieChart.PieElement
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.ChartDescriptionAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.fragments.OperationDetailsFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


@AndroidEntryPoint
class AccountDetailsFragment : Fragment(), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentAccountDetailsBinding
    private val accountDetailsViewModel: AccountDetailsViewModel by viewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private val pieData: MutableList<PieElement> = mutableListOf()
    private val args: AccountDetailsFragmentArgs by navArgs()
    private val operationsRecyclerData: MutableList<OperationModel> = mutableListOf()
    private var blockDatePicker: Boolean = false

    private var blockFavoriteButton: Boolean = false
    private var blockDeleteButton = false
    private var blockEditButton = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpChartDescriptionRecycler()
        setUpOperationsRecycler()
        setListeners()
        setObservers()


        loadingViewModel.showLoadingDialog() //show loading
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            loadingViewModel.hideLoadingDialog() //hide loading
        }

    }


    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        accountDetailsViewModel.getAccountData(args.accountId, forceUpdate)
        accountDetailsViewModel.getAccountExpenses(args.accountId, forceUpdate)
        accountDetailsViewModel.getAccountOperations(args.accountId, forceUpdate)
        accountDetailsViewModel.getBalanceDescription(args.accountId)
    }


    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.dateFilterFlow.collectLatest { filter ->

                binding.chipGroupAccountDetailsDateFilter.removeAllViews() //limpiar filtro

                if (filter is AccountDetailsViewModel.Filter.ActiveDateFilter) {
                    addFilterChip(filter.startDate, filter.endDate)

                }
                //actualizar operaciones dependiendo del filtro
                accountDetailsViewModel.getAccountOperations(args.accountId, false)

            }
        }

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.initialAccountOperations.collectLatest { status ->
                if (status is Status.Success) {
                    addAccountExpensesData(status.value!!)
                    binding.apply {
                        pieChartAccountDetailsFragment.visibility = View.VISIBLE
                        recyclerViewAccountDetailsPieCategories.visibility = View.VISIBLE
                        textViewAccountDetailsFragmentExpensesNoContent.visibility = View.GONE
                    }
                } else if (status is Status.Error) {
                    println("Diego: ${status.error}")
                    binding.apply {
                        pieChartAccountDetailsFragment.visibility = View.GONE
                        recyclerViewAccountDetailsPieCategories.visibility = View.GONE
                        textViewAccountDetailsFragmentExpensesNoContent.visibility = View.VISIBLE

                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.expensesSelected.collectLatest { value ->
                if (value is Status.Success) changePieDataType(value.value!!)
            }
        }

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.accountOperations.collectLatest { status ->
                addAccountOperationsToRecyclerView(status)
            }
        }

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.accountData.collect { status ->
                if (status is Status.Success) {
                    binding.textViewAccountDetailsFragmentAccountName.text = status.value!!.title
                    //agregar icono de favorito
                    if (status.value.defaultAccount) binding.imageViewAccountDetailsFavoriteIcon.setImageDrawable(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_star),
                    )

                    if (!status.value.editable) disableHeaderButtons()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.accountBalanceData.collectLatest { status ->
                if (status is Status.Success) changeBalanceData(status.value)
            }
        }
    }

    private fun disableHeaderButtons() {
        binding.apply {
            val color = getColor(requireContext(), R.color.light_gray_2)
            cardViewAccountDetailsFragmentDeleteIconContainer.setCardBackgroundColor(color)
            cardViewAccountDetailsFragmentFavoriteIconContainer.setCardBackgroundColor(color)
            cardViewAccountDetailsFragmentEditIconContainer.setCardBackgroundColor(color)
            blockFavoriteButton = true
            blockDeleteButton = true
            blockEditButton = true
        }
    }


    private fun changeBalanceData(balanceData: Pair<Double, Double>?) {
        binding.apply {
            val generalBalance = balanceData!!.first
            val balanceMovement = balanceData.second

            binding.apply {

                //add values
                textViewAccountDetailsFragmentAccountBalance.text =
                    getString(R.string.money_format, generalBalance.toInt().twoDigits())
                textViewAccountDetailsFragmentAccountlBalanceCents.text =
                    getString(R.string.cents_format, generalBalance.getDecimal(2).twoDigits())
                textViewAccountDetailsFragmentAccountMovement.text =
                    getString(R.string.money_format, abs(balanceMovement).toInt().twoDigits())
                textViewAccountDetailsFragmentAccountMovementCents.text =
                    getString(R.string.cents_format, balanceMovement.getDecimal(2).twoDigits())

                //change movement text color
                if (balanceMovement >= 0) {
                    textViewAccountDetailsFragmentAccountMovement.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.light_green_2)
                    )
                    textViewAccountDetailsFragmentAccountMovementCents.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.light_green_2)
                    )
                } else {
                    textViewAccountDetailsFragmentAccountMovement.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                    textViewAccountDetailsFragmentAccountMovementCents.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                }
            }
        }
    }

    private fun changePieDataType(value: Boolean) {

        if (value) {
            binding.apply { //Gastos seleccionados
                textViewAccountDetailsFragmentExpensesLabel.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.aqua)
                )
                textViewAccountDetailsFragmentIncomeLabel.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.dark_blue)
                )
                textViewAccountDetailsFragmentExpensesNoContent.text =
                    getString(R.string.no_hubo_gastos)
            }
        } else {
            binding.apply { //Ingresos seleccionados
                textViewAccountDetailsFragmentExpensesLabel.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.dark_blue)
                )
                textViewAccountDetailsFragmentIncomeLabel.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.aqua)
                )
                textViewAccountDetailsFragmentExpensesNoContent.text =
                    getString(R.string.no_hubo_ingresos)
            }
        }


        //cambiar datos de grafico
        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.getAccountExpenses(args.accountId, false)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addAccountExpensesData(expenses: List<OperationModel>?) {

        pieData.clear() //limpiar datos anteriores

        val categoriesMap = mutableMapOf<Int, PieElement>()

        //agrupar gastos por categoria
        expenses?.forEach { op ->
            if (!categoriesMap.containsKey(op.category)) {
                val category = Category(requireContext()).getCategory(op.category)
                if (category != null) {
                    categoriesMap[op.category] =
                        PieElement(category.color, abs(op.amount), category.name)
                }
            } else {
                categoriesMap[op.category]!!.amount += op.amount
            }
        }

        //Mantener categorias con gastos mayores a cero y ordenarlas
        val categoriesList =
            categoriesMap.toList().map { it.second }.filter { it.amount > 0 }
                .sortedByDescending { it.amount }

        //mantener solo los 3 más grandes
        val categoriesToKeep = 3
        if (categoriesList.size > categoriesToKeep) {
            val lastCategories = categoriesList.takeLast(categoriesList.size - categoriesToKeep)
            pieData.addAll(categoriesList.take(categoriesToKeep)) //mantiene solo n primeras categorias

            //agregar categoria 'otros'
            var othersAmount = 0.0
            val defaultCategory = Category(requireContext()).getCategory(0)
            lastCategories.forEach { othersAmount += it.amount }
            pieData.add(PieElement(defaultCategory!!.color, othersAmount, defaultCategory.name))

        } else
            pieData.addAll(categoriesList)

        //actualizar grafico y descripcion
        setUpPieChart()
        binding.recyclerViewAccountDetailsPieCategories.adapter?.notifyDataSetChanged()


    }


    private fun addFilterChip(startDate: Date, endDate: Date) {

        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)

        val chip = Chip(requireContext())
        chip.text = requireContext().getString(
            R.string.date_filter_format,
            dateFormat.format(startDate),
            dateFormat.format(endDate)
        )

        chip.isCloseIconVisible = true

        chip.setChipIconResource(R.drawable.ic_calendar)
        chip.setOnCloseIconClickListener {
            binding.chipGroupAccountDetailsDateFilter.removeView(chip)
            accountDetailsViewModel.removeDateFilter()
        }

        binding.chipGroupAccountDetailsDateFilter.addView(chip)
    }

    private fun setListeners() {
        binding.apply {
            imageViewAccountDetailsSortIcon.setOnClickListener {
                setUpDatePicker()
            }

            swipeRefreshLayoutAccountDetailsFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {
                    getFragmentData(true)
                    binding.swipeRefreshLayoutAccountDetailsFragment.isRefreshing = false
                }
            }

            textViewAccountDetailsFragmentExpensesLabel.setOnClickListener {
                accountDetailsViewModel.selectExpenses()
            }

            textViewAccountDetailsFragmentIncomeLabel.setOnClickListener {
                accountDetailsViewModel.selectIncomes()
            }

            cardViewAccountDetailsFragmentFavoriteIconContainer.setOnClickListener {
                favoritePressedAction()
            }

            cardViewAccountDetailsFragmentDeleteIconContainer.setOnClickListener {
                deleteAccountAction()
            }
        }
    }

    private fun deleteAccountAction() {
        if (blockDeleteButton) return

        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("Aceptar") { _, _ ->

                blockDeleteButton = true

                lifecycleScope.launchWhenStarted {
                    accountDetailsViewModel.deleteAccount(args.accountId).collectLatest { status ->
                        when (status) {
                            is Status.Loading -> loadingViewModel.showLoadingDialog()
                            is Status.Success -> {
                                loadingViewModel.hideLoadingDialog()
                                findNavController().navigate(R.id.action_accountDetailsFragment_to_homeFragment)
                            }
                            is Status.Error -> {
                                loadingViewModel.hideLoadingDialog()
                                Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                loadingViewModel.hideLoadingDialog()
                            }
                        }
                    }
                }

            }
            .setNegativeButton("Cancelar") { _, _ ->

            }
            .setTitle("¿Deseas eliminar esta cuenta?")
            .setMessage("Toma en cuenta que esta acción es permanente y que eliminará todas las operaciones asociadas a esta cuenta.")
            .show()



    }

    private fun favoritePressedAction() {

        if (blockFavoriteButton) return

        val accountDataStatus = accountDetailsViewModel.accountData.value
        if (accountDataStatus is Status.Success) {
            val accountData = accountDataStatus.value
            if (accountData!!.defaultAccount) {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setPositiveButton("Ok") { _, _ ->
                    }
                    .setTitle("No puedes deseleccionar tu cuenta por default")
                    .setMessage("Para hacer que una cuenta deje de ser tu cuenta por defecto, selecciona una nueva.")
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setPositiveButton("Aceptar") { _, _ ->

                        blockFavoriteButton = true
                        //marcar como favorito
                        lifecycleScope.launchWhenStarted {
                            accountDetailsViewModel.setAsFavorite(args.accountId)
                                .collectLatest { status ->
                                    if (status is Status.Loading) loadingViewModel.showLoadingDialog()
                                    else {
                                        loadingViewModel.hideLoadingDialog()
                                        if (status is Status.Error && status.error != "") Toast.makeText(
                                            requireContext(),
                                            status.error,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    blockFavoriteButton = false
                                }
                        }

                    }
                    .setNegativeButton("Cancelar") { _, _ ->

                    }
                    .setTitle("¿Deseas seleccionar esta cuenta como default?")
                    .setMessage("Al realizar esta acción, se desmarcará tu cuenta por default anterior.")
                    .show()
            }
        }
    }

    private fun setUpDatePicker() {

        if (!blockDatePicker) {

            blockDatePicker = true //evitar abrir muchos datePicker

            //mostrar date picker
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .build()

            dateRangePicker.addOnPositiveButtonClickListener {
                val startDate = Date(it.first)
                val endDate = Date(it.second)

                accountDetailsViewModel.addDateFilter(startDate, endDate)

            }

            dateRangePicker.addOnDismissListener {
                blockDatePicker = false //liberar uso de datepicker
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        }
    }

    private fun setUpChartDescriptionRecycler() {

        binding.recyclerViewAccountDetailsPieCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ChartDescriptionAdapter(pieData)
        }
    }

    private fun setUpPieChart() {
        val pie = PieChart(binding.pieChartAccountDetailsFragment)
        pie.showData(pieData.map { PieElement(it.color, it.amount, null) })
    }

    private fun setUpOperationsRecycler() {
        val context = this
        binding.recyclerViewAccountDetailsOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(operationsRecyclerData, context)
        }
    }

    override fun onItemClicked(operationData: OperationModel, position: Int) {
        val action =
            OperationDetailsFragmentDirections.actionToOperationDetails(operationData.localId!!)
        findNavController().navigate(action)
    }

    override fun onItemPressed(operationData: OperationModel, position: Int) {
        return
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addAccountOperationsToRecyclerView(status: Status<List<OperationModel>>) {
        if (status is Status.Success) {

            operationsRecyclerData.clear()
            operationsRecyclerData.addAll(status.value!!)

            binding.apply {
                recyclerViewAccountDetailsOperations.adapter?.notifyDataSetChanged()
                recyclerViewAccountDetailsOperations.visibility = View.VISIBLE
                textViewAccountDetailsFragmentOperationsNoContent.visibility = View.GONE
            }
        } else {
            if (status is Status.Error) println(status.error)
            binding.apply {
                recyclerViewAccountDetailsOperations.visibility = View.GONE
                textViewAccountDetailsFragmentOperationsNoContent.visibility = View.VISIBLE
            }
        }

    }

}