package com.example.weatherforecast.presentation.views.alerts

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.retrofitandcoroutine.data.remote.RetrofitClient
import com.example.weatherforecast.R
import com.example.weatherforecast.Repository.FetchFromApiWorker
import com.example.weatherforecast.data.local.DatabaseHelperImpl
import com.example.weatherforecast.data.local.WeatherDatabase
import com.example.weatherforecast.data.remote.ApiHelperImpl
import com.example.weatherforecast.databinding.FragmentAlertsBinding
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.AlertsWeather
import com.example.weatherforecast.pojo.convertTime
import com.example.weatherforecast.presentation.view_model.AlertViewModel
import com.example.weatherforecast.presentation.view_model.WeatherViewModel
import com.example.weatherforecast.presentation.views.home.location.maps.MapsFragmentSH
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class AlertsFragment : Fragment() {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alarmManager: AlarmManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: WeatherViewModel
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertAdapter: AlertAdapter
    var lang: String = "en"
    lateinit var prefs: SharedPreferences
    var latLng: String? = null
    var lat: String? = "30.033333"
    var lon: String? = "31.233334"
    var myHour: Int? = null
    var myMinute: Int? = null
    var myYear: Int? = null
    var myMonth: Int? = null
    var myDay: Int? = null
    private lateinit var alertList: List<AlertsWeather>
    private var withSound = true
    private var notificationOrAlarm = "notification"
    var timeZ: String? = null
    var requestCodeList = ArrayList<Int>()
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alerts, container, false)
        init()
        binding.radioGroupSound.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                withSound = checkedId == R.id.withSound
            })
        binding.radioGroupNOrA.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                if (checkedId == R.id.notification) {
                    notificationOrAlarm = "notification"
                } else {
                    notificationOrAlarm = "alarm"
                }
            })
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        latLng = sharedPreferences.getString(MapsFragmentSH.MAPS_LATLON, " ")

        lang = prefs.getString("lang", "en")!!
        Log.v("alert", lat!!)
        Log.v("alert", lon!!)
        Log.v("alert", lang)

       // if (prefs.getBoolean("alert", true)) {
            if (prefs.getBoolean("gps", false)) {
                lat = sharedPreferences.getString("GPS_LAT", " ")
                lon = sharedPreferences.getString("GPS_LON", " ")
                setUpViewModel(lat!!, lon!!, lang)
            } else {
                Log.v("locatuionChoice", "here" + latLng.toString())
                if (latLng.equals(" ")) {
                    val splite = latLng!!.split(",")
                    lat = splite[0]
                    lon = splite[1]
                }
                setUpViewModel(lat!!, lon!!, lang)
            }
            viewModel.getweatherFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                it?.let {

                    if (it.alertsWeather.size > 0) {
                        alertList = it.alertsWeather
                    }
                }
            })
           // setUpFetchFromApiWorker()


//        }else{
//           val requestCodeListJson = sharedPreferences.getString("requestsOfAlerts", " ")
//            val type: Type = object : TypeToken<List<Int>>() {}.type
//            val requestCodeList:List<Int> = gson.fromJson(requestCodeListJson,type)
//            Log.v("cancel",requestCodeListJson.toString())
//            for(requestCodeItem in requestCodeList){
//                Log.v("cancel","cancel")
//                cancelAlarm(requestCodeItem)
//            }
//        }
        getAlertFromDB()
        binding.labelT.setOnClickListener {
            getTime()
        }
        binding.labelD.setOnClickListener {

            getDate()
        }
        binding.fab.setOnClickListener {
            binding.time.text = " "
            binding.date.text = " "
            if (myDay != null && myMinute != null && myDay != null && myMonth != null && myYear != null) {
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")

                //sdf.timeZone = TimeZone.getTimeZone(timeZ)

                val date: String =
                    myDay.toString() + "-" + myMonth + "-" + myYear + " " + myHour + ":" + myMinute
                val dateLong = sdf.parse(date)!!.time
                Log.v("datelong", dateLong.toString())
                if (alertList.size > 0) {
                    for (alertItem in alertList) {
                        if (dateLong / 1000 > alertItem.start && dateLong / 1000 < alertItem.end) {
                            if (notificationOrAlarm.equals("notification")) {
                                setNotification(
                                    myHour!!,
                                    myMinute!!,
                                    myDay!!,
                                    myMonth!!,
                                    myYear!!,
                                    alertItem.event,
                                    "From ${convertTime(alertItem.start)} to ${convertTime(alertItem.end)}"
                                )
                                Log.v("gg", "ggggg")

                            } else {
                                setAlaram(
                                    alertItem.event, myHour!!,
                                    myMinute!!,
                                    myDay!!,
                                    myMonth!!,
                                    myYear!!
                                )
                                Log.v("gg", "gggg2g")
                            }
                            break

                        }
                    }
                }else{
                if (notificationOrAlarm.equals("notification")) {
                    setNotification(
                        myHour!!,
                        myMinute!!,
                        myDay!!,
                        myMonth!!,
                        myYear!!,
                        "Nothing",
                        "No Dangerous Alert"
                    )
                    Log.v("gg","1")
                } else {
                    setAlaram(
                        "No Dangerous Alert", myHour!!,
                        myMinute!!,
                        myDay!!,
                        myMonth!!,
                        myYear!!
                    )
                    Log.v("gg","2")
                }
                }
            } else {
                Toast.makeText(requireActivity(), R.string.please_e_d_t, Toast.LENGTH_LONG).show()
            }


        }
        return binding.root
    }
    private fun setUpFetchFromApiWorker(){
        val workManager = WorkManager.getInstance(requireContext())
        val data:Data = Data.Builder().putString("lat",lat).putString("lon",lon).putString("lang",lang).build()
        val constrains = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val repeatingRequest = PeriodicWorkRequest.Builder(FetchFromApiWorker::class.java,1,TimeUnit.HOURS)
            .setConstraints(constrains)
             .setInputData(data)
            .build()
        workManager.enqueue(repeatingRequest)
        workManager.getWorkInfoByIdLiveData(repeatingRequest.id).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
Log.v("st",it.state.name)
        })
    }


//    @RequiresApi(Build.VERSION_CODES.N)
//    private fun simpleTime(myStart: String) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//        val parsedMillis: Date = sdf.parse(myStart)!!
//        Log.v("parsedMillis", parsedMillis.toString())
//    }

    private fun getAlertFromDB() {
        alertViewModel.getAlertFromDB().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                alertAdapter.setDataAndContext(it, requireContext())
                binding.alertRV.adapter = alertAdapter
            }

        })

    }

    private fun addAlert(
        requestCode: Int,
        event: String,
        start: String,
        description: String,
        status: Boolean
    ) {
        val alert = AlertDB(requestCode, event, start, description, status)
        GlobalScope.launch {
            Dispatchers.IO
            alertViewModel.addAlert(alert)
        }


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

                binding.date.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                Log.v("alert", "" + dayOfMonth + "/" + monthOfYear + "/" + year)
                binding.date.visibility = View.VISIBLE
                myMonth = monthOfYear + 1
                myYear = year
                myDay = dayOfMonth


            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()


    }


    private fun getTime() {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val datetime = Calendar.getInstance()


        val tpd = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                c[Calendar.HOUR_OF_DAY] = h
                c[Calendar.MINUTE] = m
                if (c.timeInMillis >= datetime.timeInMillis) {
                    binding.time.setText("" + h + ":" + m)
                    Log.v("alert", "" + h + ":" + m)
                    binding.time.visibility = View.VISIBLE
                    myHour = h
                    myMinute = m
                } else {
                    Toast.makeText(requireActivity(), "Invalide Data", Toast.LENGTH_LONG).show()
                    binding.time.text = " "
                    binding.time.visibility = View.VISIBLE

                }


            }), hour, minute, false
        )

        tpd.show()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AlertsFragment()
                .apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    private fun setUpViewModel(lat: String, lon: String, lang: String) {
//"68.3963", "36.9419"
        viewModel.setData(
           lat,lon , lang,
            ApiHelperImpl(RetrofitClient.getApiService()),
            DatabaseHelperImpl(WeatherDatabase.getInstance(requireContext()))
        )


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setNotification(startTime: Int, event: String, description: String) {
        val intent = Intent(context, AlertReceiver::class.java)
        intent.putExtra("event", event)
        intent.putExtra("desc", description)
        val r = Random()
        val i1 = r.nextInt(99)

        val pendingIntent = PendingIntent.getBroadcast(context, i1, intent, 0)
        requestCodeList.add(i1)
        val alertTime: Long = startTime.toLong()
        Log.v("alertTime", alertTime.toString())
       alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
        Toast.makeText(context, R.string.set_alarm, Toast.LENGTH_LONG).show()
        requireActivity().registerReceiver(AlertReceiver(), IntentFilter())
    }

    private fun setAlaram(
        desc: String, hour: Int,
        min: Int,
        day: Int,
        month: Int,
        year: Int
    ) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar[Calendar.MONTH] = month - 1
        calendar[Calendar.DATE] = day
        calendar[Calendar.YEAR] = year
        calendar[Calendar.SECOND] = 0
        val alarmtime: Long = calendar.timeInMillis
        val dateStart = java.util.Date(alarmtime.toLong() * 1000)
        calendar.time = dateStart
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AlarmClock.EXTRA_MESSAGE, desc)
            putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY))
            putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE))
        }
        ContextCompat.startActivity(requireContext(), intent, null)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setNotification(
        hour: Int,
        min: Int,
        day: Int,
        month: Int,
        year: Int,
        event: String,
        description: String
    ) {
        val intentA = Intent(context, AlertReceiver::class.java)
        intentA.putExtra("event", event)
        intentA.putExtra("desc", description)
        intentA.putExtra("sound", withSound)
        val r = Random()
        val i1 = r.nextInt(99)
        val pendingIntentA = PendingIntent.getBroadcast(context, i1, intentA, 0)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar[Calendar.MONTH] = month - 1
        calendar[Calendar.DATE] = day
        calendar[Calendar.YEAR] = year
        calendar[Calendar.SECOND] = 0
        val alarmtime: Long = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime, pendingIntentA)
        Toast.makeText(context, R.string.set_alarm, Toast.LENGTH_LONG).show()
        requireActivity().registerReceiver(AlertReceiver(), IntentFilter())
        var date = day.toString() + "/" + month + "/" + year + " " + hour + ":" + min

        addAlert(i1, event, date, description, true)
    }

    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        alarmManager =
            requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alertList = ArrayList()
        binding.alertRV.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.alertRV.setHasFixedSize(true)
        alertAdapter = AlertAdapter()
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.alertRV)
        alertViewModel = ViewModelProvider(
            this
        ).get(AlertViewModel::class.java)
        alertViewModel.setDataFromDB(
            ApiHelperImpl(RetrofitClient.getApiService()),
            DatabaseHelperImpl(WeatherDatabase.getInstance(requireContext()))
        )
        viewModel = ViewModelProvider(
            this
        ).get(WeatherViewModel::class.java)

    }

    // swipe for delete
    var itemTouchHelper: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(activity).setMessage("Do You Want to Delete this Alert ?!")
                    .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, id -> //when delete item from tripDatabase, add tripHistoryDB
                            val alertItemDeleted = alertAdapter.getItemByVH(viewHolder)
                            cancelAlarm(alertItemDeleted.requestCode)
                            deleteFavoriteItemFromDB(alertItemDeleted)
                            alertAdapter.removeAlertItem(viewHolder)
                        })
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener { dialog, id ->
                            getAlertFromDB()
                        }).show()

            }
        }

    // delete favorite item
    fun deleteFavoriteItemFromDB(alertDB: AlertDB) {
        alertViewModel.deleteAlertItem(alertDB)
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

}