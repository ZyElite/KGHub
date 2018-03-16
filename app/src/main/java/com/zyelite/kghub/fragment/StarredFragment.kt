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
class StarredFragment : BaseFragment() {

    override var title = "星标"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_starred, container, false)
    }

}// Required empty public constructor
