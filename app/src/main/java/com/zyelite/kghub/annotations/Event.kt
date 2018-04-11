package com.zyelite.kghub.annotations

import android.support.annotation.StringDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author ZyElite
 * @create 2018/4/8
 * @description Event
 */

object Event {
    const val COMMIT_COMMENT_EVENT = "CommitCommentEvent"
    const val CREATE_EVENT = "CreateEvent"
    const val DELETE_EVENT = "DeleteEvent"
    const val DEPLOYMENT_EVENT = "DeploymentEvent"
    const val DEPLOYMENT_STATUS_EVENT = "DeploymentStatusEvent"
    const val DOWNLOAD_EVENT = "DownloadEvent"
    const val FOLLOW_EVENT = "FollowEvent"
    const val FORK_EVENT = "ForkEvent"
    const val FORKAPPLY_EVENT = "ForkApplyEvent"
    const val GIST_EVENT = "GistEvent"
    const val GOLLUM_EVENT = "GollumEvent"
    const val INSTALLATION_EVENT = "InstallationEvent"
    const val INSTALLATION_REPOSITORIES_EVENT = "InstallationRepositoriesEvent"
    const val ISSUE_COMMENT_EVENT = "IssueCommentEvent"
    const val ISSUES_EVENT = "IssuesEvent"
    const val LABEL_EVENT = "LabelEvent"
    const val MARKETPLACE_PURCHASE_EVENT = "MarketplacePurchaseEvent"
    const val MEMBER_EVENT = "MemberEvent"
    const val MEMBERSHIP_EVENT = "MembershipEvent"
    const val MILESTONE_EVENT = "MilestoneEvent"
    const val ORGANIZATION_EVENT = "OrganizationEvent"
    const val ORG_BLOCK_EVENT = "OrgBlockEvent"
    const val PAGE_BUILD_EVENT = "PageBuildEvent"
    const val PROJECTCARD_EVENT = "ProjectCardEvent"
    const val PROJECT_COLUMN_EVENT = "ProjectColumnEvent"
    const val PROJECT_EVENT = "ProjectEvent"
    const val PUBLIC_EVENT = "PublicEvent"
    const val PULL_REQUEST_EVENT = "PullRequestEvent"
    const val PULL_REQUEST_REVIEW_EVENT = "PullRequestReviewEvent"
    const val PULL_REQUEST_REVIEW_COMMENT_EVENT = "PullRequestReviewCommentEvent"
    const val PUSH_EVENT = "PushEvent"
    const val RELEASE_EVENT = "ReleaseEvent"
    const val REPOSITORY_EVENT = "RepositoryEvent"
    const val STATUS_EVENT = "StatusEvent"
    const val TEAM_EVENT = "TeamEvent"
    const val TEAM_ADD_EVENT = "TeamAddEvent"
    const val WATCH_EVENT = "WatchEvent"


    @StringDef(
            COMMIT_COMMENT_EVENT,
            CREATE_EVENT,
            DELETE_EVENT,
            DEPLOYMENT_EVENT,
            DEPLOYMENT_STATUS_EVENT,
            DOWNLOAD_EVENT,
            FOLLOW_EVENT,
            FORK_EVENT,
            FORKAPPLY_EVENT,
            GIST_EVENT,
            GOLLUM_EVENT,
            INSTALLATION_EVENT,
            INSTALLATION_REPOSITORIES_EVENT,
            ISSUE_COMMENT_EVENT,
            ISSUES_EVENT,
            LABEL_EVENT,
            MARKETPLACE_PURCHASE_EVENT,
            MEMBER_EVENT,
            MEMBERSHIP_EVENT,
            MILESTONE_EVENT,
            ORGANIZATION_EVENT,
            ORG_BLOCK_EVENT,
            PAGE_BUILD_EVENT,
            PROJECTCARD_EVENT,
            PROJECT_COLUMN_EVENT,
            PROJECT_EVENT,
            PUBLIC_EVENT,
            PULL_REQUEST_EVENT,
            PULL_REQUEST_REVIEW_EVENT,
            PULL_REQUEST_REVIEW_COMMENT_EVENT,
            PUSH_EVENT,
            RELEASE_EVENT,
            REPOSITORY_EVENT,
            STATUS_EVENT,
            TEAM_EVENT,
            TEAM_ADD_EVENT,
            WATCH_EVENT)
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在. class 文件.
    annotation class Type//接口，定义新的注解类型
}
