package com.example.weatherforecast.presentation.views.alerts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.AlertItemBinding
import com.example.weatherforecast.databinding.DailyItemBinding
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.DailyWeather
import com.example.weatherforecast.pojo.FavoriteModelBD

class AlertAdapter  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private  var alertList: MutableList<AlertDB>
    private lateinit var context: Context
    private lateinit var binding: AlertItemBinding




    init {
        alertList = ArrayList()
    }


    fun setDataAndContext(alertList: MutableList<AlertDB>, context: Context) {
        this.alertList = alertList
        this.context = context
    }
    /*
   SetUp Of Delete Item
    */
    fun getAlertList():MutableList<AlertDB> = alertList
    fun getItemByVH(viewHolder: RecyclerView.ViewHolder): AlertDB {
        return alertList.get(viewHolder.adapterPosition)
    }
    fun removeAlertItem(viewHolder: RecyclerView.ViewHolder){
        alertList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
       // val binding = AlertItemBinding.inflate(inflater)
        binding = DataBindingUtil.inflate(inflater, R.layout.alert_item, parent, false)

        return AlertItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as AlertItemsViewHolder
        viewHolder.bind(alertList[position])
    }

    inner class AlertItemsViewHolder(val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDB) {
            binding.alertItem = item
            binding.executePendingBindings()
        }
    }
}