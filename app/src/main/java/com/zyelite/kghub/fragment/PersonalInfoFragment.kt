package com.zyelite.kghub.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.R
import com.zyelite.kghub.fragment.base.BaseFragment
import com.zyelite.kghub.model.UserModel
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
        val user = realm.where(UserModel::class.java).findFirst() as UserModel
        repoName.text = user.getLogin()
        followers.text = user.getFollowers().toString()
        following.text = user.getFollowing().toString()
        publicRepo.text = user.getPublicRepos().toString()
        publicGists.text = user.getPublicGists().toString()
    }

}// Required empty public constructor
