package com.example.weatherforecast.presentation.views.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.DailyItemBinding
import com.example.weatherforecast.pojo.DailyWeather

class DailyAdapter   : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private  var dailyList: List<DailyWeather>
    private lateinit var context: Context



    init {
        dailyList = ArrayList()
    }


    fun setDataAndContext(dailyList: List<DailyWeather>, context: Context) {
        this.dailyList = dailyList
        this.context = context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DailyItemBinding.inflate(inflater)
        return DailyItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dailyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = holder as DailyItemsViewHolder
        viewHolder.bind(dailyList[position])
    }

    inner class DailyItemsViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyWeather) {
            binding.dailyItem = item
            binding.executePendingBindings()
        }
    }
}