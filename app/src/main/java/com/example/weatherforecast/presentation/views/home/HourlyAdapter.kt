package com.example.weatherforecast.presentation.views.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.HourlyItemBinding
import com.example.weatherforecast.pojo.HourlyWeather

class HourlyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private  var hourlyList: List<HourlyWeather>
    private lateinit var context: Context


    init {
        hourlyList = ArrayList()
    }


    fun setDataAndContext(hourlyWeather: List<HourlyWeather>, context: Context) {
        this.hourlyList = hourlyWeather
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HourlyItemBinding.inflate(inflater)
        return HourlyItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as HourlyItemsViewHolder
        viewHolder.bind(hourlyList[position])
    }

    inner class HourlyItemsViewHolder(val binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyWeather) {
            binding.hourlyItem = item
          // binding.weatherIcon = item.icon
            binding.executePendingBindings()
        }
    }
}