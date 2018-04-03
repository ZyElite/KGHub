package com.zyelite.kghub.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.R
import com.zyelite.kghub.model.CommitsResModel

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description CommitsAdapter
 */

class CommitsAdapter : RecyclerView.Adapter<CommitsAdapter.CommitsHolder>() {
    private var data = ArrayList<CommitsResModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitsHolder {
        return CommitsHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comments_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CommitsHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun replace(model: List<CommitsResModel>) {
        data.clear()
        data.addAll(model)
    }

    fun add(model: List<CommitsResModel>) {
        data.addAll(model)
    }

    class CommitsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }

}