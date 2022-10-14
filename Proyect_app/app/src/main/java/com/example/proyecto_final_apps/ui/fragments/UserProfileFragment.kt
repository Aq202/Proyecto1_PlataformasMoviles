package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.data.Contact
import com.example.proyecto_final_apps.data.ContactModel
import com.example.proyecto_final_apps.databinding.FragmentUserProfileBinding
import com.example.proyecto_final_apps.ui.adapters.ContactAdapter

class UserProfileFragment : Fragment(), ContactAdapter.ContactListener {

    private lateinit var binding:FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpContactsRecycler()
    }

    private fun setUpContactsRecycler() {
        val contacts = Contact.contacts
        val context = this
        binding.recyclerViewProfileUserFragmentContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = ContactAdapter(contacts as MutableList<ContactModel>, context)
        }
    }

    override fun onItemClicked(contactData: ContactModel, position: Int) {

    }
}