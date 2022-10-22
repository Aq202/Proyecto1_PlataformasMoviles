package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentRecentOperationsBinding
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter

class RecentOperationsFragment : Fragment(R.layout.fragment_recent_operations), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentRecentOperationsBinding

    private lateinit var recentOperationsList: MutableList<Operation>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecentOperationsRecycler()
    }

    private fun setUpRecentOperationsRecycler() {
        recentOperationsList =
            TestOperations(requireContext()).getOperations().take(10) as MutableList<Operation>
        val context = this
        binding.recyclerViewRecentOperationsFragmentRecentOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = true  //enable scroll

            adapter = OperationAdapter(recentOperationsList, context)
        }
    }

    override fun onItemClicked(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "CLICKED...", Toast.LENGTH_LONG).show()
    }

    override fun onItemPressed(operationData: Operation, position: Int) {
        Toast.makeText(requireContext(), "PRESSED...", Toast.LENGTH_LONG).show()
    }

}