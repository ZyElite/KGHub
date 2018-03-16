package com.zyelite.kghub.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zyelite.kghub.fragment.base.BaseFragment

/**
 * @author zy
 * @date 2018/3/16
 * @des PersonalAdapter
 */
class PersonalAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private var mPagerList = ArrayList<BaseFragment>()

    fun setPagerList(pagerList: List<BaseFragment>) {
        mPagerList = pagerList as ArrayList<BaseFragment>
    }

    override fun getItem(position: Int): Fragment {
        return mPagerList[position]
    }

    override fun getCount(): Int {
        return mPagerList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mPagerList[position].title
    }
}