package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.local.entity.UserModel
import com.example.proyecto_final_apps.databinding.FragmentUserProfileBinding
import com.example.proyecto_final_apps.helpers.DATE_FORMAT
import com.example.proyecto_final_apps.helpers.DateParse
import com.example.proyecto_final_apps.helpers.apiUrl
import com.example.proyecto_final_apps.ui.activity.UserSessionStatus
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

class UserProfileFragment : Fragment() {

    private lateinit var binding:FragmentUserProfileBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            userViewModel.userDataStateFlow.collectLatest { state ->
                when(state){
                    is UserSessionStatus.Logged -> {
                        addProfileInfo(state.data)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun addProfileInfo(data:UserModel) {
        binding.apply {
            textViewProfileUserFragmentName.text = getString(R.string.fullName_template, data.name, data.lastName)
            textViewProfileUserFragmentAlias.text = getString(R.string.alias_format, data.alias)
            textViewProfileUserEmail.text = data.email

            val date = DateParse.formatDate(data.birthDate)
            textViewProfileUserBirthday.text = DateParse.dateToWordsFormat(date)

            imageViewProfileUserFragmentPicture.load(apiUrl + data.imageUrl){
                placeholder(R.drawable.ic_default_user)
                error(R.drawable.ic_default_user)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }
    }

    private fun setListeners() {
        binding.buttonProfileUserFragmentEditProfile.setOnClickListener{
            findNavController().navigate(R.id.action_userProfileFragment_to_editProfileFragment)
        }
    }


}