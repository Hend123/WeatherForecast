package com.example.weatherforecast.presentation.views.alerts


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.CustomDailogAddAlertBinding
import java.text.SimpleDateFormat
import java.util.*


class AddAlertDialog : DialogFragment() {
    private lateinit var binding: CustomDailogAddAlertBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    var myHour = 0
    var myMinute: Int = 0
    var myYear = 0
    var myMonth: Int = 0
    var myDay: Int = 0
    var date:String= ""

    companion object {
        val SHARED_PREF_CUSTOM_D = "sharedPrefCustomD"
        val LOCATION_CHOICE = "locationChoice"
        val IS_OPEN_CUSTOM_D = "isOpen"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.custom_dailog_add_alert, container, false
        )
        init()
//        editor.putString(IS_OPEN_CUSTOM_D, "open")
//        editor.apply()
//        editor.commit()
//        editor.putLong("sTime", t1!!)
//        editor.putLong("eTime", t2!!)
//        editor.putLong("sDate", d1!!)
//        editor.putLong("eDate", d2!!)
//        editor.apply()
//        editor.commit()
        binding.sDate.setOnClickListener {
            getDate()
        }
        binding.sTime.setOnClickListener {
            getTime()
        }






        return binding.root
    }

    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun getDate() {
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
        Log.v("day", day.toString())


        val dpd = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.sDate.setText("" + dayOfMonth + " " + monthOfYear + ", " + year)
                myMonth = dayOfMonth
                myYear = monthOfYear
                myDay = dayOfMonth
                date = myDay.toString() + "/" + myMonth + "/" + myYear


            },
            year,
            month,
            day
        )

        dpd.show()


    }

    private fun getTime() {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(requireContext(),TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
            binding.sTime.setText("" + h + " " + m )
            myHour = h
            myMinute = m
            date = date + " " + myHour + ":" + myMinute
            editor.putString("date",date)
            editor.commit()
            editor.apply()

        }),hour,minute,false)

        tpd.show()
    }
}
