package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentPendingPaymentsBinding
import com.example.proyecto_final_apps.presentation.adapters.OperationAdapter

class PendingPaymentsFragment : Fragment(R.layout.fragment_pending_payments), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentPendingPaymentsBinding

    private lateinit var pendingPaymentsList: MutableList<Operation>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPendingPaymentsRecycler()
    }

    private fun setUpPendingPaymentsRecycler() {

        pendingPaymentsList = TestOperations(requireContext()).getOperations().filter {
            it.category?.type == CategoryTypes.DEUDAS
        } as MutableList<Operation>

        val context = this
        binding.recyclerViewPendingPaymentsFragmentPendingPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = true  //enable scroll

            adapter = OperationAdapter(pendingPaymentsList, context)
        }
    }

    override fun onItemClicked(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "CLICKED...", Toast.LENGTH_LONG).show()
    }

    override fun onItemPressed(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "PRESSED...", Toast.LENGTH_LONG).show()
    }
}