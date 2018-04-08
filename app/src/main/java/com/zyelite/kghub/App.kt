package com.zyelite.kghub

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.zyelite.kghub.dagger.component.ApiComponent
import com.zyelite.kghub.dagger.component.DaggerApiComponent
import com.zyelite.kghub.dagger.module.ApiModule
import io.realm.Realm


/**
 * @author zy
 * @date 2018/2/28
 * @des App
 */
class App : MultiDexApplication() {


    //初始化块
    init {
        instance = this

    }

    //声明静态变量
    companion object {
        lateinit var instance: App;

        private var NETCOMPONENT: ApiComponent? = null

        fun getNetComponent(): ApiComponent {
            if (NETCOMPONENT == null) {
                NETCOMPONENT = DaggerApiComponent
                        .builder()
                        .apiModule(ApiModule(instance))
                        .build()
            }
            return NETCOMPONENT!!
        }

    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化Realm数据库
        Realm.init(this)
        //配置Realm数据库
//        val config = RealmConfiguration.Builder()
//                .schemaVersion(1)
//                .name(resources.getString(R.string.app_name)).build()
//        Realm.setDefaultConfiguration(config)
    }
}