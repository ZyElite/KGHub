package com.zyelite.kghub.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @author zy
 * @date 2018/3/9
 * @des ImageUtil
 */
object ImageUtil {

    /**
     *加载圆形图片 从缓存拿
     */
    fun circle(context: Context, url: String, img: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().onlyRetrieveFromCache(false)).into(img)
    }

    /**
     *加载图片 从缓存拿
     */
    fun load(context: Context, url: String, img: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions().onlyRetrieveFromCache(false)).into(img)
    }
}