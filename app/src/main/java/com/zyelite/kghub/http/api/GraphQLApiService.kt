package com.zyelite.kghub.http.api

/**
 * @author zy
 * @date 2018/2/24
 * @des ApiService
 */
interface GraphQLApiService {
    val base: String get() = "https://api.github.com/graphql"
}