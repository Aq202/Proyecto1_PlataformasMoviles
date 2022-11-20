package com.example.proyecto_final_apps.ui.fragments.debt_detail

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
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.DebtWithContactModel
import com.example.proyecto_final_apps.databinding.FragmentDebtDetailsBinding
import com.example.proyecto_final_apps.helpers.checkIfIsPreviousBackStackEntry
import com.example.proyecto_final_apps.helpers.twoDecimals
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DebtDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDebtDetailsBinding
    private val args: DebtDetailsFragmentArgs by navArgs()

    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val debtDetailViewModel: DebtDetailViewModel by viewModels()

    private var accountSelectedIndex: Int? = null
    private lateinit var accountsList: List<AccountModel>
    private lateinit var debtData: DebtWithContactModel

    private var blockFinalize = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDebtDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenStarted {
            debtDetailViewModel.getFragmentData(args.debtId)
        }

        setObservers()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            autoCompleteViewDebtDetailsFragmentOriginAccount.setOnItemClickListener { _, _, index, _ ->
                accountSelectedIndex = index;
            }

            buttonDebtDetailsFragmentForgetDebt.setOnClickListener {
                handleFinalizeDebt()
            }

            swipeToRefreshDebtDetailsFragment.setOnRefreshListener {

                lifecycleScope.launchWhenStarted {
                    debtDetailViewModel.getFragmentData(args.debtId, true)
                    binding.swipeToRefreshDebtDetailsFragment.isRefreshing = false
                }

            }

        }


    }


    private fun handleFinalizeDebt() {

        if (blockFinalize) return

        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("Finalizar") { _, _ ->

                blockFinalize = true

                //Finalizar deuda
                lifecycleScope.launchWhenStarted {
                    debtDetailViewModel.finalizeDebt(args.debtId).collectLatest { status ->
                        handleFinalizeActions(status)
                    }
                }

            }
            .setNegativeButton("Cancelar") { _, _ ->
                blockFinalize = false
            }
            .setTitle("¿Deseas finalizar esta deuda?")
            .setMessage("Toma en cuenta que no podrás recuperar el contenido de esta y que se realizará la operación correspondiente (ingreso/egreso) en tu cuenta de deudas sin reflejar modificaciones en tus otras cuentas.")
            .show()
    }

    private fun handleFinalizeActions(status: Status<Boolean>) {
        if (status is Status.Success) {


            if (findNavController().checkIfIsPreviousBackStackEntry(R.id.contactProfileFragment)) {
                findNavController().popBackStack() //Volver a la ventana de usuario
            } else {
                //Navegar a la ventana de usuario
                val action =
                    DebtDetailsFragmentDirections.actionDebtDetailsFragmentToContactProfileFragment(
                        debtData.contact.id
                    )
                findNavController().navigate(action)
            }
        }else if (status is Status.Error) blockFinalize = false

    }

    private fun setObservers() {

        //Escuchar el estado del fragment
        lifecycleScope.launchWhenStarted {
            debtDetailViewModel.fragmentState.collectLatest { status ->
                handleFragmentState(status)
            }
        }

        //Escuchar data de la deuda
        lifecycleScope.launchWhenStarted {
            debtDetailViewModel.debtData.collectLatest { status ->
                if (status is Status.Success) {
                    fillDebtData(status.value)
                    debtData = status.value
                } else if (status is Status.Error) {
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }


            }
        }

        //Agregar cuentas
        lifecycleScope.launchWhenStarted {
            debtDetailViewModel.accountList.collectLatest { status ->
                if (status is Status.Success) {

                    setAccountDropList(status.value)
                    accountsList = status.value

                    enableAccountFunctions()

                } else if (status is Status.Error) {
                    Toast.makeText(
                        requireContext(),
                        "No hay cuentas disponibles.",
                        Toast.LENGTH_LONG
                    ).show()

                    disableAccountFunctions()
                }
            }
        }
    }

    private fun enableAccountFunctions() {
        binding.apply {
            textInputPendingPaymentDetailsFragmentOriginAccount.isEnabled = true
            buttonDebtDetailsFragmentMarkAsCompleted.isEnabled = true
        }
    }

    private fun disableAccountFunctions() {
        binding.apply {
            textInputPendingPaymentDetailsFragmentOriginAccount.isEnabled = false
            buttonDebtDetailsFragmentMarkAsCompleted.isEnabled = false
        }
    }

    private fun setAccountDropList(accounts: List<AccountModel>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            accounts.map { it.title })
        binding.autoCompleteViewDebtDetailsFragmentOriginAccount.setAdapter(adapter)
    }

    private fun fillDebtData(debtData: DebtWithContactModel) {
        val contactName = "(${
            getString(
                R.string.alias_format,
                debtData.contact.alias
            )
        }) ${
            getString(
                R.string.fullName_template,
                debtData.contact.name,
                debtData.contact.lastName
            )
        }"
        val operationType =
            if (debtData.acceptedDebt.active) getString(R.string.ingreso) else getString(R.string.egreso)

        binding.apply {
            textViewDebtDetailsFragmentContact.text = contactName
            textViewDebtDetailsFragmentAmount.text =
                getString(R.string.money_format, debtData.acceptedDebt.amount.twoDecimals())
            textViewDebtDetailsFragmentType.text = operationType
            textViewDebtDetailsFragmentDescription.text =
                debtData.acceptedDebt.description ?: "Sin descripción."
        }

    }

    private fun handleFragmentState(status: Status<Boolean>) {

        if (status is Status.Loading) loadingViewModel.showLoadingDialog()
        else loadingViewModel.hideLoadingDialog()

        if (status is Status.Success) {
            binding.apply {
                nestedScrollViewDebtDetailFragmentFragmentContainer.visibility = View.VISIBLE
                containerNoResultsContent.visibility = View.GONE
            }
        } else if (status is Status.Error) {
            binding.apply {
                nestedScrollViewDebtDetailFragmentFragmentContainer.visibility = View.GONE
                containerNoResultsContent.visibility = View.VISIBLE
            }
        }
    }
}