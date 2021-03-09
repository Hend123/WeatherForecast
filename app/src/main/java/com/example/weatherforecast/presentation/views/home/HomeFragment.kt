package com.example.weatherforecast.presentation.views.home


import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.retrofitandcoroutine.data.remote.RetrofitClient
import com.example.weatherforecast.R
import com.example.weatherforecast.Repository.FetchFromApiWorker
import com.example.weatherforecast.data.local.DatabaseHelperImpl
import com.example.weatherforecast.data.local.WeatherDatabase
import com.example.weatherforecast.data.remote.ApiHelperImpl
import com.example.weatherforecast.databinding.HomeFragmentBinding
import com.example.weatherforecast.pojo.*
import com.example.weatherforecast.presentation.view_model.WeatherViewModel
import com.example.weatherforecast.presentation.views.alerts.AddAlertDialog
import com.example.weatherforecast.presentation.views.alerts.AlertReceiver
import com.example.weatherforecast.presentation.views.home.location.gps.LocationViewModel
import com.example.weatherforecast.presentation.views.home.location.maps.MapsFragmentSH
import com.example.weatherforecast.utils.CheckInternet
import com.example.weatherforecast.utils.Status

import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var sharedPreferences: SharedPreferences
    private var lang: String = "en"
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var progressDialog: ProgressDialog
    var lat: String? = null
    var lon: String? = null
    private lateinit var prefs:SharedPreferences
    private lateinit var workManager: WorkManager


    companion object {
        val PERMISSION_LOCATION_ID = 1010
        val TAG = "HomeFragment"
        fun newInstance() =
            HomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        binding.root.visibility = View.GONE
        val isOpenSettings = sharedPreferences.getString("isOpen", "not open")
        val latLng: String? = sharedPreferences.getString(MapsFragmentSH.MAPS_LATLON, " ")

         prefs = PreferenceManager.getDefaultSharedPreferences(context)

        lang = prefs.getString("lang", "en")!!

        if (!isOpenSettings.equals("open")) {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        } else {
            if (prefs.getBoolean("gps", false)) {
                getLastLocation()
            } else if (sharedPreferences.getInt(AddAlertDialog.LOCATION_CHOICE, 1) == 2) {

                val splite = latLng!!.split(",")
                lat = splite[0]
                lon = splite[1]
                checkInternetAndDisplayDate(lat!!, lon!!)
            } else {
                Toast.makeText(requireContext(), "Please Determine Location", Toast.LENGTH_LONG)
                    .show()
            }
            setUpAlerts()
        }

        return binding.root
    }
    private fun setUpAlerts(){
        if (prefs.getBoolean("alert", true) && sharedPreferences.getString("alerts","yes").equals("yes")) {
            setUpFetchFromApiWorker()
            editor.putString("alerts","no")
            editor.commit()
            editor.apply()
        }else if(!prefs.getBoolean("alert", true)){
            val requestCodeListJson = sharedPreferences.getString("requestsOfAlerts", " ")
            val type: Type = object : TypeToken<List<Int>>() {}.type

            if (Gson().fromJson<List<Int>>(requestCodeListJson,type) != null) {
                var requestCodeList:List<Int> = Gson().fromJson(requestCodeListJson,type)
                Log.v("cancel", requestCodeListJson.toString())
                for (requestCodeItem in requestCodeList) {
                    Log.v("cancel", "cancel")
                    cancelAlarm(requestCodeItem)

                }
                workManager.cancelAllWorkByTag("PeriodicWork")
                editor.putString("alerts", "yes")
                editor.commit()
                editor.apply()
            }

        }
    }
    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
    private fun setUpFetchFromApiWorker(){
        val data: Data = Data.Builder().putString("lat",lat).putString("lon",lon).putString("lang",lang).build()
        val constrains = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val repeatingRequest = PeriodicWorkRequest.Builder(
            FetchFromApiWorker::class.java,1,
            TimeUnit.HOURS)
            .addTag("PeriodicWork")
            .setConstraints(constrains)
            .setInputData(data)
            .build()
        workManager.enqueue(repeatingRequest)
        workManager.getWorkInfoByIdLiveData(repeatingRequest.id).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.v("state",it.state.name)
        })
    }

    override fun onStart() {
        super.onStart()

    }


    private fun observeDataFromApi() {
        viewModel.fetchWeather().observe(viewLifecycleOwner, Observer {
            it?.let {

                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let {
                            Log.v("myTest", it.toString())
                            val weatherModelBD = prepareDataForDB(it)
                            GlobalScope.launch {
                                Dispatchers.IO
                                Log.v("myTest", weatherModelBD.toString())
                                viewModel.addweather(weatherModelBD)
                                withContext(Dispatchers.Main) {
                                    getWeatherModelFromDB()

                                }
                            }
                        }
                    }
                    Status.LOADING -> {
                            Log.v("loading","loading")
                        binding.root.visibility = View.INVISIBLE
                        progressDialog.show()
                    }
                    Status.ERROR -> {
                        //Handle Error
                        binding.root.visibility = View.INVISIBLE
                        progressDialog.dismiss()

                        Toast.makeText(requireContext(), "Connection Error", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })
    }

    private fun getWeatherModelFromDB() {
        viewModel.getweatherFromDB()
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    binding.weatherModelDB = it
                    setDataForRV(it)
                    progressDialog.dismiss()
                    binding.root.visibility = View.VISIBLE
                }
            })
    }


    private fun setDataForRV(weatherModelBD: WeatherModelBD) {
        initRecyclerViewOfHourly(weatherModelBD.hourlyWeather)
        initRecyclerViewOfDaily(weatherModelBD.dailyWeather)
    }

    private fun prepareDataForDB(weatherModel: WeatherModel): WeatherModelBD {
        val hourlyListDB = arrayListOf<HourlyWeather>()
        for (hourlyItem in weatherModel.hourly) {
            hourlyListDB.add(
                HourlyWeather(
                    hourlyItem.dt,
                    hourlyItem.temp,
                    hourlyItem.weather[0].icon
                )
            )
        }
        val dailyListDW = arrayListOf<DailyWeather>()
        for (dailyItem in weatherModel.daily) {
            dailyListDW.add(
                DailyWeather(
                    dailyItem.dt,
                    dailyItem.temp.min,
                    dailyItem.temp.max,
                    dailyItem.weather[0].icon,
                    dailyItem.weather[0].description
                )
            )
        }
        val alertList = arrayListOf<AlertsWeather>()
        if (weatherModel.alerts != null) {
            for (alertItem in weatherModel.alerts) {
                alertList.add(
                    AlertsWeather(
                        alertItem.sender_name,
                        alertItem.event,
                        alertItem.start,
                        alertItem.end,
                        alertItem.description
                    )
                )
            }
        }


        val weatherModelBD = WeatherModelBD(
            0,
            weatherModel.current.dt,
            weatherModel.current.temp,
            weatherModel.current.pressure,
            weatherModel.current.humidity,
            weatherModel.current.clouds,
            weatherModel.current.wind_speed,
            weatherModel.current.weather[0].icon,
            weatherModel.current.weather[0].description,
            weatherModel.lat.toString() + "+" + weatherModel.lon,
            weatherModel.timezone,
            hourlyListDB,
            dailyListDW,
            alertList
        )
        return weatherModelBD
    }


    fun init() {

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        workManager = WorkManager.getInstance(requireContext())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        binding.hourlyRv.setHasFixedSize(true)
        binding.hourlyRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        hourlyAdapter = HourlyAdapter()
        binding.dailyRv.setHasFixedSize(true)
        binding.dailyRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        dailyAdapter = DailyAdapter()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading Data, Please Wait")
        progressDialog.dismiss()
        viewModel = ViewModelProvider(
            this).get(WeatherViewModel::class.java)
    }

    private fun setupViewModel(lat: String, lng: String) {
        Log.v("myTest", lat)
        //"68.3963", "36.9419"
        viewModel.setData(lat,lng,lang,ApiHelperImpl(RetrofitClient.getApiService()),
            DatabaseHelperImpl(WeatherDatabase.getInstance(requireContext())))

    }

    private fun checkInternetAndDisplayDate(lat: String, lng: String) {

        Log.v("myTest", lat)
        setupViewModel(lat, lng)
        if (CheckInternet.hasNetworkAvailable(requireContext())) {
            Log.v("home", "internet")
            observeDataFromApi()
        } else {
            Log.v("locatuionChoice", "oofline")
            getWeatherModelFromDB()
        }
    }

    private fun getLastLocation() {

        if (checkPermissions()) {
            locationViewModel.getLocationData().observe(viewLifecycleOwner, Observer {
                Log.v("location: ", it.lat + "lon: " + it.lon)
                lat = it.lat
                lon = it.lon
                editor.putString("GPS_LAT", it.lat)
                editor.putString("GPS_LON", it.lon)
                editor.apply()
                editor.commit()
                Log.v("myTest", it.lat)
                checkInternetAndDisplayDate(it.lat, it.lon)
            })
        } else {
            requestPermissions()
        }

    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {


                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_LOCATION_ID
                )
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_LOCATION_ID
                )
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_LOCATION_ID) {
            if ((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission granted
                getLastLocation()
            } else {
                // permission not granted
                Toast.makeText(requireContext(), "Please determine Location", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    private fun initRecyclerViewOfHourly(hourlyList: List<HourlyWeather>) {
        hourlyAdapter.setDataAndContext(hourlyList, requireContext())
        binding.hourlyRv.adapter = hourlyAdapter

    }

    private fun initRecyclerViewOfDaily(dailyList: List<DailyWeather>) {
        dailyAdapter.setDataAndContext(dailyList, requireContext())
        binding.dailyRv.adapter = dailyAdapter

    }


}