package com.zyelite.kghub.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
    lateinit var userService: UserService;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_menu)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        DaggerUiComponent.builder()
                .apiComponent(App.getNetComponent())
                .build()
                .inject(this)

        userService.getUserInfo(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    //执行成功
                    val body: User = it.body() as User;
                    Log.e("KGHub", body.toString())
                    ImageUtil.circle(this, body.getAvatarUrl(), avatar);
                    userName.text = body.getLogin()
                    val format = SimpleDateFormat("yyyy-MM-dd")
                    createTime.text = "加入时间 " + format.format(body.getCreatedAt())
                }, {
                    //执行失败
                    Log.e("KGHub", "执行失败")
                })

    }


}
