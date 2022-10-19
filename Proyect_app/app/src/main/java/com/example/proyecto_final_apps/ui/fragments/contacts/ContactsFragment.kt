package com.example.proyecto_final_apps.ui.fragments.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.data.Contact
import com.example.proyecto_final_apps.data.ContactModel
import com.example.proyecto_final_apps.databinding.FragmentContactsBinding
import com.example.proyecto_final_apps.helpers.Search
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.ToolbarViewModel
import com.example.proyecto_final_apps.ui.adapters.ContactAdapter
import kotlinx.coroutines.flow.collectLatest


class ContactsFragment : Fragment(), ContactAdapter.ContactListener {

    private lateinit var binding: FragmentContactsBinding
    private val toolbarViewModel: ToolbarViewModel by activityViewModels()
    private val bottomNavigationViewModel:BottomNavigationViewModel by activityViewModels()


    private lateinit var contactsList: MutableList<ContactModel>
    private lateinit var searchUsersList: MutableList<ContactModel>

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
    }
    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem(){
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.CONTACTS)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            toolbarViewModel.searchFlow.collectLatest { query ->

                //falsa busqueda
                contactsList.clear()
                searchUsersList.clear()
                contactsList.addAll(Contact.contacts)

                if (query != "") {
                    val nonFilteredItems = contactsList.filter { contact ->
                        Search.hasCoincidences(query, contact.name, contact.lastName, contact.alias, contact.name + " " + contact.lastName)
                    }
                    contactsList.retainAll(nonFilteredItems.filter { it.isAdded })
                    searchUsersList.addAll(nonFilteredItems.filter { !it.isAdded })


                    binding.recyclerViewProfileUserFragmentSearchUsers.adapter!!.notifyDataSetChanged()

                }else{
                    contactsList.removeAll(contactsList.filter { !it.isAdded })
                }

                binding.recyclerViewProfileUserFragmentContacts.adapter!!.notifyDataSetChanged()

            }
        }
    }

    private fun setUpContactsRecycler() {
        contactsList = mutableListOf()
        val context = this
        binding.recyclerViewProfileUserFragmentContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ContactAdapter(contactsList, context)
        }
    }

    private fun setUpSearchUsersRecycler() {
        searchUsersList = mutableListOf()
        val context = this
        binding.recyclerViewProfileUserFragmentSearchUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ContactAdapter(searchUsersList, context)
        }
    }

    override fun onItemClicked(contactData: ContactModel, position: Int) {

    }


}