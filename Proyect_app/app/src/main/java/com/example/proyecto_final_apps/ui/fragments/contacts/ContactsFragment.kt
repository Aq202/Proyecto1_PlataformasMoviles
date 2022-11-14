package com.example.proyecto_final_apps.ui.fragments.contacts

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.databinding.FragmentContactsBinding
import com.example.proyecto_final_apps.helpers.Search
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.activity.ToolbarViewModel
import com.example.proyecto_final_apps.ui.adapters.ContactAdapter
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.Error


@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private val toolbarViewModel: ToolbarViewModel by activityViewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val contactViewModel: ContactsViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private val contactsList = mutableListOf<UserModel>()
    private val externalUsersList = mutableListOf<UserModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpContactsRecycler()
        setUpSearchUsersRecycler()
        setObservers()
        setListeners()

        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {

            loadFragmentData()
            loadingViewModel.hideLoadingDialog()
        }
    }

    private suspend fun loadFragmentData(forceUpdate: Boolean = false, query: String = "") {

        if (query.trim().isEmpty()) {
            contactViewModel.getContactsList(forceUpdate)
            contactViewModel.clearExternalUsers()
        } else
            contactViewModel.searchUser(query.trim())

    }

    private fun setListeners() {
        binding.apply {
            swipeRefreshLayoutContactsFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {

                    val searchQuery = toolbarViewModel.searchFlow.value
                    loadFragmentData(true, searchQuery)
                    binding.swipeRefreshLayoutContactsFragment.isRefreshing = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    override fun onStop() {
        super.onStop()
        //Limpiar la barra de busqueda
        toolbarViewModel.triggerSearchFlow("")
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.CONTACTS)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            toolbarViewModel.searchFlow.collectLatest { query ->

                //evento al buscar en toolbar
                lifecycleScope.launchWhenStarted {
                    loadFragmentData(true, query)

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            contactViewModel.contactsList.collectLatest { status ->

                when (status) {
                    is Status.Success -> addContactsList(status.value)
                    is Status.Error -> addNoContactsState()
                    else -> {}
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            contactViewModel.externalUserList.collectLatest { status ->
                when (status) {
                    is Status.Success -> addExternalUsersList(status.value)
                    is Status.Error -> addNoExternalUsersState()
                    else -> {}
                }
            }
        }
    }


    private fun addNoExternalUsersState() {
        showNoContentBanner()
        binding.apply {
            textViewProfileUserFragmentSearchUsersTitle.visibility = View.GONE
            recyclerViewProfileUserFragmentSearchUsers.visibility = View.GONE
        }
    }

    private fun addNoContactsState() {
        showNoContentBanner()
        binding.apply {
            textViewProfileUserFragmentContactsTitle.visibility = View.GONE
            recyclerViewProfileUserFragmentContacts.visibility = View.GONE
        }
    }

    private fun showNoContentBanner() {
        val contactsList = contactViewModel.contactsList.value
        val usersList = contactViewModel.externalUserList.value

        if (contactsList is Status.Error && usersList is Status.Error)
            binding.containerNoResultsContent.visibility = View.VISIBLE
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun addContactsList(userContacts: List<UserModel>) {

        contactsList.clear()
        contactsList.addAll(userContacts)
        binding.apply {
            recyclerViewProfileUserFragmentContacts.adapter!!.notifyDataSetChanged()

            //Mostrar recycler y ocultar banner no-content
            textViewProfileUserFragmentContactsTitle.visibility = View.VISIBLE
            recyclerViewProfileUserFragmentContacts.visibility = View.VISIBLE
            containerNoResultsContent.visibility = View.GONE

            //ocultar divider
            val contactsData = contactViewModel.contactsList.value
            if (contactsData is Status.Success) dividerContactsFragment.visibility = View.GONE
            else dividerContactsFragment.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addExternalUsersList(users: List<UserModel>) {
        externalUsersList.clear()
        externalUsersList.addAll(users)
        binding.apply {
            recyclerViewProfileUserFragmentSearchUsers.adapter!!.notifyDataSetChanged()

            //Mostrar recycler y ocultar banner no-content
            textViewProfileUserFragmentSearchUsersTitle.visibility = View.VISIBLE
            recyclerViewProfileUserFragmentSearchUsers.visibility = View.VISIBLE
            containerNoResultsContent.visibility = View.GONE
        }
    }


    private fun setUpContactsRecycler() {


        val listener = ContactItemListener(requireView())

        binding.recyclerViewProfileUserFragmentContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ContactAdapter(contactsList, listener)
        }
    }


    private fun setUpSearchUsersRecycler() {
        val listener = ExternalUserItemListener(requireView())
        binding.recyclerViewProfileUserFragmentSearchUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ContactAdapter(externalUsersList, listener)
        }
    }


    private class ContactItemListener(val view: View) : ContactAdapter.ContactListener {
        override fun onItemClicked(contactData: UserModel, position: Int) {
            val action = ContactsFragmentDirections.actionContactsFragmentToContactProfileFragment(
                contactData.id
            )
            view.findNavController().navigate(action)
        }

    }


    private class ExternalUserItemListener(val view: View) : ContactAdapter.ContactListener {
        override fun onItemClicked(contactData: UserModel, position: Int) {
            val action =
                ContactsFragmentDirections.actionContactsFragmentToExternalUserProfileFragment(
                    contactData.id
                )
            view.findNavController().navigate(action)
        }

    }


}