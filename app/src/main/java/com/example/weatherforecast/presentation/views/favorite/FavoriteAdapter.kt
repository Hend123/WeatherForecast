package com.example.weatherforecast.presentation.views.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FavoriteItemBinding
import com.example.weatherforecast.pojo.FavoriteModelBD

class FavoriteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var favoriteList: MutableList<FavoriteModelBD>
    private lateinit var context: Context
    private lateinit var onItemClickListener: OnItemClickListener


    init {
        favoriteList = ArrayList()

    }
    /*
    SetUp Of Delete Item
     */
    fun getFavoriteList():MutableList<FavoriteModelBD> = favoriteList
    fun getItemByVH(viewHolder: RecyclerView.ViewHolder):FavoriteModelBD{
      return favoriteList.get(viewHolder.adapterPosition)
    }
    fun removeFavoriteItem(viewHolder: RecyclerView.ViewHolder){
       favoriteList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

    }
    interface OnItemClickListener {
        fun onClick(position: Int)
    }
    fun setOnItemClickListener(onItemClickListLener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListLener
    }

    fun setDataAndContext(favoriteList: MutableList<FavoriteModelBD>, context: Context) {
        this.favoriteList = favoriteList
        this.context = context
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteItemBinding.inflate(inflater)
        return FavoriteItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (favoriteList.size == 0) 0 else favoriteList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as FavoriteItemsViewHolder
        viewHolder.bind(favoriteList[position])
    }

    inner class FavoriteItemsViewHolder(val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(item: FavoriteModelBD) {
            binding.favoriteItem = item
            binding.root.setOnClickListener(this)
            binding.root.setClickable(true)
            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            onItemClickListener.onClick(adapterPosition)
        }
    }
}