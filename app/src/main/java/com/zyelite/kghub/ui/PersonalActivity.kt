package com.zyelite.kghub.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.zyelite.kghub.R
import com.zyelite.kghub.adapter.PersonalAdapter
import com.zyelite.kghub.fragment.CommitsFragment
import com.zyelite.kghub.fragment.PersonalInfoFragment
import com.zyelite.kghub.fragment.StarredFragment
import com.zyelite.kghub.fragment.base.BaseFragment
import com.zyelite.kghub.model.UserModel
import com.zyelite.kghub.utils.ImageUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_personal.*
import java.text.SimpleDateFormat

/**
 *个人主页
 */
class PersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        setSupportActionBar(toolbar)
        setTransparentStatusBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({
            finish()
        })
        val realm = Realm.getDefaultInstance()
        val request = realm.where(UserModel::class.java)
        val user = request.findFirst()
        Log.e("asd", user.toString())
        if (user != null) {
            ImageUtil.circle(this, user.getAvatarUrl(), avatar)
            ImageUtil.load(this, user.getAvatarUrl(), aivBg)
            supportActionBar?.title = user.getLogin()
            val format = SimpleDateFormat("yyyy-MM-dd")
            joinTime.text = "加入时间：" + format.format(user.getCreatedAt())
            updateTime.text = "最后更新时间：" + format.format(user.getUpdatedAt())
        }
        val pageList = ArrayList<BaseFragment>()
        pageList.add(PersonalInfoFragment())
        pageList.add(CommitsFragment())
        pageList.add(StarredFragment())
        val personalAdapter = PersonalAdapter(supportFragmentManager)
        personalAdapter.setPagerList(pageList)
        viewPager.adapter = personalAdapter
        tab_layout.setupWithViewPager(viewPager)
    }

    /**F
     * 设置透明状态栏
     */
    private fun setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    var isFullScreen = false

    /**
     * 退出全屏
     */
    fun exitFullScreen() {
        showStatusBar()
        if (toolbar != null) toolbar!!.visibility = View.VISIBLE
        isFullScreen = false
    }

    /**
     * 进入全屏
     */
    fun intoFullScreen() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (toolbar != null) toolbar!!.visibility = View.GONE
        isFullScreen = true
    }

    private fun showStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}


