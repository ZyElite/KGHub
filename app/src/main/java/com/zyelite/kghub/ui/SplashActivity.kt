package com.zyelite.kghub.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.zyelite.kghub.R
import com.zyelite.kghub.utils.Constant
import com.zyelite.kghub.utils.StringUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val login = getSharedPreferences(StringUtil.getString(R.string.app_name), Context.MODE_PRIVATE).getString(Constant.CURRENT_LOGIN, "")
        if (!TextUtils.isEmpty(login)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
