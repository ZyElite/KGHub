package com.zyelite.kghub

import android.app.Application
import com.zyelite.kghub.dagger.component.ApiComponent
import com.zyelite.kghub.dagger.component.DaggerApiComponent
import com.zyelite.kghub.dagger.module.ApiModule
import io.realm.Realm

/**
 * @author zy
 * @date 2018/2/28
 * @des App
 */
class App : Application() {
    //初始化块
    init {
        instance = this
        Realm.init(this)
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
        super.onCreate();
    }

}