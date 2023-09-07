package com.example.electrishut.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.electrishut.R
import com.example.electrishut.databinding.FragmentSettingsBinding

class Settings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.bttnMobileNumber.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_settings_to_mobileNumber)
        }

        binding.bttnAbout.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_settings_to_about)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}