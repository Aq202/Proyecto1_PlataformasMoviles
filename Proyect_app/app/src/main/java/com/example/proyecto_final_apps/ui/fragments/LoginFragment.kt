package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.socket.SocketClient
import com.example.proyecto_final_apps.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

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


    }

    private fun initEvents(){

        binding.apply {
            buttonLoginFragmentLogin.setOnClickListener {
                requireView().findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }

            textViewLoginFragmentRegisterLabel.setOnClickListener{
                requireView().findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
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
}