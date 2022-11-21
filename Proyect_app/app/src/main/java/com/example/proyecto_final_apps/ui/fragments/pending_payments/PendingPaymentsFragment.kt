package com.example.proyecto_final_apps.ui.fragments.pending_payments

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.Category
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.data.local.entity.toOperationItem
import com.example.proyecto_final_apps.databinding.FragmentPendingPaymentsBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PendingPaymentsFragment : Fragment(), OperationAdapter.OperationListener {

    private lateinit var binding: FragmentPendingPaymentsBinding

    private val pendingPaymentsViewModel: PendingPaymentsViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    private lateinit var pendingPaymentsList: MutableList<OperationItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPendingPaymentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPendingPaymentsRecycler()
        setObservers()
        setListeners()

        pendingPaymentsViewModel.getRecentDebts(false)
    }

    private fun setListeners() {
        binding.apply {
            swipeRefreshLayoutPendingPayments.setOnRefreshListener {
                pendingPaymentsViewModel.getRecentDebts(true) //Forzar actualización
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            pendingPaymentsViewModel.recentDebts.collectLatest { status ->

                fillFragmentData(status)

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillFragmentData(status: Status<List<Pair<DebtAcceptedModel, UserModel>>>) {
        //Mostrar-ocultar loading
        if(status is Status.Default) loadingViewModel.showLoadingDialog()
        else loadingViewModel.hideLoadingDialog()

        if(!(status is Status.Loading)) binding.swipeRefreshLayoutPendingPayments.isRefreshing = false //Ocultar icono de recarga

        if(status is Status.Success){

            //agregar datos
            pendingPaymentsList.clear()
            pendingPaymentsList.addAll(status.value.map {
                val debt = it.first
                val user = it.second

                val title = getString(
                    if (!debt.active)
                        R.string.passive_debt_title
                    else
                        R.string.active_debt_title,
                    getString(R.string.alias_format, user.alias)
                )

                OperationItem(
                    localId = debt.localId!!,
                    remoteId = debt.remoteId,
                    title = title,
                    category = Category(requireContext()).getDebtsCategory(),
                    amount = debt.amount,
                    active = debt.active,
                    imgUrl = user.imageUrl
                )
            })

            binding.apply {
                recyclerViewPendingPaymentsRecentOperations.adapter!!.notifyDataSetChanged()
                recyclerViewPendingPaymentsRecentOperations.visibility = View.VISIBLE
                containerNoResultsContent.visibility = View.GONE
            }

        }else if (status is Status.Error){
            binding.apply {
                recyclerViewPendingPaymentsRecentOperations.visibility = View.GONE
                containerNoResultsContent.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpPendingPaymentsRecycler() {

        pendingPaymentsList = mutableListOf()

        val context = this
        binding.recyclerViewPendingPaymentsRecentOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false //disable scroll

            adapter = OperationAdapter(pendingPaymentsList, context)
        }
    }

    override fun onItemClicked(operationData: OperationItem, position: Int) {
        findNavController().navigate(R.id.action_toPendingPaymentDetailsFragment)
    }

    override fun onItemPressed(operationData: OperationItem, position: Int) {
        Toast.makeText(requireContext(), "PRESSED...", Toast.LENGTH_LONG).show()
    }
}