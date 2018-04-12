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
import com.zyelite.kghub.model.EventResModel
import com.zyelite.kghub.model.constant.Event
import com.zyelite.kghub.model.constant.IssuesEvent
import com.zyelite.kghub.model.constant.MemberEvent
import com.zyelite.kghub.model.constant.OrgBlockEvent
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
        val resModel = mData[position]
        holder.itemView.user_name.text = resModel.actor.login
        ImageUtil.circle(mContext, resModel.actor.avatarUrl, holder.itemView.user_avatar)
        holder.itemView.time.text = DateUtil.str2Time(mContext, resModel.createdAt)
        var builder: SpannableStringBuilder? = null
        val type = resModel.type
        var actionStr = "测试活动"
        var descriptionStr = ""
        val name = resModel.repo.name
        val repoName = name.substring(name.lastIndexOf("/") + 1, name.length)
        when (type) {
            Event.COMMIT_COMMENT_EVENT -> {
                actionStr = String.format(StringUtil.getString(R.string.created_comment_on_commit), resModel.repo.name)
                descriptionStr = resModel.payload.comment.body
            }
            Event.DELETE_EVENT -> {
                actionStr = if (Constant.BRANCH == resModel.payload.refType) {
                    String.format(StringUtil.getString(R.string.delete_branch_at),
                            resModel.payload.ref, resModel.repo.name)
                } else {
                    String.format(StringUtil.getString(R.string.delete_tag_at),
                            resModel.payload.ref, resModel.repo.name)
                }
            }
        //Events of this type are not visible in timelines. These events are only used to trigger hooks.
            Event.DEPLOYMENT_EVENT,
            Event.DEPLOYMENT_STATUS_EVENT,
                //Triggered when a new download is created.
            Event.DOWNLOAD_EVENT,
                //Triggered when a user follows another user.
            Event.FOLLOW_EVENT,
                //Triggered when a patch is applied in the Fork Queue.
            Event.FORKAPPLY_EVENT,
                //Triggered when a Gist is created or updated.
            Event.GIST_EVENT,
                //Triggered when a repository's label is created, edited, or deleted.
            Event.LABEL_EVENT,

                //Triggered when a user is added or removed from a team.
            Event.MEMBERSHIP_EVENT,
                //Triggered when a milestone is created, closed, opened, edited, or deleted.
            Event.MILESTONE_EVENT,
                //Triggered when a user is added, removed, or invited to an Organization.
            Event.ORGANIZATION_EVENT,


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
            Event.TEAM_ADD_EVENT -> {
                holder.itemView.action.text = actionStr
            }


//                case OrgBlockEvent:

//                break;
//            case ProjectCardEvent:
//                    actionStr = action + " a project ";
//                break;
//            case ProjectColumnEvent:
//                    actionStr = action + " a project ";
//                break;
//
//            case ProjectEvent:
//                    actionStr = action + " a project ";
//                break;
//            case PublicEvent:
//                    actionStr = String.format(getString(R.string.made_repo_public), fullName);
//            break;
//                case PullRequestEvent:
//            actionStr = action + " pull request " + model.getRepo().getFullName();
//                break;
//            case PullRequestReviewEvent:
//                    String pullRequestReviewStr = getPullRequestReviewEventStr(action);
//                actionStr = String.format(pullRequestReviewStr, fullName);
//            break;
//                case PullRequestReviewCommentEvent:
//            String pullRequestCommentStr = getPullRequestReviewCommentEventStr(action);
//                actionStr = String.format(pullRequestCommentStr, fullName);
//            descSpan = new SpannableStringBuilder(model.getPayload().getComment().getBody());
//                break;
//

            Event.ORG_BLOCK_EVENT -> {
                val orgBlockEventStr: String = if (OrgBlockEvent.BLOCKED == resModel.payload.action) {
                    StringUtil.getString(R.string.org_blocked_user)
                } else {
                    StringUtil.getString(R.string.org_unblocked_user)
                }
                actionStr = String.format(orgBlockEventStr,
                        resModel.payload.organization.login,
                        resModel.payload.blockedUser.login)
            }

            Event.MEMBER_EVENT -> {
                actionStr = String.format(getMemberEventStr(resModel.payload.action),
                        resModel.payload.member.login, resModel.repo.name)
            }


            Event.MARKETPLACE_PURCHASE_EVENT -> {
                actionStr = resModel.payload.action + " marketplace plan "
            }

            Event.ISSUES_EVENT -> {
                val issueEventStr = getIssueEventStr(resModel.payload.action)
                actionStr = String.format(issueEventStr,
                        resModel.payload.issue.number, resModel.repo.name)
                descriptionStr = resModel.payload.issue.title
            }

            Event.ISSUE_COMMENT_EVENT -> {
                actionStr = String.format(StringUtil.getString(R.string.created_comment_on_issue),
                        resModel.payload.issue.number, resModel.repo.name)
                descriptionStr = resModel.payload.comment.body
            }


            Event.INSTALLATION_REPOSITORIES_EVENT -> {
                actionStr = resModel.payload.action + " repository from an installation "
            }

            Event.INSTALLATION_EVENT -> {
                actionStr = resModel.payload.action + " an GitHub App "
            }

            Event.GOLLUM_EVENT -> {
                actionStr = resModel.payload.pages[0].action + " a wiki page "
            }

            Event.WATCH_EVENT -> {
                actionStr = String.format(StringUtil.getString(R.string.starred_repo), resModel.repo.name)
            }

            Event.FORK_EVENT -> {
                actionStr = String.format(StringUtil.getString(R.string.forked_to), resModel.repo.name, resModel.payload.forkee.name)
                descriptionStr = resModel.payload.forkee.description
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
                val ref = resModel.payload.ref
                val branch = ref.substring(ref.lastIndexOf("/") + 1)
                actionStr = String.format(mContext.getString(R.string.push_to), repoName, branch)
                val count = resModel.payload.size
                builder = SpannableStringBuilder()
                for (i in 0 until if (count > 4) 4 else count) {
                    val commit = resModel.payload.commits[i]
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
        holder.itemView.action.text = actionStr
        if (TextUtils.isEmpty(descriptionStr) && null == builder) holder.itemView.desc.visibility = View.GONE else {
            holder.itemView.desc.visibility = View.VISIBLE
            holder.itemView.desc.text = if (null == builder) descriptionStr else builder
        }
    }

    fun replace(model: ArrayList<EventResModel>) {
        mData.clear()
        mData.addAll(model)
        notifyDataSetChanged()
    }


    fun add(model: ArrayList<EventResModel>) {
        mData.addAll(model)
    }

//
//    private fun getPullRequestReviewEventStr(action: String?): String {
//        val actionType = EventPayload.PullRequestReviewEventActionType.valueOf(action)
//        when (actionType) {
//            submitted -> return getString(R.string.submitted_pull_request_review_at)
//            edited -> return getString(R.string.edited_pull_request_review_at)
//            dismissed -> return getString(R.string.dismissed_pull_request_review_at)
//            else -> return getString(R.string.submitted_pull_request_review_at)
//        }
//    }
//
//    private fun getPullRequestReviewCommentEventStr(action: String?): String {
//        val actionType = EventPayload.PullRequestReviewCommentEventActionType.valueOf(action)
//        when (actionType) {
//            created -> return getString(R.string.created_pull_request_comment_at)
//            edited -> return getString(R.string.edited_pull_request_comment_at)
//            deleted -> return getString(R.string.deleted_pull_request_comment_at)
//            else -> return getString(R.string.created_pull_request_comment_at)
//        }
//    }

    private fun getMemberEventStr(action: String?): String {
        return when (action) {
            MemberEvent.ADDED -> {
                StringUtil.getString(R.string.added_member_to)
            }
            MemberEvent.DELETED -> {
                StringUtil.getString(R.string.deleted_member_at)
            }
            MemberEvent.EDITED -> {
                StringUtil.getString(R.string.edited_member_at)
            }
            else -> StringUtil.getString(R.string.added_member_to)
        }
    }


    private fun getIssueEventStr(action: String?): String {
        return when (action) {
            IssuesEvent.EDITED -> {
                StringUtil.getString(R.string.edited_issue_at)
            }
            IssuesEvent.ASSIGNED -> {
                StringUtil.getString(R.string.assigned_issue_at)
            }
            IssuesEvent.UNASSIGNED -> {
                StringUtil.getString(R.string.unassigned_issue_at)
            }
            IssuesEvent.LABELED -> {
                StringUtil.getString(R.string.labeled_issue_at)
            }
            IssuesEvent.UNLABELED -> {
                StringUtil.getString(R.string.unlabeled_issue_at)
            }
            IssuesEvent.OPENED -> {
                StringUtil.getString(R.string.opened_issue_at)
            }
            IssuesEvent.REOPENED -> {
                StringUtil.getString(R.string.reopened_issue_at)
            }
            IssuesEvent.MILESTONED -> {
                StringUtil.getString(R.string.milestoned_issue_at)
            }
            IssuesEvent.DEMILESTONED -> {
                StringUtil.getString(R.string.demilestoned_issue_at)
            }
            IssuesEvent.CLOSED -> {
                StringUtil.getString(R.string.closed_issue_at)
            }
            else -> StringUtil.getString(R.string.opened_issue_at)
        }
    }


    class CommitsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}