package com.example.proyecto_final_apps.ui.fragments.newOperation

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
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.databinding.FragmentNewOperationBinding
import com.example.proyecto_final_apps.helpers.addChip
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.fragments.TabLayoutFragmentDirections
import com.example.proyecto_final_apps.ui.fragments.accounts_list.AccountsListViewModel
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException

@AndroidEntryPoint
class NewOperationFragment : Fragment() {
    private lateinit var binding: FragmentNewOperationBinding
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private var accountsList : MutableList<AccountModel> = mutableListOf()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val newOperationViewModel: NewOperationViewModel by viewModels()
    private val accountListViewModel: AccountsListViewModel by viewModels()
    private lateinit var checkedCathegory: String
    private var selectedAccount: AccountModel? = null
    private val nullAccounts = "No hay cuentas disponibles"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.NEW)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateChipGroup()
        setListeners()
        setObservers()
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            setDropLists()
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            accountListViewModel.accountList.collectLatest { status ->
                addAccountsList(status)
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
    private suspend fun getFragmentData(forceUpdate:Boolean = false) {
        accountListViewModel.getAccountList(forceUpdate)
    }

    private fun setDropLists() {
        val accounts = getAccountsNames()
        if(accounts.isEmpty()) accounts.add(nullAccounts)
        val operationTypes = listOf("Ingreso","Egreso")

        val adapterAccount = ArrayAdapter(requireContext(), R.layout.list_item, accounts)
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewNewOperationFragmentSourceAccount.setAdapter(adapterAccount)
        binding.autoCompleteViewNewOperationFragmentOperationType.setAdapter(adapterOperation)
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
        val chipGroup = binding.chipGroupNewOperationFragmentCathegories
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            updateChips(chipGroup, checkedIds[0])
        }
        binding.autoCompleteViewNewOperationFragmentSourceAccount.setOnItemClickListener { adapterView, view, i, l ->
            val account = adapterView.getItemAtPosition(i).toString()
            if(account != nullAccounts)
                selectedAccount = getAccount(account)!!
        }
        binding.buttonNewOperationFragmentAdd.setOnClickListener{
            createOperation()
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
            val name = textInputLayoutNewOperationFragmentTitle.editText!!.text
            if (name.trim().isEmpty()) {
                textInputLayoutNewOperationFragmentTitle.error =
                    "El título de la operación es obligatorio."
                return false
            }
            textInputLayoutNewOperationFragmentTitle.error = null
            return true
        }
    }
    private fun validateAccount(): Boolean {
        binding.apply {
            val account = textInputLayoutNewOperationFragmentSourceAccount.editText!!.text
            val amount = textInputLayoutNewOperationFragmentAmount.editText!!.text
            val operationType = textInputLayoutNewOperationFragmentOperationType.editText!!.text
            if (account.trim().isEmpty()) {
                textInputLayoutNewOperationFragmentSourceAccount.error =
                    "Debe seleccionar una cuenta de origen."
                return false
            }else if(account.toString() == nullAccounts){
                textInputLayoutNewOperationFragmentSourceAccount.error =
                    "Debe seleccionar una cuenta de origen."
                return false
            }else if (operationType.toString() != "Ingreso" &&
                selectedAccount!!.total < amount.toString().toDouble()) {
                textInputLayoutNewOperationFragmentSourceAccount.error =
                    "La cuenta seleccionada no tiene fondos suficientes para esta operación."
                return false
            }
            textInputLayoutNewOperationFragmentSourceAccount.error = null
            return true
        }
    }
    private fun validateOperationType(): Boolean {
        binding.apply {
            val name = textInputLayoutNewOperationFragmentOperationType.editText!!.text
            if (name.trim().isEmpty()) {
                textInputLayoutNewOperationFragmentOperationType.error =
                    "Debe especificar el tipo de operación."
                return false
            }
            textInputLayoutNewOperationFragmentOperationType.error = null
            return true
        }
    }

    private fun validateAmount(): Boolean {
        binding.apply {
            try {
                val amount =
                    textInputLayoutNewOperationFragmentAmount.editText!!.text.toString().toDouble()

                if (amount < 0) textInputLayoutNewOperationFragmentAmount.error =
                    "El monto de la operación debe ser mayor a cero."
                else {
                    textInputLayoutNewOperationFragmentAmount.error = null
                    return true
                }
            } catch (ex: NumberFormatException) {
                textInputLayoutNewOperationFragmentAmount.error =
                    "El monto de la operación es obligatorio."
            }

        }

        return false
    }

    private fun validateCathegory(): Boolean{
        binding.apply {
            if (chipGroupNewOperationFragmentCathegories.checkedChipIds.isEmpty()){
                textViewNeOperationFragmentCathegory.error =
                    "Debe seleccionar una categoría."
                return false
            }else{
                textViewNeOperationFragmentCathegory.error = null
                return true
            }
        }
    }

    private fun createOperation() {
        if (!validateTitle() && !validateAmount() && !validateAccount() && !validateOperationType() && !validateCathegory()) return
        else if(!validateTitle() || !validateAmount() || !validateAccount() || !validateOperationType() || !validateCathegory()) return

        val title = binding.textInputLayoutNewOperationFragmentTitle.editText!!.text.toString()
        val accountRemoteId = selectedAccount!!.remoteId
        val accountLocalId = selectedAccount!!.localId
        val amount = binding.textInputLayoutNewOperationFragmentAmount.editText!!.text.toString().toDouble()
        val operationType = binding.textInputLayoutNewOperationFragmentOperationType.editText!!.text.toString()
        val active = operationType == "Ingreso"
        val description = binding.textInputLayoutNewOperationFragmentDescription.editText!!.text.toString()
        val favorite = binding.checkBoxNewOperationFragmentFavoriteOperation.isChecked
        val category = Category(requireContext()).getId(checkedCathegory)

        lifecycleScope.launchWhenStarted {
            newOperationViewModel.createOperation(
                title = title,
                accountRemoteId = accountRemoteId!!,
                accountLocalId = accountLocalId!!,
                amount = amount,
                active = active,
                description = if (description != "") description else null,
                category = category,
                favorite = favorite
            ).collectLatest { status ->

                if(status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success && status.value != null){
                    System.out.println(status.value.localId!!)
                    val action = TabLayoutFragmentDirections.actionTabLayoutFragmentToOperationDetailsFragment(status.value.localId!!)
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
                binding.chipGroupNewOperationFragmentCathegories.addChip(
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