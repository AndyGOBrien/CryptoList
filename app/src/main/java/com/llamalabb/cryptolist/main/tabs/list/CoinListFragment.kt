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
import io.socket.emitter.Emitter

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
        viewModel.getCoinListObservable().observe(this, Observer<List<Coin>> { list ->
            list?.let{ list ->
                coinListAdapter.setCoinList(list)
                CoinRepository.socket.on("m", Emitter.Listener {
                    it.forEach{
                        val raw: String = it.toString()
                        val temp: List<String> = raw.split('~')
                        if(temp[0] == "5" && (temp[4] == "1" || temp[4] == "2")){
                            val index = list.single { it.symbol == temp[2] }.sortOrder!!.toInt() - 1
                            val btcPrice = list.single { it.symbol == "BTC" }.price
                            if(list[index].symbol == "BTC"){
                                list[index].price = "$"+"%.2f".format(temp[5].toDouble())
                            } else if(!btcPrice.isEmpty()) {
                                val coinPrice = temp[5].trim('$').toDouble()
                                val btc = btcPrice.trim('$').toDouble()
                                val price = coinPrice * btc
                                when {
                                    price < .1 -> list[index].price = "$"+"%.5f".format(price)
                                    price < 1  -> list[index].price = "$"+"%.4f".format(price)
                                    else       -> list[index].price = "$"+"%.3f".format(price)
                                }
                            }

                            Log.d("PRICE UPDATE", "${list[index].symbol}: ${list[index].price}")
                        }
                    }
                })
            }
        })
    }
}