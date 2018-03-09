package com.zyelite.kghub.dagger.module

import android.content.Context

/**
 * @author zy
 * @date 2018/3/7
 * @des SPModule
 */
//@Module(includes = [(AppModule::class)])
class SPModule() {

//    @Provides
    fun privateSp(context: Context, name: String) = context.getSharedPreferences(name, Context.MODE_PRIVATE)

//    @Provides
    fun privateName() = "KGHub"

}