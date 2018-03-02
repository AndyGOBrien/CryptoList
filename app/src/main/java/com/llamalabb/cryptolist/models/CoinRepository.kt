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
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by andy on 2/20/18.
 */
object CoinRepository {

    private const val BASE_URL = "https://www.cryptocompare.com/"

    private const val SOCKET_URL = "https://streamer.cryptocompare.com/"

    private val socket = IO.socket(SOCKET_URL)

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
        startSocketConnection()
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
                    startSocketUpdates(data)
                }
        return data
    }

    private fun emitCoins(list: List<Coin>){
        var streamSubscriptions = ""
        list.forEach { coin ->
            if(coin.symbol != "BTC")
                streamSubscriptions += getSubscribeString(coin.symbol)
        }
        streamSubscriptions.trim(',')
        socket.emit("SubAdd", getSubscriptionJSONObject(streamSubscriptions))
    }

    private fun getSubscribeString(symbol: String?): String = "5~CCCAGG~$symbol~BTC,"
    private fun getSubscriptionJSONObject(subs: String) = JSONObject("""{subs: [$subs]}""")

    private fun setCoinPrices(liveList: LiveData<List<Coin>>){
        var symbolCsv = ""
        liveList.value?.forEach{
            symbolCsv = symbolCsv + it.symbol + ","
        }
        symbolCsv = symbolCsv.trim(',')
        getCoinPricesFromApi(symbolCsv)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { map->
                    liveList.value?.forEach { coin ->
                        coin.price = "$" + map[coin.symbol]?.get("USD")
                    }
                }
    }

    private fun startSocketConnection(){
        socket.connect()
        socket.on(Socket.EVENT_CONNECT_ERROR, { Log.d("Main", "Connection Failure") })
        socket.on(Socket.EVENT_DISCONNECT, { Log.d("Main", "Socket Disconnect") })
        socket.on(Socket.EVENT_CONNECT, {
            Log.d("Main", "Connection Successful")
            socket.emit("SubAdd", getSubscriptionJSONObject("BTC"))
        })
    }

    private fun startSocketUpdates(liveList: LiveData<List<Coin>>){
        socket.on("m", { rawStreamDataList ->
            rawStreamDataList.forEach{
                val rawStreamItem = it.toString()
                val streamData: List<String> = rawStreamItem.split('~')
                if (streamData.size <= 5) return@on
                val streamType = streamData[0]
                val coinSymbol = streamData[2]
                val updateType = streamData[4]
                liveList.value?.let { list ->
                    if (streamType == "5" && (updateType == "1" || updateType == "2")) {
                        val updatedPrice = streamData[5]
                        val index = list.getCoinIndex(coinSymbol)
                        val btcPrice = list.getBtcPrice()
                        list[index].let { coin ->
                            if (coin.symbol == "BTC") {
                                coin.price = updatedPrice.formatStringDoubleTwoDecimalPlaces()
                            } else if (!btcPrice.isEmpty()) {
                                coin.price = getAltPriceInFiat(updatedPrice, btcPrice)
                            }
                            Log.d("PRICE UPDATE", "${coin.symbol}: ${coin.price}")
                        }
                    }
                }
                Log.d("Main", rawStreamItem)
            }
        })
    }

    private fun String.formatStringDoubleTwoDecimalPlaces() = "$"+"%.2f".format(this.toDouble())

    private fun List<Coin>.getCoinIndex(symbol: String) = this.single { it.symbol == symbol }.sortOrder!!.toInt() - 1

    private fun List<Coin>.getBtcPrice() = this.single { it.symbol == "BTC" }.price

    private fun getAltPriceInFiat(rawAltPriceInBTC: String, rawBTCPrice: String): String{
        val altPrice = rawAltPriceInBTC.trim('$').toDouble()
        val btcPrice = rawBTCPrice.trim('$').toDouble()
        val price = altPrice * btcPrice
        return when{
            price >= 10                -> "$"+"%.2f".format(price)
            price < 10 && price >= 1   -> "$"+"%.3f".format(price)
            price < 1 && price >= .1   -> "$"+"%.4f".format(price)
            price < .1 && price >= .01 -> "$"+"%.5f".format(price)
            else                       -> "$"+"%.6f".format(price)
        }
    }



}