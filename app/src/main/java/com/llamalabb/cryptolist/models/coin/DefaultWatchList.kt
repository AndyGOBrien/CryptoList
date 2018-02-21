package com.llamalabb.cryptolist.models.coin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by andy on 2/20/18.
 */
data class DefaultWatchList(
        @SerializedName("CoinIs")
        @Expose
        var coinIds: String? = null,

        @SerializedName("Sponsored")
        @Expose
        var sponsored: String? = null
) {
}