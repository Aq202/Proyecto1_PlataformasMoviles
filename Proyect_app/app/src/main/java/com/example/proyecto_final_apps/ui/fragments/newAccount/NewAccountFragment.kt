package com.example.proyecto_final_apps.ui.fragments.newAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentNewAccountBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException

@AndroidEntryPoint
class NewAccountFragment : Fragment() {

    private lateinit var binding: FragmentNewAccountBinding
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val loadingViewModel:LoadingViewModel by activityViewModels()
    private val newAccountViewModel:NewAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectCurrentBottomNavigationItem()
    }

    private fun selectCurrentBottomNavigationItem() {
        bottomNavigationViewModel.setSelectedItem(BottomNavigationViewModel.BottomNavigationItem.HOME)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {

            textInputNewAccountFragmentAccountName.editText!!.setOnFocusChangeListener { _, hasFocus ->
                //validate name
                if (!hasFocus) {
                    validateTitle()
                } else textInputNewAccountFragmentAccountName.error = null

            }

            //validar monto
            textInputNewAccountFragmentAmount.editText!!.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    validateTotal()
                } else textInputNewAccountFragmentAmount.error = null
            }


            buttonNewAccountFragmentAddAccountButton.setOnClickListener {
                createAccount()
            }
        }


    }

    private fun createAccount() {
        if (!validateTitle() && !validateTotal()) return
        else if(!validateTitle() || !validateTotal()) return


        val title = binding.textInputNewAccountFragmentAccountName.editText!!.text.toString()
        val total =
            binding.textInputNewAccountFragmentAmount.editText!!.text.toString().toDouble()
        val defaultAccount = binding.switchNewAccountFragmentDefaultAccountSwitch.isChecked



        lifecycleScope.launchWhenStarted {
            newAccountViewModel.createAccount(title = title, total = total, defaultAccount=defaultAccount).collectLatest { status ->

                if(status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success && status.value != null){
                    val action = NewAccountFragmentDirections.actionNewAccountFragmentToAccountDetailsFragment(status.value.localId!!)
                    findNavController().navigate(action)
                }else if(status is Status.Error && status.error.isNotEmpty())
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateTotal(): Boolean {
        binding.apply {
            try {
                val amount =
                    textInputNewAccountFragmentAmount.editText!!.text.toString().toDouble()

                if (amount < 0) textInputNewAccountFragmentAmount.error =
                    "El total de la cuenta debe ser mayor a cero."
                else {
                    textInputNewAccountFragmentAmount.error = null
                    return true
                }
            } catch (ex: NumberFormatException) {
                textInputNewAccountFragmentAmount.error =
                    "El total de la cuenta es obligatorio."
            }

        }

        return false
    }

    private fun validateTitle(): Boolean {
        binding.apply {
            val name = textInputNewAccountFragmentAccountName.editText!!.text
            if (name.trim().isEmpty()) {
                textInputNewAccountFragmentAccountName.error =
                    "El nombre de la cuenta es obligatorio."
                return false
            }
            textInputNewAccountFragmentAccountName.error = null
            return true
        }
    }

}