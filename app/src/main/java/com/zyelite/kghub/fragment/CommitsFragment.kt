package com.zyelite.kghub.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.App
import com.zyelite.kghub.R
import com.zyelite.kghub.adapter.CommitsAdapter
import com.zyelite.kghub.dagger.component.DaggerUiComponent
import com.zyelite.kghub.fragment.base.BaseFragment
import com.zyelite.kghub.http.api.EventService
import com.zyelite.kghub.utils.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item_list.*
import javax.inject.Inject

class CommitsFragment : BaseFragment() {

    @Inject
    lateinit var eventService: EventService
    override var title = "活动"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerUiComponent.builder()
                .apiComponent(App.getNetComponent())
                .build()
                .inject(this)
        initData()
    }


    private fun initData() {
        val commitsAdapter = CommitsAdapter()
        recycleView.adapter = commitsAdapter
        val name = context!!.getSharedPreferences("KGHub", Context.MODE_PRIVATE).getString(Constant.CURRENT_LOGIN, "")
        eventService.getUserEvents(true, name, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.isSuccessful) {
                        commitsAdapter.replace(it.body()!!)
                    }
                }, {
                    Log.e("KGHub", "失败")
                })
    }
}
