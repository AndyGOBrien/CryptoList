package com.llamalabb.cryptolist.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.llamalabb.cryptolist.models.coin.Coin
import com.llamalabb.cryptolist.models.cryptocompare.CryptoCompareApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by andy on 2/20/18.
 */
object CoinRepository {

    private const val BASE_URL = "https://www.cryptocompare.com/"

    private val url = "https://streamer.cryptocompare.com/"

    val priceMap: HashMap<String, Map<String, Double>> = HashMap()

    val socket = IO.socket(url)

    private val retrofitInstance1 = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    private val retrofitInstance2 = Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val coinPriceApiService = retrofitInstance2.create(CryptoCompareApi::class.java)

    private val coinListApiService = retrofitInstance1
            .create(CryptoCompareApi::class.java)

    init{
        socket.connect()
        socket.on(Socket.EVENT_CONNECT_ERROR, {
            Log.d("Main", "Connection Failure")

        })
        socket.on(Socket.EVENT_DISCONNECT, {
            Log.d("Main", "Socket Disconnect")
        })
        socket.on(Socket.EVENT_CONNECT, {
            Log.d("Main", "Connection Successful")
            socket.emit("SubAdd", JSONObject("""{subs: ["5~CCCAGG~BTC~USD"]}"""))
        })

        socket.on("m", {
            it.forEach { thing -> Log.d("Main", thing.toString()) }
        })
    }

    private fun getCoinListFromApi() = coinListApiService.getCoinList()

    private fun getCoinPricesFromApi(str: String) = coinPriceApiService.getCoinPrices(str)

    fun getCoinList(): LiveData<List<Coin>> {
        val data: MutableLiveData<List<Coin>> = MutableLiveData()
        getCoinListFromApi()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    val tempList = ArrayList<Coin>()
                    it.data?.forEach { tempList.add(it.value) }
                    val tempList2 = tempList.sortedWith(compareBy { it.sortOrder?.toInt() }).subList(0, 50)
                    emitCoins(tempList2)
                    data.value = tempList2
                    setCoinPrices(data)
                }
        return data
    }

    private fun emitCoins(list: List<Coin>){
        var temp: String = ""
        list.forEach {
            if(it.symbol != "BTC")
                temp += "5~CCCAGG~${it.symbol}~BTC,"
        }
        temp.trim(',')
        socket.emit("SubAdd", JSONObject("""{subs: [$temp]}"""))
    }

    private fun setCoinPrices(list: LiveData<List<Coin>>){
        var temp = ""
        list.value?.forEach{
            temp = temp + it.symbol + ","
        }
        temp = temp.trim(',')
        getCoinPricesFromApi(temp)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { map->
                    list.value?.forEach { coin ->
                        coin.price = "$"+ map.get(coin.symbol)?.get("USD")
                    }
                }
    }



}