package com.zyelite.kghub.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zyelite.kghub.R
import com.zyelite.kghub.fragment.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class PersonalInfoFragment : BaseFragment() {

    override var title = "个人信息"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

}// Required empty public constructor
