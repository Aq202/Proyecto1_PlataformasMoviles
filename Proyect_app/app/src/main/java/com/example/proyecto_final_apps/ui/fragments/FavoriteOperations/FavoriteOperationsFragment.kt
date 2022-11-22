package com.example.proyecto_final_apps.ui.fragments.FavoriteOperations

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto_final_apps.ui.util.Status
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.data.local.entity.toOperationItem
import com.example.proyecto_final_apps.databinding.FragmentFavoriteOperationsBinding
import com.example.proyecto_final_apps.ui.activity.BottomNavigationViewModel
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem
import com.example.proyecto_final_apps.ui.fragments.newOperation.NewOperationFragment
import com.example.proyecto_final_apps.ui.fragments.operation_details.OperationDetailsFragmentDirections
import com.example.proyecto_final_apps.ui.fragments.operation_details.OperationDetailsViewModel
import com.example.proyecto_final_apps.ui.fragments.tabLayout.TabLayoutFragment
import com.example.proyecto_final_apps.ui.fragments.tabLayout.TabLayoutFragmentDirections
import com.example.proyecto_final_apps.ui.fragments.tabLayout.TabLayoutViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import kotlinx.android.synthetic.main.fragment_tab_layout.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteOperationsFragment : Fragment(), OperationAdapter.OperationListener {
    private lateinit var binding : FragmentFavoriteOperationsBinding
    private val favoriteOperationsViewModel: FavoriteOperationsViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val tabLayoutViewModel: TabLayoutViewModel by activityViewModels()
    private var favoriteOperationsList: MutableList<OperationItem> = mutableListOf()
    private lateinit var toolBar: MaterialToolbar
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = requireActivity().findViewById(R.id.viewPager2_tabLayoutFragment)
        toolBar = requireActivity().findViewById(R.id.toolbar)

        setUpRecycler()
        setListeners()
        setObservers()

        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {
            getFragmentData()
            loadingViewModel.hideLoadingDialog()
            favoriteOperationsViewModel.setSuccessFragmentStatus()
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            favoriteOperationsViewModel.fragmentState.collectLatest { status ->
                if (status is Status.Success) {
                    binding.apply {
                        favouriteOperationsFragmentFavouriteOperationsContainer.visibility =
                            View.VISIBLE
                        containerErrorFragmentMessageContent.visibility = View.GONE
                        swipeResfreshLayoutFavoriteOperationsFragment.visibility = View.VISIBLE
                    }
                } else if (status is Status.Error) {
                    binding.apply {
                        favouriteOperationsFragmentFavouriteOperationsContainer.visibility =
                            View.GONE
                        containerErrorFragmentMessageContent.visibility = View.VISIBLE
                        swipeResfreshLayoutFavoriteOperationsFragment.visibility = View.VISIBLE
                    }
                } else if (status is Status.Loading) {
                    binding.apply {
                        favouriteOperationsFragmentFavouriteOperationsContainer.visibility =
                            View.GONE
                        containerErrorFragmentMessageContent.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            favoriteOperationsViewModel.favoriteOperationsList.collectLatest { status ->
                addFavoriteOperationsList(status)
            }
        }
        lifecycleScope.launchWhenStarted {
            favoriteOperationsViewModel.selectedOperations.collectLatest { list ->
                if(list.isNotEmpty()){
                    toolBar.menu.findItem(R.id.deleteOperation).isVisible = true
                    toolBar.menu.findItem(R.id.editOperation).isVisible = list.size == 1
                }else{
                    toolBar.menu.findItem(R.id.deleteOperation).isVisible = false
                    toolBar.menu.findItem(R.id.editOperation).isVisible = false
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addFavoriteOperationsList(status: Status<List<OperationModel>>) {
        if (status is Status.Success) {

            favoriteOperationsList.clear()
            favoriteOperationsList.addAll(status.value.map { operation ->
                operation.toOperationItem(requireContext())
            })

            binding.apply {

                recyclerViewFavoriteOperationFragmentFavoriteOperations.adapter!!.notifyDataSetChanged()

                //Mostrar lista y ocultar mensaje de no hay cuentas
                if (favoriteOperationsList.isNotEmpty()) {
                    favouriteOperationsFragmentFavouriteOperationsContainer.visibility =
                        View.VISIBLE
                    containerNoFavoriteOperations.visibility = View.GONE

                    return
                }

            }

        } else if (status is Status.Error)
            println("Diego: ${status.error}")

        //Si hay error o si está vacía la lista: mostrar mensaje de no hay cuentas
        binding.apply {
            favouriteOperationsFragmentFavouriteOperationsContainer.visibility =
                View.GONE //ocultar contenido
            containerNoFavoriteOperations.visibility = View.VISIBLE

        }
    }

    private fun setListeners() {
        binding.swipeResfreshLayoutFavoriteOperationsFragment.setOnRefreshListener {
            println("REFRESHING...")
            lifecycleScope.launchWhenStarted {
                getFragmentData(true)
                binding.swipeResfreshLayoutFavoriteOperationsFragment.isRefreshing = false
            }
        }
        toolBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.deleteOperation->{
                    deleteSelected()
                    true
                }
                R.id.editOperation->{
                    editOperation()
                    true
                }
                else -> true
            }
        }
    }

    private fun deleteSelected() {
        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("Aceptar") { _, _ ->

                lifecycleScope.launchWhenStarted {
                    favoriteOperationsViewModel.deleteSelected().collectLatest { status ->
                        when (status) {
                            is Status.Loading -> loadingViewModel.showLoadingDialog()
                            is Status.Success -> {
                                loadingViewModel.hideLoadingDialog()
                                getFragmentData()
                            }
                            is Status.Error -> {
                                loadingViewModel.hideLoadingDialog()
                                Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                loadingViewModel.hideLoadingDialog()
                            }
                        }
                    }
                }

            }
            .setNegativeButton("Cancelar") { _, _ ->

            }
            .setTitle("¿Deseas eliminar las operaciones seleccionadas?")
            .setMessage("Toma en cuenta que esta acción es permanente y no podrás recuperar la información de estas operaciones.")
            .show()
    }

    private fun editOperation() {
        var list = favoriteOperationsViewModel.selectedOperations.value
        val action = TabLayoutFragmentDirections.actionTabLayoutFragmentToEditFavouriteOperationFragment(list[0])
        findNavController().navigate(action)
        favoriteOperationsViewModel.desSelect()
    }

    private suspend fun getFragmentData(forceUpdate: Boolean = false) {
        favoriteOperationsViewModel.getFavoriteOperations(forceUpdate)
    }

    private fun setUpRecycler() {

        val context = this
        binding.recyclerViewFavoriteOperationFragmentFavoriteOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(favoriteOperationsList, context)
        }
    }

    override fun onItemClicked(operationData: OperationItem, position: Int) {
        if(favoriteOperationsViewModel.selectedOperations.value.contains(operationData.localId)){
            favoriteOperationsViewModel.removeOperationSelected(operationData.localId)
            operationData.isSelected = !operationData.isSelected
            binding.recyclerViewFavoriteOperationFragmentFavoriteOperations.adapter!!.notifyDataSetChanged()
        }
        else if(favoriteOperationsViewModel.selectedOperations.value.size>0){
            favoriteOperationsViewModel.addOperationSelected(operationData.localId)
            operationData.isSelected = !operationData.isSelected
            binding.recyclerViewFavoriteOperationFragmentFavoriteOperations.adapter!!.notifyDataSetChanged()
        }else{
            lifecycleScope.launch {
                tabLayoutViewModel.getAccountData(operationData.localId, false)
                tabLayoutViewModel.getOperationData(operationData.localId,false)
            }
            viewPager.currentItem = 0

        }
    }

    override fun onItemPressed(operationData: OperationItem, position: Int) {
        vibrate(100)
        operationData.isSelected = !operationData.isSelected
        binding.recyclerViewFavoriteOperationFragmentFavoriteOperations.adapter!!.notifyDataSetChanged()

        if(favoriteOperationsViewModel.selectedOperations.value.contains(operationData.localId))
            favoriteOperationsViewModel.removeOperationSelected(operationData.localId)
        else
            favoriteOperationsViewModel.addOperationSelected(operationData.localId)

    }

    private fun vibrate(duration: Long) {
        val vibrationEffect: VibrationEffect
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val vibratorManager = requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        }else{
            @Suppress("DEPRECATION")
            requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrationEffect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }
    }
}