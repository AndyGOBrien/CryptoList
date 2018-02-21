package com.llamalabb.cryptolist.main;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.llamalabb.cryptolist.GlideApp;

/**
 * Created by andy on 2/21/18.
 */

public class ImageBindingAdapter {
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url){
        GlideApp.with(imageView.getContext()).load(url).into(imageView);
    }
}
