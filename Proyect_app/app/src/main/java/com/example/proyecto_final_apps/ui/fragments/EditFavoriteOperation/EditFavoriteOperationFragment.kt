package com.example.proyecto_final_apps.ui.fragments.EditFavoriteOperation

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.databinding.FragmentEditFavoriteOperationBinding
import com.example.proyecto_final_apps.helpers.addChip
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.fragments.accounts_list.AccountsListViewModel
import com.example.proyecto_final_apps.ui.fragments.editOperation.EditOperationFragmentDirections
import com.example.proyecto_final_apps.ui.fragments.editOperation.EditOperationViewModel
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException

@AndroidEntryPoint
class EditFavoriteOperationFragment : Fragment() {
    private lateinit var binding: FragmentEditFavoriteOperationBinding
    private val editFavoriteOperationViewModel: EditFavoriteOperationViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val accountListViewModel: AccountsListViewModel by viewModels()
    private var accountsList : MutableList<AccountModel> = mutableListOf()
    private var selectedAccount: AccountModel? = null
    private lateinit var checkedCathegory: String
    private val args: EditFavoriteOperationFragmentArgs by navArgs()
    private val nullAccounts = "No hay cuentas disponibles"
    private var operationImgUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditFavoriteOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateChipGroup()
        setListeners()
        setObservers()
        loadingViewModel.showLoadingDialog() //show loading
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            setDropLists()
            loadingViewModel.hideLoadingDialog() //hide loading
            editFavoriteOperationViewModel.setSuccessFragmentStatus()
        }
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        accountListViewModel.getAccountList(forceUpdate)
        editFavoriteOperationViewModel.getOperationData(args.localId, forceUpdate)
        editFavoriteOperationViewModel.getAccountData(args.localId,forceUpdate)
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            accountListViewModel.accountList.collectLatest { status ->
                addAccountsList(status)
            }
        }
        lifecycleScope.launchWhenStarted {
            editFavoriteOperationViewModel.fragmentState.collectLatest { status->
                if(status is Status.Success){
                    binding.swipeResfreshLayoutEditFavoriteOperationFragment.visibility = View.VISIBLE
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            editFavoriteOperationViewModel.accountData.collectLatest { status ->

                if (status is Status.Success) {
                    status.value?.let { account ->
                        editFavoriteOperationViewModel.operationData.collectLatest { statusOp ->
                            if (statusOp is Status.Success) {
                                statusOp.value?.let { operation ->
                                    operationImgUrl = operation.imgUrl
                                    selectedAccount = getAccount(account.title)
                                    binding.apply {
                                        textInputLayoutEditFavoriteOperationFragmentTitle.editText!!.setText(
                                            operation.title
                                        )
                                        textInputLayoutEditFavoriteOperationFragmentSourceAccount.editText!!.setText(
                                            account.title
                                        )
                                        textInputLayoutEditFavoriteOperationFragmentAmount.editText!!.setText(
                                            operation.amount.toString()
                                        )
                                        textInputLayoutEditFavoriteOperationFragmentOperationType.editText!!.setText(
                                            if (operation.active) "Ingreso" else "Egreso"
                                        )
                                        textInputLayoutEditFavoriteOperationFragmentDescription.editText!!.setText(
                                            operation.description
                                        )
                                        chipGroupEditFavoriteOperationFragmentCathegories.check(operation.category)
                                    }
                                }
                            } else if (statusOp is Status.Error) {
                                Toast.makeText(requireContext(), statusOp.error, Toast.LENGTH_LONG)
                                    .show()
                                //Navegar al home
                                val action = EditOperationFragmentDirections.actionToHome()
                                findNavController().navigate(action)
                            }
                        }
                    }

                } else if (status is Status.Error) {
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addAccountsList(status: Status<List<AccountModel>>) {
        if(status is Status.Success){

            accountsList.clear()
            accountsList.addAll(status.value!!)
            accountsList.sortBy { !it.defaultAccount }

        }else if(status is Status.Error){
            println("Diego: ${status.error}")
        }
    }

    private fun setDropLists() {
        val accounts = getAccountsNames()
        if(accounts.isEmpty()) accounts.add(nullAccounts)
        val operationTypes = listOf("Ingreso","Egreso")

        val adapterAccount = ArrayAdapter(requireContext(), R.layout.list_item, accounts)
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewEditFavoriteOperationFragmentSourceAccount.setAdapter(adapterAccount)
        binding.autoCompleteViewEditFavoriteOperationFragmentOperationType.setAdapter(adapterOperation)
    }

    private fun getAccountsNames(): MutableList<String> {
        var accounts = mutableListOf<String>()
        accountsList.forEach { account ->
            if(account.editable)
                accounts.add(account.title)
        }
        return accounts
    }

    private fun setListeners() {
        val chipGroup = binding.chipGroupEditFavoriteOperationFragmentCathegories
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            updateChips(chipGroup, checkedIds[0])
        }
        binding.autoCompleteViewEditFavoriteOperationFragmentSourceAccount.setOnItemClickListener { adapterView, view, i, l ->
            val account = adapterView.getItemAtPosition(i).toString()
            if(account != nullAccounts)
                selectedAccount = getAccount(account)!!
        }

        binding.buttonEditFavoriteOperationFragmentAdd.setOnClickListener{
            updateOperation()
        }

        binding.swipeResfreshLayoutEditFavoriteOperationFragment.setOnRefreshListener {
            println("REFRESHING...")
            lifecycleScope.launchWhenStarted {
                getFragmentData(true)
                binding.swipeResfreshLayoutEditFavoriteOperationFragment.isRefreshing = false
            }

        }
    }

    private fun getAccount(title: String): AccountModel? {
        accountsList.forEach { account ->
            if (account.title == title)
                return account
        }
        return null
    }

    private fun validateTitle(): Boolean {
        binding.apply {
            val name = textInputLayoutEditFavoriteOperationFragmentTitle.editText!!.text
            if (name.trim().isEmpty()) {
                textInputLayoutEditFavoriteOperationFragmentTitle.error =
                    "El título de la operación es obligatorio."
                return false
            }
            textInputLayoutEditFavoriteOperationFragmentTitle.error = null
            return true
        }
    }
    private fun validateAccount(): Boolean {
        binding.apply {
            val account = textInputLayoutEditFavoriteOperationFragmentSourceAccount.editText!!.text
            var amount = textInputLayoutEditFavoriteOperationFragmentAmount.editText!!.text.toString()
            amount = if(amount == "") "0" else amount
            val operationType = textInputLayoutEditFavoriteOperationFragmentOperationType.editText!!.text
            if (account.trim().isEmpty()) {
                textInputLayoutEditFavoriteOperationFragmentSourceAccount.error =
                    "Debe seleccionar una cuenta de origen."
                return false
            }else if(account.toString() == nullAccounts){
                textInputLayoutEditFavoriteOperationFragmentSourceAccount.error =
                    "Debe seleccionar una cuenta de origen."
                return false
            }else if (operationType.toString() != "Ingreso" &&
                selectedAccount!!.total < amount.toDouble()) {
                textInputLayoutEditFavoriteOperationFragmentSourceAccount.error =
                    "La cuenta seleccionada no tiene fondos suficientes para esta operación."
                return false
            }
            textInputLayoutEditFavoriteOperationFragmentSourceAccount.error = null
            return true
        }
    }
    private fun validateOperationType(): Boolean {
        binding.apply {
            val name = textInputLayoutEditFavoriteOperationFragmentOperationType.editText!!.text
            if (name.trim().isEmpty()) {
                textInputLayoutEditFavoriteOperationFragmentTitle.error =
                    "Debe especificar el tipo de operación."
                return false
            }
            textInputLayoutEditFavoriteOperationFragmentOperationType.error = null
            return true
        }
    }

    private fun validateAmount(): Boolean {
        binding.apply {
            try {
                val amount =
                    textInputLayoutEditFavoriteOperationFragmentAmount.editText!!.text.toString().toDouble()

                if (amount < 0) textInputLayoutEditFavoriteOperationFragmentAmount.error =
                    "El monto de la operación debe ser mayor a cero."
                else {
                    textInputLayoutEditFavoriteOperationFragmentAmount.error = null
                    return true
                }
            } catch (ex: NumberFormatException) {
                textInputLayoutEditFavoriteOperationFragmentAmount.error =
                    "El monto de la operación es obligatorio."
            }

        }

        return false
    }

    private fun validateCathegory(): Boolean{
        binding.apply {
            if (chipGroupEditFavoriteOperationFragmentCathegories.checkedChipIds.isEmpty()){
                textViewEditFavoriteOperationFragmentCathegory.error =
                    "Debe seleccionar una categoría."
                return false
            }else{
                textViewEditFavoriteOperationFragmentCathegory.error = null
                return true
            }
        }
    }

    private fun updateOperation() {
        if (!validateTitle() && !validateAmount() && !validateAccount() && !validateOperationType() && !validateCathegory()) return
        else if(!validateTitle() || !validateAmount() || !validateAccount() || !validateOperationType() || !validateCathegory()) return

        val title = binding.textInputLayoutEditFavoriteOperationFragmentTitle.editText!!.text.toString()
        val accountLocalId = selectedAccount!!.localId
        val amount = binding.textInputLayoutEditFavoriteOperationFragmentAmount.editText!!.text.toString().toDouble()
        val operationType = binding.textInputLayoutEditFavoriteOperationFragmentOperationType.editText!!.text.toString()
        val active = operationType == "Ingreso"
        val description = binding.textInputLayoutEditFavoriteOperationFragmentDescription.editText!!.text.toString()
        val favorite = true
        val category = Category(requireContext()).getId(checkedCathegory)

        lifecycleScope.launchWhenStarted {
            editFavoriteOperationViewModel.updateOperation(
                operationLocalId = args.localId,
                title = title,
                accountLocalId = accountLocalId!!,
                amount = amount,
                active = active,
                description = if (description != "") description else null,
                category = category,
                favorite = favorite,
                imgUrl = operationImgUrl
            ).collectLatest { status ->

                if(status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success){
                    println(status.value.localId!!)
                    val action = EditOperationFragmentDirections.actionToOperationDetails(status.value.localId!!)
                    findNavController().navigate(action)
                }else if(status is Status.Error && status.error.isNotEmpty())
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateChips(chipGroup: ChipGroup, checkedChip: Int) {
        chipGroup.children.forEach { chip ->
            chip as Chip
            if(chip.id == checkedChip){
                chip.chipStrokeWidth = 1F
                checkedCathegory = chip.text.toString()
            }
            else
                chip.chipStrokeWidth = 0F
        }
    }

    private fun generateChipGroup() {
        val categories = Category(requireContext()).getCategories()
        categories.forEach{ cathegory ->
            if(cathegory.name != "Deudas") {
                val backgroundCSL = generateCSL(cathegory.color, true)
                val strokeCSL = generateCSL(cathegory.color, false)
                binding.chipGroupEditFavoriteOperationFragmentCathegories.addChip(
                    requireContext(),
                    cathegory,
                    backgroundCSL,
                    strokeCSL
                )
            }
        }
    }

    private fun generateCSL(color: Int, default: Boolean): ColorStateList {
        val states =
            if(default)
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                )
            else
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )
        val colors = intArrayOf(
            if (default) ColorUtils.setAlphaComponent(getColor(requireContext(), R.color.white),0) else getColor(requireContext(), R.color.white),
            color
        )
        return ColorStateList(states, colors)
    }
}