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
            coinList?.let { coinListAdapter.setCoinList(it) }
        })
    }
}