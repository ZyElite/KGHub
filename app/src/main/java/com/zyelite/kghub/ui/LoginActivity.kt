package com.zyelite.kghub.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zyelite.kghub.App
import com.zyelite.kghub.R
import com.zyelite.kghub.dagger.component.DaggerUiComponent
import com.zyelite.kghub.http.api.LoginService
import com.zyelite.kghub.model.AuthReqModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Credentials
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginService: LoginService;

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login);
        DaggerUiComponent.builder()
                .apiComponent(App.getNetComponent())
                .build()
                .inject(this);

        login.setOnClickListener({ login() });
    }

    @SuppressLint("ShowToast")
    private fun login() {
        val username = user_name_et.text;
        val password = password_et.text;
        val token = Credentials.basic(username.toString(), password.toString());
        getSharedPreferences("KGHub", Context.MODE_PRIVATE).edit().putString("token", token).apply()
        val authReqModel = AuthReqModel().generate();

        loginService.authorizations(authReqModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ res ->
                    if (res.isSuccessful) {
                        Log.e("KGHub", res.body().toString())
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }, {
                    Log.e("KGHub", "请求失败")
                })

    }


}