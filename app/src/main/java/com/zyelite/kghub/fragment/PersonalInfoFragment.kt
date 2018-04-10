package com.zyelite.kghub.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.R
import com.zyelite.kghub.fragment.base.BaseFragment
import com.zyelite.kghub.model.User
import kotlinx.android.synthetic.main.fragment_personal_info.*


/**
 *
 */
class PersonalInfoFragment : BaseFragment() {

    override var title = "个人信息"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = realm.where(User::class.java).findFirst() as User
        repoName.text = user.login
        followers.text = user.followers.toString()
        following.text = user.following.toString()
        publicRepo.text = user.publicRepos.toString()
        publicGists.text = user.publicGists.toString()
    }

}// Required empty public constructor
