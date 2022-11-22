package com.example.proyecto_final_apps.ui.fragments.operation_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.databinding.FragmentOperationDetailsBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.fragments.editAccount.EditAccountFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OperationDetailsFragment : Fragment() {

    private lateinit var binding:FragmentOperationDetailsBinding
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val operationDetailsViewModel: OperationDetailsViewModel by viewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val args: OperationDetailsFragmentArgs by navArgs()

    private var blockDeleteButton = false
    private var blockEditButton = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
        loadingViewModel.showLoadingDialog() //show loading
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            loadingViewModel.hideLoadingDialog() //hide loading
            operationDetailsViewModel.setSuccessFragmentStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            operationDetailsViewModel.accountData.collectLatest { status ->

                if(status is Status.Success){
                    status.value?.let { account ->
                        operationDetailsViewModel.operationData.collectLatest { statusOp ->
                            if (statusOp is Status.Success) {
                                statusOp.value?.let { operation ->
                                    binding.apply {
                                        textViewOperationDetailsFragmentTitleName.text = operation.title
                                        textViewOperationDetailsFragmentOriginAccountName.text = account.title
                                        textViewOperationDetailsFragmentAmount.text = "Q${operation.amount.toString()}"
                                        textViewOperationDetailsFragmentOperationType.text = if (operation.active) "Ingreso" else "Egreso"
                                        textViewOperationDetailsFragmentDescription.text = operation.description ?: "Sin descripción"
                                        textViewOperationDetailsFragmentCategory.text = Category(requireContext()).getCategory(operation.category)?.name
                                        textViewOperationDetailsFragmentDate.text = operation.date

                                        //Si es una operación de tipo deuda, bloquear botón de editar
                                        if(operation.category == Category(requireContext()).getDebtsCategory()?.id)
                                            buttonOperationDetailsFragmentEdit.isEnabled = false
                                    }
                                }
                            } else if (statusOp is Status.Error) {
                                Toast.makeText(requireContext(), statusOp.error, Toast.LENGTH_LONG)
                                    .show()
                                //Navegar al home
                                val action = OperationDetailsFragmentDirections.actionToHome()
                                findNavController().navigate(action)
                            }
                        }
                    }

                }else if (status is Status.Error){
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }

            }
        }
        lifecycleScope.launchWhenStarted {
            operationDetailsViewModel.fragmentState.collectLatest { status->
                if(status is Status.Success){
                    binding.swipeResfreshLayoutOperationDetailsFragment.visibility = View.VISIBLE
                }
            }
        }
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        operationDetailsViewModel.getOperationData(args.operationId, forceUpdate)
        operationDetailsViewModel.getAccountData(args.operationId,forceUpdate)
    }

    private fun setListeners() {
        binding.apply {
            buttonOperationDetailsFragmentDelete.setOnClickListener {
                deleteOperationAction()
            }

            buttonOperationDetailsFragmentEdit.setOnClickListener {
                editOperationPressed()
            }

            swipeResfreshLayoutOperationDetailsFragment.setOnRefreshListener {
                println("REFRESHING...")
                lifecycleScope.launchWhenStarted {
                    getFragmentData(true)
                    binding.swipeResfreshLayoutOperationDetailsFragment.isRefreshing = false
                }

            }
        }
    }

    private fun editOperationPressed() {
        val action = OperationDetailsFragmentDirections.actionOperationDetailsFragmentToEditOperationFragment(args.operationId)
        findNavController().navigate(action)
    }


    private fun deleteOperationAction() {
        if (blockDeleteButton) return

        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("Aceptar") { _, _ ->

                blockDeleteButton = true

                lifecycleScope.launchWhenStarted {
                    operationDetailsViewModel.deleteOperation(args.operationId).collectLatest { status ->
                        when (status) {
                            is Status.Loading -> loadingViewModel.showLoadingDialog()
                            is Status.Success -> {
                                loadingViewModel.hideLoadingDialog()
                                val action = OperationDetailsFragmentDirections.actionToHome()
                                findNavController().navigate(action)
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
            .setTitle("¿Deseas eliminar esta operación?")
            .setMessage("Toma en cuenta que esta acción es permanente y no podrás recuperar la información de esta operación.")
            .show()
    }

}