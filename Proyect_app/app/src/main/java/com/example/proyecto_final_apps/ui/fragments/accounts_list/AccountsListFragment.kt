package com.example.proyecto_final_apps.ui.fragments.accounts_list

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.*
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.databinding.FragmentAccountsListBinding
import com.example.proyecto_final_apps.helpers.getDecimal
import com.example.proyecto_final_apps.helpers.twoDigits
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.AccountAdapter
import com.example.proyecto_final_apps.ui.dialogs.LoadingDialog
import com.example.proyecto_final_apps.ui.fragments.account_details.AccountDetailsFragment
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

@AndroidEntryPoint
class AccountsListFragment : Fragment(), AccountAdapter.AccountListener {

    private lateinit var binding: FragmentAccountsListBinding
    private var accountsList: MutableList<AccountModel> = mutableListOf()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val accountListViewModel: AccountsListViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycler()
        setListeners()
        setObservers()

        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            loadingViewModel.hideLoadingDialog()
        }
    }


    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun setObservers() {

        //Añadir lista de cuentas
        lifecycleScope.launchWhenStarted {
            accountListViewModel.accountList.collectLatest { status ->
                addAccountsList(status)
            }
        }

        lifecycleScope.launchWhenStarted {
            accountListViewModel.balanceData.collectLatest { status ->
                addBalanceData(status)
            }
        }

        //Manejar estados del fragment
        lifecycleScope.launchWhenStarted {
            accountListViewModel.fragmentState.collectLatest { status ->
                if (status is Status.Success) {
                    binding.apply {
                        coordinatorLayoutAccountsListFragmentFragmentContainer.visibility =
                            View.VISIBLE
                        containerErrorFragmentMessageContent.visibility = View.GONE
                    }
                } else if (status is Status.Error) {
                    binding.apply {
                        coordinatorLayoutAccountsListFragmentFragmentContainer.visibility =
                            View.GONE
                        containerErrorFragmentMessageContent.visibility = View.VISIBLE
                    }
                } else if (status is Status.Loading) {
                    binding.apply {
                        coordinatorLayoutAccountsListFragmentFragmentContainer.visibility =
                            View.GONE
                        containerErrorFragmentMessageContent.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun addBalanceData(status: Status<Pair<Double, Double>>) {
        if (status is Status.Success) {

            val generalBalance = status.value!!.first
            val balanceMovement = status.value!!.second

            binding.apply {

                //add values
                textViewAccountDetailsFragmentAccountBalance.text =
                    getString(R.string.money_format, generalBalance.toInt().twoDigits())
                textViewAccountDetailsFragmentAccountlBalanceCents.text =
                    getString(R.string.cents_format, generalBalance.getDecimal(2).twoDigits())
                textViewAccountDetailsFragmentAccountMovement.text =
                    getString(R.string.money_format, abs(balanceMovement).toInt().twoDigits())
                textViewAccountDetailsFragmentAccountMovementCents.text =
                    getString(R.string.cents_format, balanceMovement.getDecimal(2).twoDigits())

                //change movement text color
                if (balanceMovement >= 0) {
                    textViewAccountDetailsFragmentAccountMovement.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.light_green_2)
                    )
                    textViewAccountDetailsFragmentAccountMovementCents.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.light_green_2)
                    )
                } else {
                    textViewAccountDetailsFragmentAccountMovement.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                    textViewAccountDetailsFragmentAccountMovementCents.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                }
            }
        } else if (status is Status.Error) {
            println("Diego: ${status.error}")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addAccountsList(status: Status<List<AccountModel>>) {
        if (status is Status.Success) {

            accountsList.clear()
            accountsList.addAll(status.value!!)
            accountsList.sortBy { !it.defaultAccount }

            binding.apply {

                recyclerViewAccountsFragment.adapter?.notifyDataSetChanged()

                //Mostrar lista y ocultar mensaje de no hay cuentas
                if (accountsList.isNotEmpty()) {
                    nestedScrollViewAccountsLitFragmentAccountsContentFragment.visibility =
                        View.VISIBLE
                    containerNoAccountResults.visibility = View.GONE

                    return
                }

            }

        } else if (status is Status.Error)
            println("Diego: ${status.error}")

        //Si hay error o si está vacía la lista: mostrar mensaje de no hay cuentas
        binding.apply {
            nestedScrollViewAccountsLitFragmentAccountsContentFragment.visibility =
                View.GONE //ocultar contenido
            containerNoAccountResults.visibility = View.VISIBLE

        }


    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        accountListViewModel.getAccountList(forceUpdate)
        accountListViewModel.getGeneralDescription()
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setListeners() {
        binding.apply {
            fabAccountsListFragmentCreateAccount.setOnClickListener {
                findNavController().navigate(R.id.action_accountsListFragment_to_newAccountFragment)
            }

            swipeResfreshLayoutListAccountsFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {
                    getFragmentData(true)
                    binding.swipeResfreshLayoutListAccountsFragment.isRefreshing = false
                }
            }
        }
    }

    private fun setUpRecycler() {

        val context = this
        binding.recyclerViewAccountsFragment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = AccountAdapter(accountsList, context)
        }
    }

    override fun onItemClicked(operationData: AccountModel, position: Int) {
        val action =
            AccountsListFragmentDirections.actionAccountsListFragmentToAccountDetailsFragment(
                accountId = operationData.localId!!
            )
        findNavController().navigate(action)
    }

    override fun onItemPressed(operationData: AccountModel, position: Int) {

    }
}