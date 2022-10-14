package com.example.proyecto_final_apps.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentAccountDetailsBinding
import com.example.proyecto_final_apps.presentation.Components.PieChart
import com.example.proyecto_final_apps.presentation.adapters.AccountAdapter
import com.example.proyecto_final_apps.presentation.adapters.ChartDescriptionAdapter
import com.example.proyecto_final_apps.presentation.adapters.OperationAdapter
import kotlin.random.Random


class AccountDetailsFragment : Fragment(), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentAccountDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPieChart()
        setUpChartDescriptionRecycler()
        setUpOperationsRecycler()
    }

    private fun setUpChartDescriptionRecycler() {

        val data: MutableList<ChartDescriptionAdapter.DescriptionItem> = mutableListOf()
        val categories = Category(requireContext()).getCategories()

        categories.forEach {
            data.add(
                ChartDescriptionAdapter.DescriptionItem(
                    it.color,
                    it.name,
                    Math.random() * 100
                )
            )
        }

        binding.recyclerViewAccountDetailsPieCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll
            adapter = ChartDescriptionAdapter(data)
        }
    }

    private fun setUpPieChart() {

        val categories = Category(requireContext()).getCategories()
        val pie = PieChart(binding.pieChartAccountDetailsFragment)

        val data: MutableList<PieChart.PieElement> = mutableListOf()
        categories.forEach {
            data.add(PieChart.PieElement(it.color, Math.random() * 100, null))
        }

        pie.showData(data)
    }

    private fun setUpOperationsRecycler() {
        val data =
            TestOperations(requireContext()).getOperations()

        val context = this
        binding.recyclerViewAccountDetailsOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(data, context)
        }
    }

    override fun onItemClicked(operationData: Operation, position: Int) {
        return
    }

    override fun onItemPressed(operationData: Operation, position: Int) {
        return
    }

}