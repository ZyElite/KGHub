package com.zyelite.kghub.fragment

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

    }

    fun initData() {
        val commitsAdapter = CommitsAdapter()
        recycleView.adapter = commitsAdapter
        eventService.getUserEvents(true, "ZyElite", 60)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("asd","成功")
                    if (it.isSuccessful) {

                    }
                }, {
                    Log.e("asd","失败")
                })
    }
}
