package com.example.proyecto_final_apps.ui.fragments.new_debt

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.ContactWithUserModel
import com.example.proyecto_final_apps.databinding.FragmentNewDebtBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException

@AndroidEntryPoint
class NewDebtFragment : Fragment() {
    private lateinit var binding: FragmentNewDebtBinding
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val newDebtViewModel: NewDebtViewModel by viewModels()

    private var blockSendingAction = false

    private var accountSelectedIndex: Int? = null
    private lateinit var accountsList: List<AccountModel>

    private var contactSelectedIndex: Int? = null
    private lateinit var contactsList: List<ContactWithUserModel>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDropLists()
        setListeners()
        setObservers()

        lifecycleScope.launchWhenStarted {
            newDebtViewModel.getFormData()
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {

            newDebtViewModel.fragmentState.collectLatest { state ->
                handleFragmentState(state)
            }
        }
        lifecycleScope.launchWhenStarted {
            newDebtViewModel.contactsList.collectLatest { status ->
                if (status is Status.Success) {
                    setContactsDropList(status.value)
                    contactsList = status.value
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            newDebtViewModel.accountsList.collectLatest { status ->
                if (status is Status.Success) {
                    setAccountDropList(status.value)
                    accountsList = status.value
                }
            }
        }
    }

    private fun handleFragmentState(state: Status<Boolean>) {
        if (state is Status.Loading) loadingViewModel.showLoadingDialog()
        else loadingViewModel.hideLoadingDialog()

        if (state is Status.Success) {
            binding.apply {
                newDebtFragmentContainer.visibility = View.VISIBLE
                containerNoResultsContent.visibility = View.GONE
            }
        } else if (state is Status.Error) {
            binding.apply {

                //cambiar mensaje e imagen de error

                val image = if (state.errorType == NewDebtViewModel.NewDebtErrors.NO_CONTACT)
                    R.drawable.banner_contacts
                else
                    R.drawable.banner_money

                val errorMessage =
                    if (state.errorType == NewDebtViewModel.NewDebtErrors.NO_CONTACT)
                        "No hay contactos disponibles"
                    else "No hay cuentas disponibles"

                imageViewNoResultsBanner.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        image
                    )
                )
                textViewNoResults.text = errorMessage

                //mostrar contenido
                newDebtFragmentContainer.visibility = View.GONE
                containerNoResultsContent.visibility = View.VISIBLE
            }
            Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setAccountDropList(account: List<AccountModel>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            account.map { it.title })
        binding.autoCompleteViewNewDebtFragmentSourceAccount.setAdapter(adapter)
    }

    private fun setContactsDropList(contacts: List<ContactWithUserModel>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            contacts.map {
                "(${
                    getString(
                        R.string.alias_format,
                        it.user.alias
                    )
                }) ${getString(R.string.fullName_template, it.user.name, it.user.lastName)}"
            })
        binding.autoCompleteViewNewDebtFragmentContact.setAdapter(adapter)
    }

    private fun setDropLists() {
        val accounts = arrayListOf<String>()
        val operationTypes = listOf(getString(R.string.ingreso), getString(R.string.egreso))

        val adapterAccount = ArrayAdapter(requireContext(), R.layout.list_item, accounts)
        val adapterOperation = ArrayAdapter(requireContext(), R.layout.list_item, operationTypes)
        binding.autoCompleteViewNewDebtFragmentSourceAccount.setAdapter(adapterAccount)
        binding.autoCompleteViewNewDebtFragmentOperationType.setAdapter(adapterOperation)
    }

    private fun setListeners() {
        //Cambiar indice seleccionado en autocompletes
        binding.apply {
            autoCompleteViewNewDebtFragmentSourceAccount.setOnItemClickListener { _, _, index, _ ->
                accountSelectedIndex = index;
            }
            autoCompleteViewNewDebtFragmentContact.setOnItemClickListener { _, _, index, _ ->
                contactSelectedIndex = index
            }

            //send button
            buttonNewDebtFragmentAdd.setOnClickListener {
                sendForm()
            }

            //loading
            swipeRefreshLayoutNewDebtFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {
                    newDebtViewModel.getFormData(true)
                    binding.swipeRefreshLayoutNewDebtFragment.isRefreshing = false
                }
            }
        }
    }

    //Devuelve el Id local de la cuenta
    private fun getAndValidateAccount(): AccountModel? {
        binding.apply {
            if (accountSelectedIndex == null) {
                textInputLayoutNewDebtFragmentSourceAccount.error =
                    "Debe especificar la cuenta objetivo."
                return null
            }
            textInputLayoutNewDebtFragmentSourceAccount.error = null
            return accountsList[accountSelectedIndex!!]
        }
    }

    private fun getAndValidateUserContact(): ContactModel? {
        binding.apply {
            if (contactSelectedIndex == null) {
                textInputLayoutNewDebtFragmentUser.error = "Debe especificar el usuario objetivo."
                return null
            }
            textInputLayoutNewDebtFragmentSourceAccount.error = null
            return contactsList[contactSelectedIndex!!].contact
        }
    }

    private fun getAndValidateTypeOperation(): Boolean? {
        binding.apply {
            val type = textInputLayoutNewDebtFragmentOperationType.editText!!.text.toString()
            if (type.trim().isEmpty()) {
                textInputLayoutNewDebtFragmentOperationType.error =
                    "Debe especificar el tipo de operación."
                return null
            }
            textInputLayoutNewDebtFragmentOperationType.error = null
            return type == getString(R.string.ingreso)
        }
    }

    private fun getAndValidateAmount(account: AccountModel, active: Boolean): Double? {
        binding.apply {
            var amount: Double = 0.0
            var error: String? = null
            try {
                amount = textInputLayoutNewDebtFragmentAmount.editText!!.text.toString()
                    .toDouble()

                if (amount <= 0) error = "El monto de la deuda debe ser mayor a cero."
                else if (!active && account.total < amount) error =
                    "La cuenta seleccionada no cuenta con fondos suficientes."


            } catch (ex: NumberFormatException) {
                error = "Debe ingresar un monto para la  deuda."
            }

            return if (error != null) {
                textInputLayoutNewDebtFragmentAmount.error = error
                null
            } else {
                textInputLayoutNewDebtFragmentAmount.error = null
                amount
            }
        }
    }

    private fun getDescription(): String? {
        val description =
            binding.textInputLayoutNewDebtFragmentDescription.editText!!.text.toString()
        return if (description.trim().isNotEmpty()) description
        else null
    }

    private fun sendForm() {

        if (blockSendingAction) return

        val contact = getAndValidateUserContact() ?: return
        val account = getAndValidateAccount() ?: return
        val type = getAndValidateTypeOperation() ?: return
        val amount = getAndValidateAmount(account, type) ?: return
        val description = getDescription()

        blockSendingAction = true

        lifecycleScope.launchWhenStarted {
            newDebtViewModel.createNewDebt(
                contact = contact,
                active = type,
                amount = amount,
                account = account,
                description = description,
            ).collectLatest { status ->
                if (status is Status.Success) {
                    //NAVEGAR A DETALLES DE DEUDA
                    val action = NewDebtFragmentDirections.actionNewDebtFragmentToDebtDetailsFragment(status.value.localId!!)
                    findNavController().navigate(action)

                } else if (status is Status.Error) {
                    blockSendingAction = false
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                }

            }


        }
    }
}
