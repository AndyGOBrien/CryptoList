package com.llamalabb.cryptolist.models.coin

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.llamalabb.cryptolist.BR
import com.llamalabb.cryptolist.GlideApp


/**
 * Created by andy on 2/20/18.
 */
data class Coin(
        @SerializedName("Id")
        @Expose
        private var _id: String? = null,

        @SerializedName("Url")
        @Expose
        private var _url: String? = null,

        @SerializedName("ImageUrl")
        @Expose
        private var _imageUrl: String? = null,

        @SerializedName("Name")
        @Expose
        private var _name: String? = null,

        @SerializedName("Symbol")
        @Expose
        private var _symbol: String? = null,

        @SerializedName("CoinName")
        @Expose
        private var _coinName: String? = null,

        @SerializedName("FullName")
        @Expose
        private var _fullName: String? = null,

        @SerializedName("Algorithm")
        @Expose
        private var _algorithm: String? = null,

        @SerializedName("ProofType")
        @Expose
        private var _proofType: String? = null,

        @SerializedName("FullyPremined")
        @Expose
        private var _fullyPremined: String? = null,

        @SerializedName("TotalCoinSupply")
        @Expose
        private var _totalCoinSupply: String? = null,

        @SerializedName("PreMinedValue")
        @Expose
        private var _preMinedValue: String? = null,

        @SerializedName("TotalCoinsFreeFloat")
        @Expose
        private var _totalCoinsFreeFloat: String? = null,

        @SerializedName("SortOrder")
        @Expose
        private var _sortOrder: String? = null,

        @SerializedName("Sponsored")
        @Expose
        private var _sponsored: Boolean? = null,

        @SerializedName("IsTrading")
        @Expose
        private var _trading: Boolean? = null,

        private var _marketCap: String = "",
        private var _price: String = "",
        private var _volume: String = ""

) : BaseObservable() {
    var price: String
        @Bindable get() = _price
        set(value) {
            _price = value
            notifyPropertyChanged(BR.price)
        }
    var id: String?
        @Bindable get() = _id
        set(value) {
            _id = value
            notifyPropertyChanged(BR.id)
        }

    var url: String?
        @Bindable get() = _url
        set(value) {
            _url = value
            notifyPropertyChanged(BR.url)
        }

    var imageUrl: String?
        @Bindable get() = "https://www.cryptocompare.com"+_imageUrl
        set(value) {
            _imageUrl = value
            notifyPropertyChanged(BR.imageUrl)
        }

    var name: String?
        @Bindable get() = _name
        set(value) {
            _name = value
            notifyPropertyChanged(BR.name)
        }

    var symbol: String?
        @Bindable get() = _symbol
        set(value) {
            _symbol = value
            notifyPropertyChanged(BR.symbol)
        }

    var coinName: String?
        @Bindable get() = _coinName
        set(value) {
            _coinName = value
            notifyPropertyChanged(BR.coinName)
        }

    var fullName: String?
        @Bindable get() = _fullName
        set(value) {
            _fullName = value
            notifyPropertyChanged(BR.fullName)
        }

    var algorithm: String?
        @Bindable get() = _algorithm
        set(value) {
            _algorithm = value
            notifyPropertyChanged(BR.algorithm)
        }

    var proofType: String?
        @Bindable get() = _proofType
        set(value) {
            _proofType = value
            notifyPropertyChanged(BR.proofType)
        }

    var fullyPremined: String?
        @Bindable get() = _fullyPremined
        set(value) {
            _fullyPremined = value
            notifyPropertyChanged(BR.fullyPremined)
        }

    var totalCoinSupply: String?
        @Bindable get() = _totalCoinSupply
        set(value) {
            _totalCoinSupply = value
            notifyPropertyChanged(BR.totalCoinSupply)
        }

    var preMinedValue: String?
        @Bindable get() = _preMinedValue
        set(value) {
            _preMinedValue = value
            notifyPropertyChanged(BR.preMinedValue)
        }

    var totalCoinsFreeFloat: String?
        @Bindable get() = _totalCoinsFreeFloat
        set(value) {
            _totalCoinsFreeFloat = value
            notifyPropertyChanged(BR.totalCoinsFreeFloat)
        }

    var sortOrder: String?
        @Bindable get() = _sortOrder
        set(value) {
            _sortOrder = value
            notifyPropertyChanged(BR.sortOrder)
        }

    var sponsored: Boolean?
        @Bindable get() = _sponsored
        set(value) {
            _sponsored = value
            notifyPropertyChanged(BR.sponsored)
        }

    var trading: Boolean?
        @Bindable get() = _trading
        set(value) {
            _trading = value
            notifyPropertyChanged(BR.trading)
        }

    var marketCap: String
        @Bindable get() = _marketCap
        set(value) {
            _marketCap = value
            notifyPropertyChanged(BR.marketCap)
        }

    var volume: String
        @Bindable get() = _volume
        set(value) {
            _volume = value
            notifyPropertyChanged(BR.volume)
        }
}