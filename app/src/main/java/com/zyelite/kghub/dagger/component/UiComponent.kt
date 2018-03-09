package com.zyelite.kghub.dagger.component

import com.zyelite.kghub.dagger.module.ApiServiceModule
import com.zyelite.kghub.dagger.scope.UIScope
import com.zyelite.kghub.ui.LoginActivity
import com.zyelite.kghub.ui.MainActivity
import dagger.Component


/**
 * @author zy
 * @date 2018/3/2
 * @des UiComponent
 */
@UIScope
@Component(modules = [(ApiServiceModule::class)], dependencies = [(ApiComponent::class)])
interface UiComponent {
    /**
     * 注入到登录界面ApiServiceModule
     */
    fun inject(loginActivity: LoginActivity)

    /**
     * 注入到主界面ApiServiceModule
     */
    fun inject(mainActivity: MainActivity)
}

