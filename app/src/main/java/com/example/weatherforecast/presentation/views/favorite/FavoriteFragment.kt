package com.example.weatherforecast.presentation.views.favorite

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitandcoroutine.data.remote.RetrofitClient
import com.example.weatherforecast.R
import com.example.weatherforecast.data.local.DatabaseHelperImpl
import com.example.weatherforecast.data.local.WeatherDatabase
import com.example.weatherforecast.data.remote.ApiHelperImpl
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.pojo.DailyWeather
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.HourlyWeather
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.presentation.view_model.FavoriteViewModel
import com.example.weatherforecast.presentation.view_model.WeatherViewModel
import com.example.weatherforecast.utils.CheckInternet
import com.example.weatherforecast.utils.Status

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteFragment : Fragment(), FavoriteAdapter.OnItemClickListener {
    private var latLng: LatLng? = null
    private lateinit var viewModel: WeatherViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var bindingFavoriteFragment: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteList: List<FavoriteModelBD>
    private var lang:String = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latLng = it.getParcelable<LatLng>("latLng")!!


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingFavoriteFragment =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        init()
        bindingFavoriteFragment.lifecycleOwner = viewLifecycleOwner


        if (CheckInternet.hasNetworkAvailable(requireContext())) {
            if (latLng != null) {
                setUpViewModelOfWeather()
                Log.v("lat", latLng!!.latitude.toString() + " ")

                observeDataFromApi()
            } else {
                setUpViewModeOfFav()
                getFavoriteModelFromDB()
            }
        } else {
            setUpViewModeOfFav()
            getFavoriteModelFromDB()
        }
        return bindingFavoriteFragment.root
    }

    private fun setUpViewModelOfWeather() {

        viewModel.setData(latLng!!.latitude.toString(),
            latLng!!.longitude.toString(), lang,
            ApiHelperImpl(RetrofitClient.getApiService()),
            DatabaseHelperImpl(WeatherDatabase.getInstance(requireContext())))
        setUpViewModeOfFav()
    }

    private fun setUpViewModeOfFav() {

        favoriteViewModel.setDataFromDB( ApiHelperImpl(RetrofitClient.getApiService()),
            DatabaseHelperImpl(
                WeatherDatabase.getInstance(requireContext())
            ))
    }

    private fun init() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        lang = prefs.getString("lang", "en")!!
        bindingFavoriteFragment.favoriteRv.setHasFixedSize(true)
        bindingFavoriteFragment.favoriteRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        favoriteAdapter = FavoriteAdapter()
        favoriteAdapter.setOnItemClickListener(this)
        favoriteList = ArrayList()
        favoriteViewModel = ViewModelProvider(
            this
        ).get(FavoriteViewModel::class.java)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(bindingFavoriteFragment.favoriteRv)

        bindingFavoriteFragment.fab.setOnClickListener(View.OnClickListener {
            Log.v("hhh","fab")
            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_favoriteMaps)
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            FavoriteFragment()
                .apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    private fun observeDataFromApi() {
        viewModel.fetchWeather().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.v("status:", "sussess")
                        // progressBar.visibility = View.GONE
                        it.data?.let {
                            val favoriteBD = prepareDataForDB(it)
                            GlobalScope.launch {
                                Dispatchers.IO
                                favoriteViewModel.addFavoriteDB(favoriteBD)

                                withContext(Dispatchers.Main) {
                                    getFavoriteModelFromDB()
                                }
                            }
                        }
                    }
                    Status.LOADING -> {
                        Log.v("status:", "loading")
//                        progressBar.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        //Handle Error
                        //progressBar.visibility = View.GONE
                        Log.v("status:", "error")
                        getFavoriteModelFromDB()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun initRecyclerViewOfFavorite(favoriteModelDBList: MutableList<FavoriteModelBD>) {
        favoriteAdapter.setDataAndContext(favoriteModelDBList, requireContext())
        bindingFavoriteFragment.favoriteRv.adapter = favoriteAdapter

    }


    private fun getFavoriteModelFromDB() {
        favoriteViewModel.getFavoriteFromDB()
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    favoriteList = it
                    setDataForRV(it)
                }
            })
    }

    private fun setDataForRV(favoriteModelBD: MutableList<FavoriteModelBD>) {
        initRecyclerViewOfFavorite(favoriteModelBD)
    }

    private fun prepareDataForDB(weatherModel: WeatherModel): FavoriteModelBD {
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

        val favoriteModelBD = FavoriteModelBD(
            weatherModel.current.dt,
            weatherModel.current.temp,
            weatherModel.current.pressure,
            weatherModel.current.humidity,
            weatherModel.current.clouds,
            weatherModel.current.wind_speed,
            weatherModel.current.weather[0].icon,
            weatherModel.current.weather[0].description,
            weatherModel.lat.toString() + "+" + weatherModel.lon,
            hourlyListDB,
            dailyListDW
        )
        return favoriteModelBD
    }

    override fun onClick(position: Int) {
        val favoriteItem = bundleOf("favoriteItem" to favoriteList.get(position))
        findNavController().navigate(
            R.id.action_favoriteFragment_to_detailsFavoriteFragment,
            favoriteItem
        )
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
               AlertDialog.Builder(activity).setMessage("Do You Want to Delete this Favorite ?!")
                    .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, id -> //when delete item from tripDatabase, add tripHistoryDB
                            deleteFavoriteItemFromDB(favoriteAdapter.getItemByVH(viewHolder))
                            favoriteAdapter.removeFavoriteItem(viewHolder)
                        })
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener { dialog, id ->
                            getFavoriteModelFromDB()
                        }).show()

            }
        }

    // delete favorite item
    fun deleteFavoriteItemFromDB(favoriteModelBD: FavoriteModelBD) {
    favoriteViewModel.deleteFavItem(favoriteModelBD)
    }


}