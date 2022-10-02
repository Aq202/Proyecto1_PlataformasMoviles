package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.presentation.adapters.OperationAdapter
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment(R.layout.fragment_home), OperationAdapter.OperationListener {

    private lateinit var recyclerView_recentOperations:RecyclerView

    private lateinit var recentOperationsList: MutableList<Operation>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView_recentOperations = view.findViewById(R.id.recyclerView_homeFragment_recentOperations)

        setUpRecentOperationsRecycler()
    }

    private fun setUpRecentOperationsRecycler() {
        recentOperationsList = TestOperations(requireContext()).getOperations()
        recyclerView_recentOperations.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_recentOperations.setHasFixedSize(true)
        recyclerView_recentOperations.adapter = OperationAdapter(recentOperationsList,this)
    }

    override fun onItemClicked(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "CLICKED...", Toast.LENGTH_LONG).show()
    }

    override fun onItemPressed(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "PRESSED...", Toast.LENGTH_LONG).show()
    }


}