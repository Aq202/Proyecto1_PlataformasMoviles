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
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.databinding.FragmentOperationDetailsBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OperationDetailsFragment : Fragment() {

    private lateinit var binding:FragmentOperationDetailsBinding
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val operationDetailsViewModel: OperationDetailsViewModel by viewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val args: OperationDetailsFragmentArgs by navArgs()

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
                                        textViewOperationDetailsFragmentOriginAccountName.text = account.title
                                        textViewOperationDetailsFragmentAmount.text = "Q${operation.amount.toString()}"
                                        textViewOperationDetailsFragmentOperationType.text = if (operation.active) "Ingreso" else "Egreso"
                                        textViewOperationDetailsFragmentDescription.text = operation.description ?: "Sin descripción"
                                        textViewOperationDetailsFragmentCategory.text = Category(requireContext()).getCategory(operation.category)?.name
                                        textViewOperationDetailsFragmentDate.text = operation.date
                                    }
                                }
                            } else if (statusOp is Status.Error) {
                                Toast.makeText(requireContext(), statusOp.error, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                }else if (status is Status.Error){
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        operationDetailsViewModel.getOperationData(args.operationId, forceUpdate)
        operationDetailsViewModel.getAccountData(args.operationId,forceUpdate)
    }

    private fun setListeners() {
        binding.buttonOperationDetailsFragmentEdit.setOnClickListener{
        }
    }

}