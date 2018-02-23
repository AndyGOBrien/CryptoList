package com.llamalabb.cryptolist.main.tabs.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.llamalabb.cryptolist.R
import com.llamalabb.cryptolist.databinding.FragmentCoinListBinding
import com.llamalabb.cryptolist.models.CoinRepository
import com.llamalabb.cryptolist.models.coin.Coin

/**
 * Created by andy on 2/20/18.
 */
class CoinListFragment : Fragment() {

    private val coinListAdapter = CoinListAdapter()
    private lateinit var binding: FragmentCoinListBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coin_list, container, false)
        binding.coinList.adapter = coinListAdapter
        binding.coinList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(CoinListViewModel::class.java)
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: CoinListViewModel){
        viewModel.getCoinListObservable().observe(this, Observer<List<Coin>> { coinList ->
            coinList?.let{ list ->
                coinListAdapter.setCoinList(list)
                CoinRepository.socket.on("m", { rawStreamDataList ->
                    rawStreamDataList.forEach{
                        val rawStreamItem = it.toString()
                        val streamData: List<String> = rawStreamItem.split('~')
                        val streamType = streamData[0]
                        val coinSymbol = streamData[2]
                        val updateType = streamData[4]
                        if(streamType == "5" && (updateType == "1" || updateType == "2")){
                            val updatedPrice = streamData[5]
                            val index = list.getCoinIndex(coinSymbol)
                            val btcPrice = list.getBtcPrice()
                            if(list[index].symbol == "BTC"){
                                list[index].price = updatedPrice.formatStringDoubleTwoDecimalPlaces()
                            } else if(!btcPrice.isEmpty()) {
                                list[index].price = getAltPriceInFiat(updatedPrice, btcPrice)
                            }

                            Log.d("PRICE UPDATE", "${list[index].symbol}: ${list[index].price}")
                        }
                    }
                })
            }
        })
    }

    private fun String.formatStringDoubleTwoDecimalPlaces() = "$" + "%.2f".format(this.toDouble())
    private fun List<Coin>.getCoinIndex(symbol: String) = this.single { it.symbol == symbol }.sortOrder!!.toInt() - 1
    private fun List<Coin>.getBtcPrice() = this.single { it.symbol == "BTC" }.price
    private fun getAltPriceInFiat(rawAltPriceInBTC: String, rawBTCPrice: String): String{
        val altPrice = rawAltPriceInBTC.trim('$').toDouble()
        val btcPrice = rawBTCPrice.trim('$').toDouble()
        val price = altPrice * btcPrice
        return when{
            price >= 10                -> "$"+"%.2f".format(price)
            price < 10 && price >= 1   -> "$"+"%.3f".format(price)
            price < 1 && price >= .01  -> "$"+"%.5f".format(price)
            else                       -> "$"+"%.6f".format(price)
        }
    }
}