package com.llamalabb.cryptolist.main.tabs.list

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.llamalabb.cryptolist.R
import com.llamalabb.cryptolist.databinding.ItemCryptoCoinBinding
import com.llamalabb.cryptolist.models.coin.Coin

/**
 * Created by andy on 2/20/18.
 */
class CoinListAdapter : RecyclerView.Adapter<CoinListAdapter.CoinViewHolder>() {

    private var coinList: List<Coin>? = null

    fun setCoinList(newList: List<Coin>){
        if(coinList == null){
            coinList = newList
            notifyItemRangeInserted(0, newList.size)
        } else {
            val result = DiffUtil.calculateDiff(object: DiffUtil.Callback(){
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return coinList!![oldItemPosition].price == newList[newItemPosition].price
                }

                override fun getOldListSize(): Int {
                    return coinList!!.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val price = newList[newItemPosition].price
                    val oldPrice = coinList!![oldItemPosition].price
                    return price == oldPrice
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    val newItem = newList[newItemPosition]
                    val oldItem = coinList!![oldItemPosition]
                    val diffBundle = Bundle()

                    if(newItem.price != oldItem.price){
                        diffBundle.putString("PRICE", newItem.price)
                    }

                    return diffBundle
                }

            })
            this.coinList = newList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val binding: ItemCryptoCoinBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.context),
                        R.layout.item_crypto_coin,
                        parent,
                        false)

        return CoinViewHolder(binding)
    }

    override fun getItemCount(): Int = coinList?.size ?: 0

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.binding.coin = coinList?.get(position)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int, payloads: List<Any>) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        }
        else {
            val o = payloads[0] as Bundle
            for( key: String in o.keySet()){
                if(key == "PRICE"){
                    holder.binding.coin = coinList?.get(position)
                }
            }
        }
    }

    class CoinViewHolder(val binding: ItemCryptoCoinBinding) : RecyclerView.ViewHolder(binding.root)
}