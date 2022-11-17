package com.example.proyecto_final_apps.ui.fragments.editOperation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
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
import com.example.proyecto_final_apps.databinding.FragmentEditOperationBinding
import com.example.proyecto_final_apps.helpers.addChip
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditOperationFragment : Fragment() {
    private lateinit var binding: FragmentEditOperationBinding
    private val editOperationViewModel: EditOperationViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private var accountsList : MutableList<AccountModel> = mutableListOf()
    private var selectedAccount: AccountModel? = null
    private val args: EditOperationFragmentArgs by navArgs()
    private val nullAccounts = "No hay cuentas disponibles"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditOperationBinding.inflate(inflater, container, false)
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
        }
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        editOperationViewModel.getOperationData(args.operationId, forceUpdate)
        editOperationViewModel.getAccountData(args.operationId,forceUpdate)
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            editOperationViewModel.accountData.collectLatest { status ->

                if (status is Status.Success) {
                    status.value?.let { account ->
                        editOperationViewModel.operationData.collectLatest { statusOp ->
                            if (statusOp is Status.Success) {
                                statusOp.value?.let { operation ->
                                    binding.apply {
                                        textInputLayoutEditOperationFragmentTitle.editText!!.setText(
                                            operation.title
                                        )
                                        textInputLayoutEditOperationFragmentSourceAccount.editText!!.setText(
                                            account.title
                                        )
                                        textInputLayoutEditOperationFragmentAmount.editText!!.setText(
                                            operation.amount.toString()
                                        )
                                        textInputLayoutEditOperationFragmentOperationType.editText!!.setText(
                                            if (operation.active) "Ingreso" else "Egreso"
                                        )
                                        textInputLayoutEditOperationFragmentDescription.editText!!.setText(
                                            operation.description ?: "Sin descripción"
                                        )
                                        chipGroupEditOperationFragmentCathegories.check(operation.category)
                                        checkBoxEditOperationFragmentFavoriteOperation.isChecked =
                                            operation.favorite
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

    private fun setDropLists() {
        val accounts = getAccountsNames()
        if(accounts.isEmpty()) accounts.add(nullAccounts)
        val operationTypes = listOf("Ingreso","Egreso")

        val adapterAccount = ArrayAdapter(requireContext(), R.layout.list_item, accounts)
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewEditOperationFragmentSourceAccount.setAdapter(adapterAccount)
        binding.autoCompleteViewEditOperationFragmentOperationType.setAdapter(adapterOperation)
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
        val chipGroup = binding.chipGroupEditOperationFragmentCathegories
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            updateChips(chipGroup, checkedIds[0])
        }
        binding.autoCompleteViewEditOperationFragmentSourceAccount.setOnItemClickListener { adapterView, view, i, l ->
            val account = adapterView.getItemAtPosition(i).toString()
            if(account != nullAccounts)
                selectedAccount = getAccount(account)!!
        }

        binding.autoCompleteViewEditOperationFragmentOperationType.setOnItemClickListener { adapterView, view, i, l ->

        }
    }

    private fun updateChips(chipGroup: ChipGroup, checkedChip: Int) {
        chipGroup.children.forEach { chip ->
            chip as Chip
            if(chip.id == checkedChip){
                chip.chipStrokeWidth = 1F
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
                binding.chipGroupEditOperationFragmentCathegories.addChip(
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