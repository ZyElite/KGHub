package com.zyelite.kghub.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zyelite.kghub.R
import com.zyelite.kghub.model.User
import com.zyelite.kghub.utils.ImageUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_personal.*

/**
 *个人主页
 */
class PersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({
            finish()
        })
        val realm = Realm.getDefaultInstance()
        val request = realm.where(User::class.java)
        val user = request.findFirst()
        Log.e("asd",user.toString())
        if (user != null) {
            ImageUtil.circle(this, user.getAvatarUrl(), avatar)
            supportActionBar?.title = user.getName()
        }
    }
}
