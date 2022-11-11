package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.databinding.FragmentExternalUserProfileBinding

class ExternalUserProfileFragment : Fragment() {


    private lateinit var binding:FragmentExternalUserProfileBinding
    private val args:ExternalUserProfileFragmentArgs by navArgs()

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

        showData()
    }

    private fun showData() {

        binding.apply {
/*
        textViewExternalUserProfileName.text = args.name
        textViewExternalUserProfileAlias.text = getString(R.string.alias_format, args.alias)
        imageViewExternalUserProfilePicture.load(args.pictureUrl) {
            placeholder(R.drawable.ic_loading)
            error(R.drawable.ic_default_user) //Imagen por default
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
        }

 */
        }
    }
}