package com.example.proyecto_final_apps.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.databinding.FragmentHomeBinding
import com.example.proyecto_final_apps.helpers.twoDecimals
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.activity.UserSessionStatus
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.dialogs.LoadingDialog
import com.example.proyecto_final_apps.ui.fragments.operation_details.OperationDetailsFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private var recentOperationsList: MutableList<OperationModel> = mutableListOf()
    private var pendingOperationsList: MutableList<OperationModel> = mutableListOf()


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

        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {

            loadFragmentData() //load data
            loadingViewModel.hideLoadingDialog()

        }



    }

    private suspend fun loadFragmentData(forceUpdate: Boolean = false) {
        homeViewModel.getRecentOperations(forceUpdate)
        homeViewModel.getGeneralBalance()

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            userViewModel.userDataStateFlow.collectLatest { status ->

                when (status) {
                    is UserSessionStatus.Logged -> {
                        binding.apply {
                            textViewHomeFragmentUserName.text = getString(
                                R.string.fullName_template,
                                status.data.name,
                                status.data.lastName
                            )
                        }
                    }
                    else -> {
                    }
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.generalBalance.collectLatest { status ->
                showGeneralBalance(status)
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.recentOperations.collectLatest { status ->
                addRecentOperationsData(status)
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.pendingOperations.collectLatest { status ->
                addPendingOperationsData(status)
            }
        }



    }


    private fun addPendingOperationsData(status: Status<List<OperationModel>>) {
        when (status) {
            is Status.Success -> {


            }
            else -> {
                binding.apply {
                    recyclerViewHomeFragmentPendingPayments.visibility = View.GONE
                    textViewHomeFragmentPendingPaymentsNoContent.visibility = View.VISIBLE
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addRecentOperationsData(status: Status<List<OperationModel>>) {
        when (status) {
            is Status.Success -> {
                recentOperationsList.clear()
                recentOperationsList.addAll(status.value!!)

                binding.apply {
                    recyclerViewHomeFragmentRecentOperations.adapter!!.notifyDataSetChanged()
                    recyclerViewHomeFragmentRecentOperations.visibility = View.VISIBLE
                    textViewHomeFragmentRecentOperationsNoContent.visibility = View.GONE
                }

            }
            else -> {
                binding.apply {
                    recyclerViewHomeFragmentRecentOperations.visibility = View.GONE
                    textViewHomeFragmentRecentOperationsNoContent.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showGeneralBalance(status: Status<Double>) {
        if (status is Status.Success) {
            val balance = status.value
            if (balance!! >= 0.0)
                binding.textViewHomeFragmentGeneralBalanceAmount.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.light_green_2)
                )
            else
                binding.textViewHomeFragmentGeneralBalanceAmount.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )

            binding.textViewHomeFragmentGeneralBalanceAmount.text =
                getString(R.string.money_format, abs(balance).twoDecimals())


        }


    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }


    private class RecentOperationsListener(val navController: NavController) :
        OperationAdapter.OperationListener {
        override fun onItemClicked(operationData: OperationModel, position: Int) {
            val action =
                OperationDetailsFragmentDirections.actionToOperationDetails(operationData.localId!!)
            navController.navigate(action)
        }

        override fun onItemPressed(operationData: OperationModel, position: Int) {
        }

    }

    private class PendingPaymentsListener(val navController: NavController) :
        OperationAdapter.OperationListener {
        override fun onItemClicked(operationData: OperationModel, position: Int) {
            navController.navigate(R.id.action_toPendingPaymentDetailsFragment)
        }

        override fun onItemPressed(operationData: OperationModel, position: Int) {

        }

    }


    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setListeners() {
        binding.apply {
            textViewHomeFragmentSeeDetails.setOnClickListener {
                requireView().findNavController()
                    .navigate(R.id.action_homeFragment_to_accountsListFragment)
            }

            imageViewHomeFragmentSeeAllRecentOperationIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_recentOperationsFragment)
            }

            imageViewHomeFragmentSeeAllPendingPaymentsIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_pendingPaymentsFragment)
            }
            swipeResfreshLayoutHomeFragment.setOnRefreshListener {
                println("REFRESHING...")
                lifecycleScope.launchWhenStarted {
                    loadFragmentData(true)
                    binding.swipeResfreshLayoutHomeFragment.isRefreshing = false
                }

            }

        }
    }


    private fun setUpRecentOperationsRecycler() {

        binding.recyclerViewHomeFragmentRecentOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(
                recentOperationsList,
                RecentOperationsListener(findNavController())
            )
        }
    }

    private fun setUpPendingPaymentsRecycler() {

        binding.recyclerViewHomeFragmentPendingPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter =
                OperationAdapter(pendingOperationsList, PendingPaymentsListener(findNavController()))
        }

    }


}