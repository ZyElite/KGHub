package com.zyelite.kghub.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zyelite.kghub.R
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
    }
}
