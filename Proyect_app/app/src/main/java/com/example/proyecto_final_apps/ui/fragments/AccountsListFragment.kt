package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.*
import com.example.proyecto_final_apps.databinding.FragmentAccountsListBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.adapters.AccountAdapter

class AccountsListFragment : Fragment(), AccountAdapter.AccountListener {

    private lateinit var binding: FragmentAccountsListBinding
    private lateinit var accountsList : MutableList<AccountModel>
    private val bottomNavigationViewModel:BottomNavigationViewModel by activityViewModels()

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
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem(){
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    private fun setListeners() {
        binding.apply {
            fabAccountsListFragmentCreateAccount.setOnClickListener{
                findNavController().navigate(R.id.action_accountsListFragment_to_newAccountFragment)
            }
        }
    }

    private fun setUpRecycler() {

        accountsList = AccountData.accounts

        accountsList.sortBy { !it.default } //ordenar primero a la cuenta default

        val context = this
        binding.recyclerViewAccountsFragment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = AccountAdapter(accountsList, context)
        }
    }

    override fun onItemClicked(operationData: AccountModel, position: Int) {
        findNavController().navigate(R.id.action_accountsListFragment_to_accountDetailsFragment)
    }

    override fun onItemPressed(operationData: AccountModel, position: Int) {

    }
}