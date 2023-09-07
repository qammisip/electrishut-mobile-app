package com.example.electrishut.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.electrishut.R
import com.example.electrishut.databinding.FragmentAboutBinding
import com.example.electrishut.databinding.FragmentAbtThresholdsBinding

class AbtThresholds : Fragment() {

    private var _binding: FragmentAbtThresholdsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAbtThresholdsBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // show up arrow
            setDisplayShowHomeEnabled(false) // show app icon
            setDisplayShowTitleEnabled(false) // hide title
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowHomeEnabled(true) // show app icon
            setDisplayHomeAsUpEnabled(false) // hide up arrow
        }
        super.onDestroyView()
        _binding = null
    }
}