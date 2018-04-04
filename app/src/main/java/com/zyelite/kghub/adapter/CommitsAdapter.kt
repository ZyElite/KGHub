package com.zyelite.kghub.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.R
import com.zyelite.kghub.model.EventResModel
import com.zyelite.kghub.utils.DateUtil
import com.zyelite.kghub.utils.EllipsizeLineSpan
import com.zyelite.kghub.utils.ImageUtil
import kotlinx.android.synthetic.main.item_commits_layout.view.*
import java.util.*
import java.util.regex.Pattern

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description CommitsAdapter
 */

class CommitsAdapter : RecyclerView.Adapter<CommitsAdapter.CommitsHolder>() {
    private var mData = ArrayList<EventResModel>()
    private lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitsHolder {
        mContext = parent.context
        return CommitsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_commits_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: CommitsHolder, position: Int) {
        val resModel = mData[position]
        holder.itemView.user_name.text = resModel.actor.login
        ImageUtil.circle(mContext, resModel.actor.avatarUrl, holder.itemView.user_avatar)
        holder.itemView.time.text = DateUtil.str2Time(mContext, resModel.createdAt)
        if (resModel.type == EventResModel.EventType.PushEvent) {
            var ref = resModel.payload.ref
            val branch = ref.substring(ref.lastIndexOf("/") + 1)
            val format = String.format(mContext.getString(R.string.push_to), branch, resModel.repo.name)

            var builder = SpannableStringBuilder()
            val count = resModel.payload.size
            for (i in 0 until if (count > 4) 4 else count) {
                var commit = resModel.payload.commits[i]
                if (i != 0) {
                    builder.append("\n")
                }
                val lastLength = builder.length
                val sha = commit.sha.substring(0, 7)
                builder.append(sha)
                builder.setSpan(TextAppearanceSpan(mContext, R.style.text_link),
                        lastLength, lastLength + sha.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append(" ")
                builder.append(commit.message)
                builder.setSpan(EllipsizeLineSpan(if (i == count - 1) 0 else 0),
                        lastLength, builder.length, 0)
            }
            val span = SpannableStringBuilder(format)
            val matcher = Pattern.compile("([a-z]|[A-Z]|\\d|-)*/([a-z]|[A-Z]|\\d|-|\\.|_)*").matcher(format)
            while (matcher.find()) {
                span.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            holder.itemView.action.text = format
            holder.itemView.desc.text = builder
        } else {
            holder.itemView.action.text = "测试活动"
            holder.itemView.desc.text = "测试描述"
        }

    }

    private fun getFirstLine(str: String?): String? {
        return if (str == null || !str.contains("\n")) str else str.substring(0, str.indexOf("\n"))
    }


    fun replace(model: ArrayList<EventResModel>) {
        mData.clear()
        mData.addAll(model)
        notifyDataSetChanged()
    }

    fun add(model: ArrayList<EventResModel>) {
        mData.addAll(model)
    }

    class CommitsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}