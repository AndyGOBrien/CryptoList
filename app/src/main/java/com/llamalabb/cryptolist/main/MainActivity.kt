package com.llamalabb.cryptolist.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.llamalabb.cryptolist.R
import com.llamalabb.cryptolist.main.tabs.list.CoinListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, CoinListFragment())
                .commit()
    }
}
