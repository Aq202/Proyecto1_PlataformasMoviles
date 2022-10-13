package com.example.proyecto_final_apps.ui.fragments.account_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentAccountDetailsBinding
import com.example.proyecto_final_apps.ui.Components.PieChart
import com.example.proyecto_final_apps.ui.adapters.ChartDescriptionAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.util.DATE_FORMAT
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*


class AccountDetailsFragment : Fragment(), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentAccountDetailsBinding
    private val accountDetailsViewModel:AccountDetailsViewModel by viewModels()

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
        setListeners()
        setObservers()
    }

    private fun setObservers(){
        lifecycleScope.launchWhenStarted {
            accountDetailsViewModel.dateFilterFlow.collectLatest { filter ->
                if (filter is AccountDetailsViewModel.Filter.ActiveDateFilter) {
                    addFilterChip(filter.startDate, filter.endDate)
                }

            }
        }
    }


    private fun addFilterChip(startDate:Date, endDate:Date){

        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)

        val chip = Chip(requireContext())
        chip.text = requireContext().getString(R.string.date_filter_format, dateFormat.format(startDate),dateFormat.format(endDate))

        chip.isCloseIconVisible = true

        chip.setChipIconResource(R.drawable.ic_calendar)
        chip.setOnCloseIconClickListener{
            binding.chipGroupAccountDetailsDateFilter.removeView(chip)
            accountDetailsViewModel.removeDateFilter()
        }

        binding.chipGroupAccountDetailsDateFilter.addView(chip)
    }

    private fun setListeners() {
        binding.apply {
            imageViewAccountDetailsSortIcon.setOnClickListener{

                //mostrar date picker
                val dateRangePicker =
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .build()

                    dateRangePicker.addOnPositiveButtonClickListener {
                        val startDate = Date(it.first)
                        val endDate = Date(it.second)

                        accountDetailsViewModel.addDateFilter(startDate, endDate)
                    }

                dateRangePicker.show(requireActivity().supportFragmentManager, "DatePicker")
            }
        }
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