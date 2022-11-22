package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentNewActionBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.fragments.tabLayout.TabLayoutViewModel
import com.google.android.material.snackbar.Snackbar

class NewActionFragment : Fragment() {

    private lateinit var binding: FragmentNewActionBinding
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val tabLayoutViewModel: TabLayoutViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewActionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        tabLayoutViewModel.deleteData()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.NEW)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayoutViewModel.deleteData()
        setListeners()

    }

    private fun setListeners() {
        binding.apply {

            cardViewNewActionFragmentOperationCard.setOnClickListener{
                findNavController().navigate(R.id.action_newActionFragment_to_tabLayoutFragment)
            }

            cardViewNewActionFragmentTransferenceCard.setOnClickListener{
                findNavController().navigate(R.id.action_newActionFragment_to_newTransferFragment)
            }

            cardViewNewActionFragmentNewDebtCard.setOnClickListener{
                findNavController().navigate(R.id.action_newActionFragment_to_newDebtFragment)
            }

            cardViewNewActionFragmentPendingOperationCard.setOnClickListener{
                Snackbar.make(
                    requireView(),
                    "Esta funcionalidad extra no fue implementada como parte del proyecto.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Ok"){
                    Toast.makeText(requireContext(), "Gracias por tu comprensión. ", Toast.LENGTH_LONG).show()
                }.show()
            }
        }
    }


}