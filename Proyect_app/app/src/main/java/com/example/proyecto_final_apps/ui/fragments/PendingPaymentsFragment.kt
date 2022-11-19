package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.databinding.FragmentPendingPaymentsBinding
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem

class PendingPaymentsFragment : Fragment(), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentPendingPaymentsBinding

    private lateinit var pendingPaymentsList: MutableList<OperationItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPendingPaymentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPendingPaymentsRecycler()
    }

    private fun setUpPendingPaymentsRecycler() {

        pendingPaymentsList = mutableListOf()

        val context = this
        binding.recyclerViewPendingPaymentsFragmentPendingPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false //disable scroll

            adapter = OperationAdapter(pendingPaymentsList, context)
        }
    }

    override fun onItemClicked(operationData: OperationItem, position: Int) {
        findNavController().navigate(R.id.action_toPendingPaymentDetailsFragment)
    }

    override fun onItemPressed(operationData: OperationItem, position: Int) {
        Toast.makeText(requireContext(), "PRESSED...", Toast.LENGTH_LONG).show()
    }
}