package com.zyelite.kghub.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description BaseActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initView())
        init()
    }

    /**
     * 初始化界面
     */
    abstract fun initView(): Int

    /**
     * 初始化 操作
     */
    abstract fun init()
}
