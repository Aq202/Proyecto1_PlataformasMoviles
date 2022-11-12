package com.example.proyecto_final_apps.ui.fragments.contact_profile

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.local.entity.ContactFullDataModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.databinding.FragmentContactProfileBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem
import com.example.proyecto_final_apps.ui.fragments.OperationDetailsFragmentDirections
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ContactProfileFragment : Fragment() {
    private lateinit var binding: FragmentContactProfileBinding
    private val args: ContactProfileFragmentArgs by navArgs()
    private val contactProfileViewModel: ContactProfileViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private val acceptedDebts = mutableListOf<OperationItem>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDebtsRecycler()
        setObservers()
        setListeners()

        //load data
        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {
            contactProfileViewModel.getContactData(args.userId, false)
            loadingViewModel.hideLoadingDialog()
        }

    }

    private fun setListeners() {
        binding.apply {
            swipeResfreshLayoutContactProfileFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {
                    contactProfileViewModel.getContactData(args.userId, true)
                    swipeResfreshLayoutContactProfileFragment.isRefreshing = false
                }
            }
        }
    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            contactProfileViewModel.fragmentState.collectLatest { status ->
                manageFragmentContentState(status)
            }
        }

        lifecycleScope.launchWhenStarted {
            contactProfileViewModel.contactData.collectLatest { status ->
                if (status is Status.Success) {
                    showData(status.value)
                    contactProfileViewModel.setSuccessFragmentState()
                } else if (status is Status.Error) {
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                    contactProfileViewModel.setErrorFragmentState()

                }
            }
        }
    }

    private fun manageFragmentContentState(status: Status<Boolean>) {
        when (status) {
            is Status.Success ->
                binding.apply {
                    nestedScrollViewContactProfileFragmentFragmentContainer.visibility =
                        View.VISIBLE
                    containerNoResultsContent.visibility = View.GONE
                }
            is Status.Error ->
                binding.apply {
                    nestedScrollViewContactProfileFragmentFragmentContainer.visibility = View.GONE
                    containerNoResultsContent.visibility = View.VISIBLE
                }

            else ->
                binding.apply {
                    nestedScrollViewContactProfileFragmentFragmentContainer.visibility = View.GONE
                    containerNoResultsContent.visibility = View.GONE
                }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showData(data: ContactFullDataModel) {
        binding.apply {

            textViewContactProfileFragmentName.text = getString(R.string.fullName_template, data.userAsContactData.name, data.userAsContactData.lastName)
            textViewContactProfileFragmentAlias.text =
                getString(R.string.alias_format, data.userAsContactData.alias)
            imageViewContactProfileFragmentPicture.load( data.userAsContactData.imageUrl) {
                placeholder(R.drawable.ic_loading)
                error(R.drawable.ic_default_user) //Imagen por default
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            if (data.debtsAccepted?.isNotEmpty() == true) {

                recyclerViewContactProfileFragmentAcceptedDebtsList.visibility = View.VISIBLE
                textViewContactProfileFragmentAcceptedDebtsTitle.visibility = View.VISIBLE

                acceptedDebts.clear()
                acceptedDebts.addAll(data.debtsAccepted.map { debt ->
                    OperationItem(
                        localId = debt.localId!!,
                        remoteId = debt.remoteId,
                        title = getString(
                            if (debt.active) R.string.active_debt_title else R.string.passive_debt_title,
                            getString(
                                R.string.fullName_template, data.userAsContactData.name,
                                data.userAsContactData.lastName
                            )
                        ),
                        category= Category(requireContext()).getDebtsCategory(),
                        amount = debt.amount,
                        active = debt.active,
                        imgUrl =data.userAsContactData.imageUrl
                    )
                })

                binding.recyclerViewContactProfileFragmentAcceptedDebtsList.adapter!!.notifyDataSetChanged()

            } else {
                recyclerViewContactProfileFragmentAcceptedDebtsList.visibility = View.GONE
                textViewContactProfileFragmentAcceptedDebtsTitle.visibility = View.GONE
            }


        }
    }

    private fun setUpDebtsRecycler() {

        val listener = AcceptedOperationListener(requireView())
        binding.recyclerViewContactProfileFragmentAcceptedDebtsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(acceptedDebts, listener)
        }
    }


    private class AcceptedOperationListener(val view: View) : OperationAdapter.OperationListener {
        override fun onItemClicked(operationData: OperationItem, position: Int) {

        }

        override fun onItemPressed(operationData: OperationItem, position: Int) {

        }

    }

}