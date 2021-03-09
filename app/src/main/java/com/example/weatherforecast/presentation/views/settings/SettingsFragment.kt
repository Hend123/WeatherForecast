package com.example.weatherforecast.presentation.views.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        init()
        editor.putString("isOpen", "open")
        editor.apply()
        editor.commit()
        findPreference<Preference>("maps")!!.setOnPreferenceClickListener(object :
            Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                findNavController().navigate(R.id.action_settingsFragment_to_mapsFragmentSH)
                return true
            }
        })

            preferenceManager.findPreference<Preference>("lang")!!
                .setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener { preference, newValue ->
                 Toast.makeText(requireActivity(), R.string.udpate_lang, Toast.LENGTH_LONG).show()

//                    setLocale(requireActivity(),newValue.toString())
                //startActivity(Intent(requireContext(),MainActivity::class.java))
           return@OnPreferenceChangeListener true
        })


    }


    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

    }

}