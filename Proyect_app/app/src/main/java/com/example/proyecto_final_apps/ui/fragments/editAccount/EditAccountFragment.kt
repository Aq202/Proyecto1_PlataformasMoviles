package com.example.proyecto_final_apps.ui.fragments.editAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentEditAccountBinding
import com.example.proyecto_final_apps.helpers.twoDecimals
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.fragments.newAccount.NewAccountFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

@AndroidEntryPoint
class EditAccountFragment : Fragment() {

    private lateinit var binding:FragmentEditAccountBinding
    private val args:EditAccountFragmentArgs by navArgs()
    private val editAccountViewModel:EditAccountViewModel by viewModels()
    private val bottomNavigationViewModel: BottomNavigationViewModel by activityViewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false)
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
        setObservers()
        editAccountViewModel.getAccountData(args.accountId)
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            editAccountViewModel.accountData.collectLatest { status ->

                //Mostrar-ocultar loading dialog
                if(status is Status.Loading)loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success){
                    status.value?.let { account ->
                        binding.apply {
                            textInputEditAccountFragmentAccountName.editText!!.setText(account.title)
                            textInputEditAccountFragmentAmount.editText!!.setText(account.total.twoDecimals())
                        }
                    }

                }else if (status is Status.Error){
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                    //Navegar de regreso
                    val action = EditAccountFragmentDirections.actionEditAccountFragmentToAccountDetailsFragment(args.accountId)
                    findNavController().navigate(action)
                }

            }
        }
    }


    private fun setListeners() {
        binding.apply {

            textInputEditAccountFragmentAccountName.editText!!.setOnFocusChangeListener { _, hasFocus ->
                //validate name
                if (!hasFocus) {
                    validateTitle()
                } else textInputEditAccountFragmentAccountName.error = null

            }

            //validar monto
            textInputEditAccountFragmentAmount.editText!!.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    validateTotal()
                } else textInputEditAccountFragmentAmount.error = null
            }


            buttonEditAccountFragmentAddAccountButton.setOnClickListener {
                createAccount()
            }
        }


    }

    private fun createAccount() {
        if (!validateTitle() && !validateTotal()) return


        val title = binding.textInputEditAccountFragmentAccountName.editText!!.text.toString()
        val total =
            binding.textInputEditAccountFragmentAmount.editText!!.text.toString().toDouble()



        lifecycleScope.launch {
             editAccountViewModel.updateAccount(accountLocalId=args.accountId, title = title, total = total).collectLatest { status ->

                if(status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success && status.value != null){
                    val action = EditAccountFragmentDirections.actionEditAccountFragmentToAccountDetailsFragment(args.accountId)
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
                    textInputEditAccountFragmentAmount.editText!!.text.toString().toDouble()

                if (amount < 0) textInputEditAccountFragmentAmount.error =
                    "El total de la cuenta debe ser mayor a cero."
                else {
                    textInputEditAccountFragmentAmount.error = null
                    return true
                }
            } catch (ex: NumberFormatException) {
                textInputEditAccountFragmentAmount.error =
                    "El total de la cuenta es obligatorio."
            }

        }

        return false
    }

    private fun validateTitle(): Boolean {
        binding.apply {
            val name = textInputEditAccountFragmentAccountName.editText!!.text
            if (name.trim().isEmpty()) {
                textInputEditAccountFragmentAccountName.error =
                    "El nombre de la cuenta es obligatorio."
                return false
            }
            textInputEditAccountFragmentAccountName.error = null
            return true
        }
    }

}