package com.example.weatherforecast.presentation.views.details_favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentDetailsFavoriteBinding
import com.example.weatherforecast.pojo.DailyWeather
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.HourlyWeather
import com.example.weatherforecast.presentation.views.home.DailyAdapter
import com.example.weatherforecast.presentation.views.home.HourlyAdapter

class DetailsFavoriteFragment : Fragment() {
    private lateinit var binding: FragmentDetailsFavoriteBinding
    private var favoriteItem: FavoriteModelBD? = null
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            favoriteItem = it.getParcelable<FavoriteModelBD>("favoriteItem")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_details_favorite, container, false)
        init()
        binding.favoriteModelDB = favoriteItem

        setUpRVOfHourly(favoriteItem!!.hourlyWeather)
        setUpRVOfDaily(favoriteItem!!.dailyWeather)
        return binding.root
    }

    private fun init() {
        binding.hourlyFRv.setHasFixedSize(true)
        binding.hourlyFRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        hourlyAdapter = HourlyAdapter()
        binding.dailyFRv.setHasFixedSize(true)
        binding.dailyFRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        dailyAdapter = DailyAdapter()
    }

    private fun setUpRVOfHourly(hourlyWeather: List<HourlyWeather>) {

        hourlyAdapter.setDataAndContext(hourlyWeather, requireContext())
        binding.hourlyFRv.adapter = hourlyAdapter

    }

    private fun setUpRVOfDaily(dailyWeather: List<DailyWeather>) {

        dailyAdapter.setDataAndContext(dailyWeather, requireContext())
        binding.dailyFRv.adapter = dailyAdapter

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DetailsFavoriteFragment()
                .apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}