package com.llamalabb.cryptolist.models.coin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by andy on 2/20/18.
 */
data class CoinList(
        @SerializedName("Response")
        @Expose
        var response: String? = null,

        @SerializedName("Message")
        @Expose
        var message: String? = null,

        @SerializedName("BaseImageUrl")
        @Expose
        var baseImageUrl: String? = null,

        @SerializedName("BaseLinkUrl")
        @Expose
        var baseLinkUrl: String? = null,

        @SerializedName("DefaultWatchList")
        @Expose
        var defaultWatchList: DefaultWatchList? = null,

        @SerializedName("Data")
        @Expose
        var data: LinkedHashMap<String, Coin>? = null,

        @SerializedName("Type")
        @Expose
        var type: Int? = null
)