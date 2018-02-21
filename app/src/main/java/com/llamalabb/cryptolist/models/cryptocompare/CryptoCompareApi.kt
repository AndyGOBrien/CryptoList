package com.llamalabb.cryptolist.models.cryptocompare

import com.llamalabb.cryptolist.models.coin.CoinList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by andy on 2/20/18.
 */
interface CryptoCompareApi {

    @GET("api/data/coinlist")
    fun getCoinList() : Observable<CoinList>

    @GET("data/pricemulti")
    fun getCoinPrices(@Query(value="fsyms") fsyms: String, @Query(value="tsyms") tsyms: String = "BTC,USD") : Observable<HashMap<String,HashMap<String,Double>>>
}