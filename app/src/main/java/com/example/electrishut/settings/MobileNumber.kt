package com.example.electrishut.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.electrishut.databinding.FragmentMobileNumberBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

class MobileNumber : Fragment() {

    private var _binding: FragmentMobileNumberBinding? = null
    private val binding get() = _binding!!

    private lateinit var database : DatabaseReference
    private lateinit var firebase: FirebaseDatabase

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMobileNumberBinding.inflate(inflater, container, false)
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // show up arrow
            setDisplayShowHomeEnabled(false) // show app icon
            setDisplayShowTitleEnabled(false) // hide title
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())

        // Configure Firebase authentication
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("electrishut@gmail.com", "%ELECTRIshut2023!")
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid

                    binding.bttnSave.setOnClickListener {
                        // Saving Data from App to Firebase
                        val phoneNumber = binding.newNumInput.text.toString()
                        val confirmNumber = binding.reEnterNumInput.text.toString()

                        if (phoneNumber == confirmNumber && phoneNumber.length == 10 && confirmNumber.length == 10) {
                            firebase = FirebaseDatabase.getInstance()
                            database = firebase.getReference("PhoneNumber")
                            val number = PhoneNumber(phoneNumber,confirmNumber)
                            database.child(uid).setValue(number).addOnSuccessListener {
                                binding.newNumInput.text!!.clear()
                                binding.reEnterNumInput.text!!.clear()

                                Toast.makeText(requireContext(), "Successfully changed the mobile number.", Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener {
                                    binding.newNumInput.text!!.clear()
                                    binding.reEnterNumInput.text!!.clear()
                                    Toast.makeText(requireContext(), "Cannot save the mobile number.", Toast.LENGTH_SHORT).show()
                                }
                        }

                        else {
                            Toast.makeText(requireContext(), "Error! Check the entered mobile number.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Displaying Data
                    val database = FirebaseDatabase.getInstance().reference
                    val dataRef = database.child("PhoneNumber").child(uid).child("phoneNumber")

                    dataRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val numberData = snapshot.value.toString()
                            binding.txtNumCont.text = numberData
                            Log.d("MobileNumberFragment", "Retrieved value from Firebase Realtime Database.")
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors here
                            Toast.makeText(requireContext(), "Error! Cannot access the database.", Toast.LENGTH_SHORT).show()
                            Log.w("MobileNumberFragment", "Failed to read value.", databaseError.toException())
                        }
                    })

                    // Verifying the mobile number that is being entered.
                    newNumListener()

                    // Canceling Saving the Data
                    binding.bttnCancel.setOnClickListener {
                        binding.newNumInput.text!!.clear()
                        binding.reEnterNumInput.text!!.clear()
                    }

                }

                else {
                    // Authentication failed, handle error
                Toast.makeText(requireContext(), "Error! Cannot access the database.", Toast.LENGTH_SHORT).show()
                }
            }

        return binding.root
    }

    private fun newNumListener() {
        binding.newNumInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.inputNewNum.helperText = validPhone()
            }
        }
        binding.reEnterNumInput.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.inputreEnterNum.helperText = validPhone()
            }
        }
    }

    private fun validPhone(): String? {
        val phoneText = binding.newNumInput.text.toString(); binding.reEnterNumInput.text.toString()
        if(!phoneText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Numbers!"
        }
        if(phoneText.length != 10) {
            return "Must be 10 Digits!"
        }
        return null
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

