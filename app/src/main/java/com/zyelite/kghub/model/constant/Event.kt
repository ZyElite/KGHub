package com.zyelite.kghub.model.constant

/**
 * @author ZyElite
 * @create 2018/4/8
 * @description Event
 */

object Event {

    const val COMMIT_COMMENT_EVENT = "CommitCommentEvent"
    const val CREATE_EVENT = "CreateEvent"
    const val DELETE_EVENT = "DeleteEvent"
    //Events of this type are not visible in timelines. These events are only used to trigger hooks.
    const val DEPLOYMENT_EVENT = "DeploymentEvent"
    const val DEPLOYMENT_STATUS_EVENT = "DeploymentStatusEvent"
    //Triggered when a new download is created.
    const val DOWNLOAD_EVENT = "DownloadEvent"
    //Triggered when a user follows another user.
    const val FOLLOW_EVENT = "FollowEvent"

    const val FORK_EVENT = "ForkEvent"
    //Triggered when a patch is applied in the Fork Queue.
    const val FORKAPPLY_EVENT = "ForkApplyEvent"
    //Triggered when a Gist is created or updated.
    const val GIST_EVENT = "GistEvent"
    //Triggered when a Wiki page is created or updated.
    const val GOLLUM_EVENT = "GollumEvent"
    //Triggered when a GitHub App has been installed or uninstalled.
    const val INSTALLATION_EVENT = "InstallationEvent"
    //Triggered when a repository is added or removed from an installation.
    const val INSTALLATION_REPOSITORIES_EVENT = "InstallationRepositoriesEvent"
    //Triggered when an issue comment is created, edited, or deleted.
    const val ISSUE_COMMENT_EVENT = "IssueCommentEvent"
    const val ISSUES_EVENT = "IssuesEvent"
    const val LABEL_EVENT = "LabelEvent"
    const val MARKETPLACE_PURCHASE_EVENT = "MarketplacePurchaseEvent"
    const val MEMBER_EVENT = "MemberEvent"
    //Triggered when a user is added or removed from a team.
    const val MEMBERSHIP_EVENT = "MembershipEvent"
    //Triggered when a milestone is created, closed, opened, edited, or deleted.
    const val MILESTONE_EVENT = "MilestoneEvent"
    //Triggered when a user is added, removed, or invited to an Organization.
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
}
