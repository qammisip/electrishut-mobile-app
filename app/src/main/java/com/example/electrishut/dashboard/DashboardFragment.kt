package com.example.electrishut.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.electrishut.databinding.FragmentDashboardBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.electrishut.R
import com.example.electrishut.notification.*
import com.google.firebase.database.FirebaseDatabase

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val database = FirebaseDatabase.getInstance()
    private val firebase = database.getReference("SwitchState")
    private lateinit var auth: FirebaseAuth

    private var count = 0
    private val maxClicks = 1

    // Declare the SharedPreferences instance at the top of your fragment
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.show()

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())

        // Configure Firebase authentication
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("electrishut@gmail.com", "%ELECTRIshut2023!")
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid

                    // SENSORS OVERVIEW
                    val sensorDB = FirebaseDatabase.getInstance().reference
                    val sensorRef = sensorDB.child("SensorsData").child(uid)

                    // Initialize the SharedPreferences instance in your onCreateView or onViewCreated method
                    sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

                    sensorRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val sensorData = snapshot.getValue(SensorsData::class.java)
                                if (sensorData != null) {
                                    binding.water.text = sensorData.water
                                    binding.waterStat.text = sensorData.waterstat

                                    binding.earthquakeStat.text = sensorData.earthquakestat
                                    binding.earthquake.text = sensorData.earthquake

                                    binding.fireStat.text = sensorData.firestat
                                    binding.txtFireDetection.text = sensorData.fire
                                    binding.temperature.text = sensorData.temp

                                    // waterStat Color
                                    if (sensorData.waterstat == "CRITICAL") {
                                        binding.waterStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical_status_color))
                                    }
                                    else if (sensorData.waterstat == "WARNING") {
                                        binding.waterStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_status_color))
                                    }
                                    else if (sensorData.waterstat == "NORMAL") {
                                        binding.waterStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.power_switch))
                                    }
                                    else {
                                        // Do Nothing
                                    }

                                    // earthquakeStat Color
                                    if (sensorData.earthquakestat == "CRITICAL") {
                                        binding.earthquakeStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical_status_color))
                                    }
                                    else if (sensorData.earthquakestat == "WARNING") {
                                        binding.earthquakeStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_status_color))
                                    }
                                    else if (sensorData.earthquakestat == "NORMAL") {
                                        binding.earthquakeStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.power_switch))
                                    }
                                    else {
                                        // Do Nothing
                                    }

                                    // fireStat Color
                                    if (sensorData.firestat == "CRITICAL") {
                                        binding.fireStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical_status_color))
                                    }
                                    else if (sensorData.firestat == "WARNING") {
                                        binding.fireStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_status_color))
                                    }
                                    else if (sensorData.firestat == "NORMAL") {
                                        binding.fireStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.power_switch))
                                    }
                                    else {
                                        // Do Nothing
                                    }

                                    // waterStat Notif
                                    if (sensorData.waterstat == "CRITICAL" && !sharedPref.getBoolean("waterNotificationDismissed", false)) {
                                        makeStatusNotification("CRITICAL: Flood Detected. Power is Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("waterNotificationDismissed", true).apply()
                                        sharedPref.edit().putBoolean("waterWarningNotification", false).apply()
                                    }
                                    else if (sensorData.waterstat == "WARNING" && !sharedPref.getBoolean("waterWarningNotification", false)) {
                                        makeStatusNotification("WARNING: Flood Detected. Powered Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("waterWarningNotification", true).apply()
                                        sharedPref.edit().putBoolean("waterNotificationDismissed", false).apply()
                                    }
                                    else if (sensorData.waterstat == "NORMAL") {
                                        sharedPref.edit().putBoolean("waterNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("waterWarningNotification", false).apply()
                                    }
                                    else {
                                        sharedPref.edit().putBoolean("waterNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("waterWarningNotification", false).apply()
                                    }

                                    // earthquakeStat Notif
                                    if (sensorData.earthquakestat == "CRITICAL" && !sharedPref.getBoolean("earthquakeNotificationDismissed", false)) {
                                        makeStatusNotification("CRITICAL: Earthquake Detected. Power is Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("earthquakeNotificationDismissed", true).apply()
                                        sharedPref.edit().putBoolean("earthquakeWarningNotification", false).apply()
                                    }
                                    else if (sensorData.earthquakestat == "WARNING" && !sharedPref.getBoolean("earthquakeWarningNotification", false)) {
                                        makeStatusNotification("WARNING: Earthquake Detected.Powered Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("earthquakeWarningNotification", true).apply()
                                        sharedPref.edit().putBoolean("earthquakeNotificationDismissed", false).apply()
                                    }
                                    else if (sensorData.earthquakestat == "NORMAL") {
                                        sharedPref.edit().putBoolean("earthquakeNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("earthquakeWarningNotification", false).apply()
                                    }
                                    else {
                                        sharedPref.edit().putBoolean("earthquakeNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("earthquakeWarningNotification", false).apply()
                                    }

                                    // fireStat Notif
                                    if (sensorData.firestat == "CRITICAL" && !sharedPref.getBoolean("fireNotificationDismissed", false)) {
                                        makeStatusNotification("CRITICAL: Fire Detected. Power is Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("fireNotificationDismissed", true).apply()
                                        sharedPref.edit().putBoolean("fireWarningNotification", false).apply()
                                    }
                                    else if (sensorData.firestat == "WARNING" && !sharedPref.getBoolean("fireWarningNotification", false)) {
                                        makeStatusNotification("WARNING: Fire Detected.Powered Off.", requireActivity().applicationContext!!)

                                        sharedPref.edit().putBoolean("fireWarningNotification", true).apply()
                                        sharedPref.edit().putBoolean("fireNotificationDismissed", false).apply()
                                    }
                                    else if (sensorData.firestat == "NORMAL") {
                                        sharedPref.edit().putBoolean("fireNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("fireWarningNotification", false).apply()
                                    }
                                    else {
                                        sharedPref.edit().putBoolean("fireNotificationDismissed", false).apply()
                                        sharedPref.edit().putBoolean("fireWarningNotification", false).apply()
                                    }

                                    // System Status
                                    if (sensorData.waterstat == "CRITICAL" || sensorData.earthquakestat == "CRITICAL" || sensorData.firestat == "CRITICAL") {
                                        binding.lblSystemStat.text = "CRITICAL"
                                        binding.lblAction.text = "Power is Off."
                                        binding.lblSystemStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical_status_color))
                                        binding.statusIcon.setImageResource(R.drawable.ic_critical_status)
                                    }
                                    else if (sensorData.waterstat == "WARNING" || sensorData.earthquakestat == "WARNING" || sensorData.firestat == "WARNING") {
                                        binding.lblSystemStat.text = "WARNING"
                                        binding.lblAction.text = "Powered Off."
                                        binding.lblSystemStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_status_color))
                                        binding.statusIcon.setImageResource(R.drawable.ic_warning_status)
                                    }
                                    else {
                                        binding.lblSystemStat.text = "NORMAL"
                                        binding.lblAction.text = "No action needed."
                                        binding.lblSystemStat.setTextColor(ContextCompat.getColor(requireContext(), R.color.power_switch))
                                        binding.statusIcon.setImageResource(R.drawable.ic_normal_status)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors here
                            Toast.makeText(requireContext(), "Please check your network settings and try again.", Toast.LENGTH_SHORT).show()
                            Log.w("DashboardFragment", "Failed to read value.", databaseError.toException())
                        }
                    })

                    // Switching Feature
                    binding.bttnPower.setOnCheckedChangeListener { toggleButton, isChecked ->

                        count++
                        if (count >= maxClicks) {
                            binding.bttnPower.isEnabled = false
                            binding.bttnPower.alpha = 0.5f
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.bttnPower.isEnabled = true
                                binding.bttnPower.alpha = 1.0f
                                count = 0
                            }, 2000L)

                            Toast.makeText(requireContext(), "Switch button is locked. Try again after 2 seconds.", Toast.LENGTH_SHORT).show()
                        }

                        if (isChecked) {
                            val state = 0
                            val status = SwitchState(state)
                            firebase.child(uid).setValue(status).addOnSuccessListener {
                                // Do Nothing
                            } .addOnFailureListener {
                                // Do notihing
                            }
                            makeSwitchNotification("Power is OFF.", requireActivity().applicationContext!!)
                        }
                        else {
                            val state = 1
                            val status = SwitchState(state)
                            firebase.child(uid).setValue(status).addOnSuccessListener {
                                // Do Nothing
                            } .addOnFailureListener {
                                // Do notihing
                            }

                            makeSwitchNotification("Power is ON.", requireActivity().applicationContext!!)
                        }
                    }

                    // Checking the state of the program in order for the button in the mobile app would be in sync with the state of the system
                    val switchState = FirebaseDatabase.getInstance().reference
                    val switchRef = switchState.child("SwitchState").child(uid)

                    switchRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Get the new switch state from the database
                            val state = dataSnapshot.getValue(SwitchState::class.java)?.switchState ?: 0

                            // Update the button state based on the new switch state
                            if (state == 1) {
                                binding.bttnPower.text = "OFF"
                                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.bttnPower.setBackgroundTintList(colorStateList)
                            }
                            else if (state == 0) {
                                binding.bttnPower.text = "ON"
                                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.power_switch))
                                binding.bttnPower.setBackgroundTintList(colorStateList)
                            }
                            else {
                                // Do Nothing
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors here
                            Toast.makeText(requireContext(), "Please check your network settings and try again.", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        // About Thresholds
        binding.bttnAbtThreshold.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_dashboard_to_abtThresholds)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}