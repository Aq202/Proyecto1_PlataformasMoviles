package com.example.proyecto_final_apps.ui.fragments.recent_operations

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.toOperationItem
import com.example.proyecto_final_apps.databinding.FragmentRecentOperationsBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem
import com.example.proyecto_final_apps.ui.fragments.operation_details.OperationDetailsFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RecentOperationsFragment : Fragment(R.layout.fragment_recent_operations), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentRecentOperationsBinding

    private val recentOperationsViewModel:RecentOperationsViewModel by viewModels()
    private val loadingViewModel:LoadingViewModel by activityViewModels()

    private val recentOperationsList = mutableListOf<OperationItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecentOperationsRecycler()
        setObservers()
        setListeners()

        recentOperationsViewModel.getRecentOperations(false)

    }

    private fun setListeners() {
        binding.apply {
            swipeRefreshLayoutRecentOperations.setOnRefreshListener {
                recentOperationsViewModel.getRecentOperations(true) //Forzar actualización
            }
        }
    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            recentOperationsViewModel.recentOperations.collectLatest { status ->

                fillFragmentData(status)

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillFragmentData(status: Status<List<OperationModel>>) {
        //Mostrar-ocultar loading
        if(status is Status.Default) loadingViewModel.showLoadingDialog()
        else loadingViewModel.hideLoadingDialog()

        if(!(status is Status.Loading)) binding.swipeRefreshLayoutRecentOperations.isRefreshing = false //Ocultar icono de recarga

        if(status is Status.Success){

            //agregar datos
            recentOperationsList.clear()
            recentOperationsList.addAll(status.value.map{it.toOperationItem(requireContext())})

            binding.apply {
                recyclerViewRecentOperationsFragmentRecentOperations.adapter!!.notifyDataSetChanged()
                recyclerViewRecentOperationsFragmentRecentOperations.visibility = View.VISIBLE
                containerNoResultsContent.visibility = View.GONE
            }

        }else if (status is Status.Error){
            binding.apply {
                recyclerViewRecentOperationsFragmentRecentOperations.visibility = View.GONE
                containerNoResultsContent.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpRecentOperationsRecycler() {
        val context = this
        binding.recyclerViewRecentOperationsFragmentRecentOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //enable scroll
            adapter = OperationAdapter(recentOperationsList, context)
        }
    }

    override fun onItemClicked(operationData: OperationItem, position: Int) {
        val action = OperationDetailsFragmentDirections.actionToOperationDetails(operationData.localId)
        findNavController().navigate(action)
    }

    override fun onItemPressed(operationData: OperationItem, position: Int) {
    }

}