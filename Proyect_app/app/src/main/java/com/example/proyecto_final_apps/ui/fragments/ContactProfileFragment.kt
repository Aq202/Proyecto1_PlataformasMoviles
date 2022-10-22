package com.example.proyecto_final_apps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.CachePolicy
import com.example.proyecto_final_apps.R
import com.example.proyecto_final_apps.data.CategoryTypes
import com.example.proyecto_final_apps.data.Operation
import com.example.proyecto_final_apps.data.TestOperations
import com.example.proyecto_final_apps.databinding.FragmentContactProfileBinding
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter

class ContactProfileFragment : Fragment(), OperationAdapter.OperationListener {
    private lateinit var binding:FragmentContactProfileBinding
    private val args:ContactProfileFragmentArgs by navArgs()

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
        showData()
    }

    private fun showData() {
        binding.apply {
            textViewContactProfileFragmentName.text = args.name
            textViewContactProfileFragmentAlias.text = getString(R.string.alias_format, args.alias)
            imageViewContactProfileFragmentPicture.load(args.pictureUrl) {
                placeholder(R.drawable.ic_loading)
                error(R.drawable.ic_default_user) //Imagen por default
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
        }
    }

    private fun setUpDebtsRecycler() {
        val operations = TestOperations(requireContext()).getOperations().filter {
            it.category?.type == CategoryTypes.DEUDAS
        }.take(3) as MutableList<Operation>

        val context = this
        binding.recyclerViewContactProfileFragmentDebtsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(operations, context)
        }
    }


    override fun onItemClicked(operationData: Operation, position: Int) {
        val action = OperationDetailsFragmentDirections.actionToOperationDetails(operationData.id)
        findNavController().navigate(action)
    }

    override fun onItemPressed(operationData: Operation, position: Int) {

    }
}