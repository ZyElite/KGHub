package com.zyelite.kghub.ui

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.zyelite.kghub.App
import com.zyelite.kghub.R
import com.zyelite.kghub.dagger.component.DaggerUiComponent
import com.zyelite.kghub.http.api.UserService
import com.zyelite.kghub.model.User
import com.zyelite.kghub.utils.ImageUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.text.SimpleDateFormat
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        inject()
        getUserInfo()
        nav_view_start.inflateMenu(R.menu.activity_main_drawer)
    }

    private fun getUserInfo() {
        userService.getUserInfo(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    //执行成功
                    val body: User = it.body() as User
                    Log.e("KGHub", body.toString())
                    ImageUtil.circle(this, body.getAvatarUrl(), avatar)
                    userName.text = body.getLogin()
                    val format = SimpleDateFormat("yyyy-MM-dd")
                    mail.text = if (TextUtils.isEmpty(body.getEmail())) format.format(body.getCreatedAt()) else body.getEmail()
                }, {
                    //执行失败
                    Log.e("KGHub", "执行失败")
                })
    }

    private fun inject() {
        DaggerUiComponent.builder()
                .apiComponent(App.getNetComponent())
                .build()
                .inject(this)
    }

    /**
     * 初始化toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_toolbar_open, R.string.navigation_toolbar_close)
        mDrawerToggle.syncState()
        mDrawerToggle.isDrawerSlideAnimationEnabled = false
        mDrawerLayout.addDrawerListener(mDrawerToggle)
    }


}
