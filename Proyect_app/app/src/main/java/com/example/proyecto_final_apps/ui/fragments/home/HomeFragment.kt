package com.example.proyecto_final_apps.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentHomeBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.UserSessionStatus
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.fragments.OperationDetailsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()

    private lateinit var recentOperationsList: MutableList<Operation>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecentOperationsRecycler()
        setUpPendingPaymentsRecycler()
        setListeners()
        setObservers()

        userViewModel.getUserData()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            userViewModel.userDataStateFlow.collectLatest { status ->

                when(status){
                    is UserSessionStatus.Logged -> {
                        binding.apply {
                            textViewHomeFragmentUserName.text = getString(R.string.fullName_template, status.data.name, status.data.lastName)
                        }
                    }
                    is UserSessionStatus.Error-> {
                        println("DIEGO: ${status.err}")
                    }
                    else -> {}
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }


    private class RecentOperationsListener(val navController:NavController):OperationAdapter.OperationListener{
        override fun onItemClicked(operationData: Operation, position: Int) {
            val action = OperationDetailsFragmentDirections.actionToOperationDetails(operationData.id)
            navController.navigate(action)
        }

        override fun onItemPressed(operationData: Operation, position: Int) {
        }

    }

    private class PendingPaymentsListener(val navController:NavController):OperationAdapter.OperationListener{
        override fun onItemClicked(operationData: Operation, position: Int) {
            navController.navigate(R.id.action_toPendingPaymentDetailsFragment)
        }

        override fun onItemPressed(operationData: Operation, position: Int) {

        }

    }


    private fun selectCurrentBottomNavigationItem(){
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setListeners() {
        binding.apply {
            textViewHomeFragmentSeeDetails.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_homeFragment_to_accountsListFragment)
            }

            imageViewHomeFragmentSeeAllRecentOperationIcon.setOnClickListener{
                findNavController().navigate(R.id.action_homeFragment_to_recentOperationsFragment)
            }

            imageViewHomeFragmentSeeAllPendingPaymentsIcon.setOnClickListener{
                findNavController().navigate(R.id.action_homeFragment_to_pendingPaymentsFragment)
            }
        }
    }



    private fun setUpRecentOperationsRecycler() {
        recentOperationsList =
            TestOperations(requireContext()).getOperations().take(3) as MutableList<Operation>

        binding.recyclerViewHomeFragmentRecentOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(recentOperationsList, RecentOperationsListener(findNavController()) )
        }
    }

    private fun setUpPendingPaymentsRecycler() {

        //obtener 3 primeras deudas
        recentOperationsList = TestOperations(requireContext()).getOperations().filter {
            it.category?.type == CategoryTypes.DEUDAS
        }.take(3) as MutableList<Operation>

        val context = this
        binding.recyclerViewHomeFragmentPendingPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(recentOperationsList, PendingPaymentsListener(findNavController()))
        }
    }



}