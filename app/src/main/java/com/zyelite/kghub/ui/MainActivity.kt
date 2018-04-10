package com.zyelite.kghub.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.ActionBarDrawerToggle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.zyelite.kghub.App
import com.zyelite.kghub.R
import com.zyelite.kghub.base.BaseActivity
import com.zyelite.kghub.dagger.component.DaggerUiComponent
import com.zyelite.kghub.http.api.UserService
import com.zyelite.kghub.model.User
import com.zyelite.kghub.utils.Constant
import com.zyelite.kghub.utils.DateUtil
import com.zyelite.kghub.utils.ImageUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    //侧边栏 是否打开另一组 默认不打开
    private var isOpen = false

    @Inject
    lateinit var userService: UserService

    override fun initView(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        initToolbar()
        inject()
        initMenu()
        //TODO 当个人信息为空时 请求个人信息
        val name = getSharedPreferences("KGHub", Context.MODE_PRIVATE).getString(Constant.CURRENT_LOGIN, "")
        val user = realm.where(User::class.java).equalTo("login", name).findFirst()
        if (user == null) {
            getUserInfo()
        }
    }


    /**
     * 初始化左边菜单栏
     */
    private fun initMenu() {
        nav_view_start.inflateMenu(R.menu.activity_main_drawer)
        nav_view_start.setNavigationItemSelectedListener({
            Toast.makeText(this, it.title, Toast.LENGTH_SHORT).show()
            it.isCheckable = true
            when (it.itemId) {
                R.id.nav_profile -> startActivity(Intent(this, PersonalActivity::class.java))
            }
            true
        })
    }

    private fun initMenuGroup() {
        val menu = nav_view_start.menu
        menu.setGroupVisible(R.id.my_account, isOpen)
        menu.setGroupVisible(R.id.manage_accounts, isOpen)
        menu.setGroupVisible(R.id.my, !isOpen)
        menu.setGroupVisible(R.id.repositories, !isOpen)
        menu.setGroupVisible(R.id.search, !isOpen)
        menu.setGroupVisible(R.id.setting, !isOpen)
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {

        userService.getUserInfo(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    //执行成功
                    val body: User = it.body() as User
                    Log.e("KGHub", body.toString())
                    ImageUtil.circle(this, body.avatarUrl, avatar)
                    userName.text = body.login
                    mail.text = if (TextUtils.isEmpty(body.email)) DateUtil.getDateStr(body.createdAt!!) else body.email
                    //开启异步事物
                    realm.executeTransactionAsync({ bgRealm ->
                        bgRealm.insert(body)
                        // bgRealm.insertOrUpdate(body)
//                        bgRealm.deleteAll()
//                        bgRealm.insert(body)
                        // if (user == null) {
                        //   user = bgRealm.createObjectFromJson(User::class.java,body.toString())
//                            user?.setLogin(body.getLogin())
//                            // user.setEmail(body.getEmail())
//                            user?.setAvatarUrl(body.getAvatarUrl())
//                            user?.setCreatedAt(body.getCreatedAt()!!)
                        //  }
                    }, {
                        Log.e("realm", "执行成功")
                        // Transaction was a success.
                    }) {
                        Log.e("realm", it.message)
                        // Transaction failed and was automatically canceled.
                    }

                }, {
                    //执行失败
                    Log.e("KGHub", "执行失败")
                })
    }

    /**
     * dagger2 注入
     */
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


    /**
     * 左边菜单创建时
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toggle_account_bn.setOnClickListener {
            isOpen = !isOpen
            toggle_account_bn.setImageResource(if (isOpen) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down)
            initMenuGroup()
        }
        return true
    }

    /**
     * 左边菜单展示时
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        initMenuGroup()
        return true
    }

}
