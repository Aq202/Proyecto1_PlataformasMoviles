package com.example.proyecto_final_apps.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.socket.SocketClient
import com.example.proyecto_final_apps.databinding.FragmentLoginBinding
import com.example.proyecto_final_apps.helpers.Internet
import com.example.proyecto_final_apps.ui.activity.MainActivity
import com.example.proyecto_final_apps.ui.activity.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val userViewModel:UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvents()
        setObservers()

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginStateFlow.collectLatest { result ->

                when(result){
                    is LoginStatus.Loading -> {
                        binding.apply {
                            buttonLoginFragmentLogin.visibility = View.INVISIBLE
                            progressIndicatorFragmentLogin.visibility = View.VISIBLE
                        }
                    }
                    is LoginStatus.Logged -> {

                            userViewModel.getUserData(false)

                        //Navigate to mainActivity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)

                        requireActivity().finish()

                    }
                    is LoginStatus.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.apply {
                            buttonLoginFragmentLogin.visibility = View.VISIBLE
                            progressIndicatorFragmentLogin.visibility = View.GONE
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initEvents(){

        binding.apply {
            buttonLoginFragmentLogin.setOnClickListener {

                performLogin()

            }

            textViewLoginFragmentRegisterLabel.setOnClickListener{
                requireView().findNavController().navigate(R.id.action_loginFragment2_to_signUpFragment2)
            }

            //Eliminar, es prueba de sockets nada más
            imageViewLoginFragmentBanner.setOnClickListener{
                val msg = binding.textFieldLoginFragmentUser.editText!!.text


                val mSocket = SocketClient.getSocket()

                Toast.makeText(requireContext(), "Mensaje enviado: ${msg}", Toast.LENGTH_LONG).show()

                mSocket.emit("global", msg)
            }
        }

    }

    private fun performLogin() {
        val user = binding.textFieldLoginFragmentUser.editText!!.text.toString()
        val password = binding.textFieldLoginFragmentPassword.editText!!.text.toString()

        println("DIEGO: ${Internet.checkForInternet(requireContext())}")

        loginViewModel.login(user = user, password = password)

    }
}