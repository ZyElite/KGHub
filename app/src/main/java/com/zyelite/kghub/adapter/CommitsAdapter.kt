package com.zyelite.kghub.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.R
import com.zyelite.kghub.annotations.Event
import com.zyelite.kghub.model.EventResModel
import com.zyelite.kghub.utils.*
import kotlinx.android.synthetic.main.item_commits_layout.view.*
import java.util.*

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
        var resModel = mData[position]
        holder.itemView.user_name.text = resModel.actor.login
        ImageUtil.circle(mContext, resModel.actor.avatarUrl, holder.itemView.user_avatar)
        holder.itemView.time.text = DateUtil.str2Time(mContext, resModel.createdAt)
        var builder: SpannableStringBuilder? = null
        var type = resModel.type
        var actionStr = "测试活动"
        var descriptionStr = ""
        val name = resModel.repo.name
        val repoName = name.substring(name.lastIndexOf("/") + 1, name.length)
        when (type) {
            Event.COMMIT_COMMENT_EVENT,
            Event.DELETE_EVENT,
            Event.DEPLOYMENT_EVENT,
            Event.DEPLOYMENT_STATUS_EVENT,
            Event.DOWNLOAD_EVENT,
            Event.FOLLOW_EVENT,
            Event.FORK_EVENT,
            Event.FORKAPPLY_EVENT,
            Event.GIST_EVENT,
            Event.GOLLUM_EVENT,
            Event.INSTALLATION_EVENT,
            Event.INSTALLATION_REPOSITORIES_EVENT,
            Event.ISSUE_COMMENT_EVENT,
            Event.ISSUES_EVENT,
            Event.LABEL_EVENT,
            Event.MARKETPLACE_PURCHASE_EVENT,
            Event.MEMBER_EVENT,
            Event.MEMBERSHIP_EVENT,
            Event.MILESTONE_EVENT,
            Event.ORGANIZATION_EVENT,
            Event.ORG_BLOCK_EVENT,
            Event.PAGE_BUILD_EVENT,
            Event.PROJECTCARD_EVENT,
            Event.PROJECT_COLUMN_EVENT,
            Event.PROJECT_EVENT,
            Event.PUBLIC_EVENT,
            Event.PULL_REQUEST_EVENT,
            Event.PULL_REQUEST_REVIEW_EVENT,
            Event.PULL_REQUEST_REVIEW_COMMENT_EVENT,
            Event.RELEASE_EVENT,
            Event.REPOSITORY_EVENT,
            Event.STATUS_EVENT,
            Event.TEAM_EVENT,
            Event.TEAM_ADD_EVENT,
            Event.WATCH_EVENT -> {
                holder.itemView.action.text = actionStr
            }
            Event.CREATE_EVENT -> {
                actionStr = when {
                    Constant.REPOSITORY == resModel.payload.refType -> {
                        String.format(StringUtil.getString(R.string.created_repo), repoName)
                    }
                    Constant.BRANCH == resModel.payload.refType -> {
                        String.format(StringUtil.getString(R.string.created_branch_at),
                                resModel.payload.ref, repoName)
                    }
                    else -> {
                        String.format(StringUtil.getString(R.string.created_tag_at),
                                resModel.payload.ref, repoName)
                    }
                }
                holder.itemView.action.text = actionStr
                holder.itemView.desc.text = ""
            }
            Event.PUSH_EVENT -> {
                var ref = resModel.payload.ref
                val branch = ref.substring(ref.lastIndexOf("/") + 1)
                actionStr = String.format(mContext.getString(R.string.push_to), repoName, branch)
                val count = resModel.payload.size
                builder = SpannableStringBuilder()
                for (i in 0 until if (count > 4) 4 else count) {
                    var commit = resModel.payload.commits[i]
                    if (i != 0) {
                        builder.append("\n")
                    }
                    val lastLength = builder.length
                    val sha = commit.sha.substring(0, 7)
                    builder.append(sha)
                    builder.setSpan(TextAppearanceSpan(mContext, R.style.text_link),
                            lastLength, lastLength + (sha.length), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    builder.append(" ")
                    builder.append(commit.message)
                    builder.setSpan(EllipsizeLineSpan(if (i == count - 1) 0 else 0),
                            lastLength, builder.length, 0)
                }
            }

        }
//        val matcher = Pattern.compile("([a-z]|[A-Z]|\\d|-)*/([a-z]|[A-Z]|\\d|-|\\.|_)*").matcher(actionStr)
//        val span = SpannableStringBuilder(actionStr)
//        while (matcher.find()) {
//            span.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
        holder.itemView.action.text = actionStr
        if (TextUtils.isEmpty(descriptionStr) && null == builder) holder.itemView.desc.visibility = View.GONE else {
            holder.itemView.desc.visibility = View.VISIBLE
            holder.itemView.desc.text = if (null == builder) descriptionStr else builder
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