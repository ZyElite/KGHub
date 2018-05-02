package com.zyelite.kghub.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
    private var page = 1
    private val pageSize = 30
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

        //refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { initData()})

//        refreshLayout.setOnRefreshListener { initData() }
        initData()
        initListener()

    }

    private var isLoadMore = false
    private var lastVisibleItemPosition: Int = 0
    private fun initListener() {
        val layoutManager = recyclerView.layoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                recyclerView.childCount
                layoutManager.childCount
                layoutManager.itemCount
                Log.e("asd", " recyclerView.childCount= " + recyclerView.childCount
                        + " layoutManager.childCount = " + layoutManager.childCount
                        + " layoutManager.itemCount=" + layoutManager.itemCount)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView.childCount
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItemPosition == layoutManager.itemCount) {
                    //last one
                    isLoadMore = true
                }
            }
        })
    }


    private fun initData() {
        val commitsAdapter = CommitsAdapter()
        recyclerView.adapter = commitsAdapter
        loadData(commitsAdapter)
    }

    private fun loadData(commitsAdapter: CommitsAdapter) {
        eventService.getUserEvents(true, Constant.NAME, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.isSuccessful) {
                        val headers = it.headers()
                        Log.e("heads", headers.toString())
                        val link = headers.get("Link")
                        Log.e("link", link.orEmpty())
                        commitsAdapter.replace(it.body()!!)
                    }
                }, {
                    Log.e("KGHub", "失败")
                })

    }
}
