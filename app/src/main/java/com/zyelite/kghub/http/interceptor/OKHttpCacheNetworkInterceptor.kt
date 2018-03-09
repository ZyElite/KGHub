package com.zyelite.kghub.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 *
 *  OkHttp3 缓存网络拦截器
 * 配合 {@link retrofit2.http.Header}、{@link retrofit2.http.Headers}
 * 当服务器返回 Cache-Control: must-revalidate 时使用（慎用）
 *
 * @author zy
 * @date 2018/3/1
 * @des OKHttpCacheNetworkInterceptor
 *

 */
class OKHttpCacheNetworkInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
                .newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .build()
    }
}