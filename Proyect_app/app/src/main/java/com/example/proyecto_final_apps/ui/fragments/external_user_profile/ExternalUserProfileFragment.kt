package com.example.proyecto_final_apps.ui.fragments.external_user_profile

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
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.databinding.FragmentExternalUserProfileBinding
import com.example.proyecto_final_apps.ui.activity.LoadingViewModel
import com.example.proyecto_final_apps.ui.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ExternalUserProfileFragment : Fragment() {


    private lateinit var binding: FragmentExternalUserProfileBinding
    private val args: ExternalUserProfileFragmentArgs by navArgs()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private val externalProfileViewModel: ExternalProfileViewModel by viewModels()

    private var blockAddUserAction = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExternalUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setListeners()

        loadingViewModel.showLoadingDialog()
        lifecycleScope.launchWhenStarted {
            externalProfileViewModel.getUserData(args.userId, false)
            loadingViewModel.hideLoadingDialog()
        }
    }

    private fun setListeners() {
        binding.apply {
            swipeRefreshLayoutExternalUserProfileFragment.setOnRefreshListener {
                lifecycleScope.launchWhenStarted {
                    externalProfileViewModel.getUserData(args.userId, true)
                    binding.swipeRefreshLayoutExternalUserProfileFragment.isRefreshing = false
                }
            }

            buttonExternalUserProfileAddUser.setOnClickListener {
                handleAddUserAction()
            }
        }
    }

    private fun handleAddUserAction() {
        if(blockAddUserAction) return
        blockAddUserAction = true

        lifecycleScope.launchWhenStarted {
            externalProfileViewModel.addAsContact(args.userId).collectLatest { status ->
                if(status is Status.Loading) loadingViewModel.showLoadingDialog()
                else loadingViewModel.hideLoadingDialog()

                if(status is Status.Success){
                    val action = ExternalUserProfileFragmentDirections.actionExternalUserProfileFragmentToContactProfileFragment(status.value.userAsContact)
                    findNavController().navigate(action)
                }else if (status is Status.Error){
                    Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                    blockAddUserAction = false
                }
            }
        }

    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            externalProfileViewModel.fragmentState.collectLatest { state ->
                handleFragmentState(state)
            }
        }

            lifecycleScope.launchWhenStarted {
                externalProfileViewModel.userData.collectLatest { status ->
                    if (status is Status.Success) {
                        showData(status.value)
                        externalProfileViewModel.setSuccessFragmentState()
                    }else if (status is Status.Error){
                        Toast.makeText(requireContext(), status.error, Toast.LENGTH_LONG).show()
                        externalProfileViewModel.setErrorFragmentState()
                    }
                }
            }

    }

    private fun showData(user: UserModel) {

        binding.apply {

        textViewExternalUserProfileName.text = getString(R.string.fullName_template, user.name, user.lastName)
        textViewExternalUserProfileAlias.text = getString(R.string.alias_format, user.alias)
        imageViewExternalUserProfilePicture.load(user.imageUrl) {
            placeholder(R.drawable.ic_loading)
            error(R.drawable.ic_default_user) //Imagen por default
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
        }

        }
    }


    private fun handleFragmentState(state: Status<Boolean>) {

        when (state) {
            is Status.Success -> {
                binding.apply {
                    containerExternalUserProfileFragment.visibility = View.VISIBLE
                    containerNoResultsContent.visibility = View.GONE
                }
            }
            is Status.Error -> {
                binding.apply {
                    containerExternalUserProfileFragment.visibility = View.GONE
                    containerNoResultsContent.visibility = View.VISIBLE
                }
            }
            else -> {
                binding.apply {
                    containerExternalUserProfileFragment.visibility = View.GONE
                    containerNoResultsContent.visibility = View.GONE
                }
            }
        }
    }
}
