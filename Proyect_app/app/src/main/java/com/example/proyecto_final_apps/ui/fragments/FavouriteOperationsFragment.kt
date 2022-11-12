package com.example.proyecto_final_apps.ui.fragments

import android.content.Context
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_final_apps.data.local.entity.OperationModel
import com.example.proyecto_final_apps.databinding.FragmentFavouriteOperationsBinding
import com.example.proyecto_final_apps.ui.adapters.OperationAdapter
import com.example.proyecto_final_apps.ui.adapters.OperationItem


class FavouriteOperationsFragment : Fragment(), OperationAdapter.OperationListener {
    private lateinit var binding : FragmentFavouriteOperationsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycler()
    }

    private fun setUpRecycler() {
        val data = mutableListOf<OperationItem>()

        val context = this
        binding.recyclerViewFavouriteOperationFragmentFavouriteOperations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            isNestedScrollingEnabled = false  //disable scroll

            adapter = OperationAdapter(data, context)
        }
    }

    override fun onItemClicked(operationData: OperationItem, position: Int) {
        Toast.makeText(requireContext(), "${operationData.title}", Toast.LENGTH_LONG).show()
    }

    override fun onItemPressed(operationData: OperationItem, position: Int) {
        vibrate(100)

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