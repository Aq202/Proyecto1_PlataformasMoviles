package com.example.proyecto_final_apps.ui.fragments.transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.databinding.FragmentNewTransferBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NewTransferFragment : Fragment() {

    private lateinit var binding: FragmentNewTransferBinding

    private val transferViewModel: TransferViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private var originAccountSelectedIndex: Int? = null
    private var targetAccountSelectedIndex: Int? = null
    private lateinit var accountsList: List<AccountModel>

    private var blockAction = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()

        getInitialFragmentData()

    }

    private fun setListeners() {
        binding.apply {

            //origin account listener
            autoCompleteViewTransferFragmentOriginAccount.setOnItemClickListener { _, _, index, _ ->
                originAccountSelectedIndex = index;
            }

            //target account listener
            autoCompleteViewTransferFragmentTargetAccount.setOnItemClickListener { _, _, index, _ ->
                targetAccountSelectedIndex = index
            }

            //Send button
            buttonNewTransferFragmentDoneButton.setOnClickListener {
                sendForm()
            }

        }
    }


    private fun getInitialFragmentData() {
        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {
            transferViewModel.getAccountsList(false)
            loadingViewModel.hideLoadingDialog()
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            transferViewModel.accountsList.collectLatest { status ->
                handleAccountsList(status)
            }
        }
    }

    private fun handleAccountsList(status: Status<List<AccountModel>>) {

        if (status !is Status.Loading) binding.swipeRefreshLayoutTransferFragment.isRefreshing =
            false

        if (status is Status.Success) {

            accountsList = status.value
            setAccountDropList(accountsList)

            binding.apply {
                constraintLayoutTransferFragmentFragmentContainer.visibility = View.VISIBLE
                containerNoContentMessage.visibility = View.GONE
            }

        } else if (status is Status.Error) {
            binding.apply {
                constraintLayoutTransferFragmentFragmentContainer.visibility = View.VISIBLE
                containerNoContentMessage.visibility = View.GONE
            }
        }

    }

    private fun setAccountDropList(account: List<AccountModel>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            account.map { it.title })

        binding.autoCompleteViewTransferFragmentTargetAccount.setAdapter(adapter)
        binding.autoCompleteViewTransferFragmentOriginAccount.setAdapter(adapter)
    }


    private fun getAndValidateOriginAccount(amount: Double): AccountModel? {
        if (originAccountSelectedIndex != null) {
            val account = accountsList[originAccountSelectedIndex!!]

            if (account.total < amount) {
                binding.textInputNewTransferFragmentOriginAccount.error =
                    "La cuenta no tiene fondos suficientes."
                return null
            }
            binding.textInputNewTransferFragmentOriginAccount.error = null
            return account
        }
        binding.textInputNewTransferFragmentOriginAccount.error =
            "Debes seleccionar una cuenta de origen."
        return null
    }

    private fun getAndValidateTargetAccount(): AccountModel? {

        val index = targetAccountSelectedIndex

        if (index == null) {
            binding.textInputNewTransferFragmentTargetAccount.error =
                "Debes seleccionar una cuenta de destino."
            return null
        }

        if (originAccountSelectedIndex == index) {
            binding.textInputNewTransferFragmentTargetAccount.error =
                "Las cuentas de origen y destino no pueden ser iguales.."
            return null
        }

        //Formato correcto
        binding.textInputNewTransferFragmentTargetAccount.error = null
        return accountsList[index]


    }

    private fun getAndValidateAmount(): Double? {

        try {
            val amount =
                binding.textInputNewTransferFragmentAmount.editText!!.text.toString().toDouble()

            if (amount <= 0) {
                binding.textInputNewTransferFragmentAmount.error =
                    "El monto a transferir debe ser mayor a cero."
                return null
            }

            //Formato correcto
            binding.textInputNewTransferFragmentAmount.error = null
            return amount

        } catch (ex: NumberFormatException) {
            binding.textInputNewTransferFragmentAmount.error =
                "Debes ingresar la cantidad de la transferencia."
            return null
        }
    }

    private fun getDescription(): String? {
        val description = binding.textInputNewTransferFragmentDescription.editText!!.text.toString()
        return if (description.trim() != "") description else null
    }

    private fun sendForm() {
        if (blockAction) return

        //validar y obtener valores
        val amount = getAndValidateAmount() ?: return
        val originAccount = getAndValidateOriginAccount(amount) ?: return
        val targetAccount = getAndValidateTargetAccount() ?: return
        val description = getDescription()

        blockAction = true

        lifecycleScope.launchWhenStarted {
            transferViewModel.makeAccountsTransfer(
                originAccount.localId!!,
                targetAccount.localId!!,
                amount,
                description
            ).collectLatest { status ->

                if (status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if (status is Status.Success) {
                    //Navegar a detalles de operación
                    val action =
                        NewTransferFragmentDirections.actionToOperationDetails(status.value.localId!!)
                    findNavController().navigate(action)
                } else if (status is Status.Error) {
                    blockAction = false
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }

            }
        }

    }


}