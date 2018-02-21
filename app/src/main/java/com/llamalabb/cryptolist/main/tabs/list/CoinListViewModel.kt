package com.llamalabb.cryptolist.main.tabs.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.llamalabb.cryptolist.models.CoinRepository
import com.llamalabb.cryptolist.models.coin.Coin

/**
 * Created by andy on 2/20/18.
 */
class CoinListViewModel(application: Application) : AndroidViewModel(application) {

    private val coinList: LiveData<List<Coin>> = CoinRepository.getCoinList()

    fun getCoinListObservable() = coinList

}