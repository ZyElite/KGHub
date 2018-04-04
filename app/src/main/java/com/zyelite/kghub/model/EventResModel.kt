package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * @author ZyElite
 * @create 2018/4/3
 * @description EventResModel
 */

data class EventResModel(
        @SerializedName("type") var type: EventType,
        @SerializedName("public") var public: Boolean = false,
        @SerializedName("payload") var payload: Payload = Payload(),
        @SerializedName("repo") var repo: Repo = Repo(),
        @SerializedName("actor") var actor: Actor = Actor(),
        @SerializedName("org") var org: Org = Org(),
        @SerializedName("created_at") var createdAt: Date,
        @SerializedName("id") var id: String = ""

) {
    enum class EventType {
        CommitCommentEvent,
        CreateEvent,
        /**
         * Represents a deleted branch or tag.
         */
        DeleteEvent,
        ForkEvent,
        /**
         * Triggered when a Wiki page is created or updated.
         */
        GollumEvent,

        /**
         * Triggered when a GitHub App has been installed or uninstalled.
         */
        InstallationEvent,
        /**
         * Triggered when a repository is added or removed from an installation.
         */
        InstallationRepositoriesEvent,
        IssueCommentEvent,
        IssuesEvent,


        /**
         * Triggered when a user purchases, cancels, or changes their GitHub Marketplace plan.
         */
        MarketplacePurchaseEvent,
        /**
         * Triggered when a user is added or removed as a collaborator to a repository, or has their permissions changed.
         */
        MemberEvent,
        /**
         * Triggered when an organization blocks or unblocks a user.
         */
        OrgBlockEvent,
        /**
         * Triggered when a project card is created, updated, moved, converted to an issue, or deleted.
         */
        ProjectCardEvent,
        /**
         * Triggered when a project column is created, updated, moved, or deleted.
         */
        ProjectColumnEvent,


        /**
         * Triggered when a project is created, updated, closed, reopened, or deleted.
         */
        ProjectEvent,
        /**
         * made repository public
         */
        PublicEvent,
        PullRequestEvent,
        /**
         * Triggered when a pull request review is submitted into a non-pending state, the body is edited, or the review is dismissed.
         */
        PullRequestReviewEvent,
        PullRequestReviewCommentEvent,


        PushEvent,
        ReleaseEvent,
        WatchEvent,

        //Events of this type are not visible in timelines. These events are only used to trigger hooks.
        DeploymentEvent,
        DeploymentStatusEvent,
        MembershipEvent,
        MilestoneEvent,
        OrganizationEvent,
        PageBuildEvent,
        RepositoryEvent,
        StatusEvent,
        TeamEvent,
        TeamAddEvent,
        LabelEvent,

        //Events of this type are no longer delivered, but it's possible that they exist in timelines
        // of some users. You cannot createForRepo webhooks that listen to these events.
        DownloadEvent,
        FollowEvent,
        ForkApplyEvent,
        GistEvent

    }
}

data class Org(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("login") var login: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("avatar_url") var avatarUrl: String = ""

) {
    override fun toString(): String {
        return "Org(id=$id, login='$login', gravatarId='$gravatarId', url='$url', avatarUrl='$avatarUrl')"
    }
}

data class Payload(
        @SerializedName("push_id") var pushId: Long = 0,
        @SerializedName("size") var size: Int = 0,
        @SerializedName("distinct_size") var distinctSize: Int = 0,
        @SerializedName("ref") var ref: String = "",
        @SerializedName("head") var head: String = "",
        @SerializedName("before") var before: String = "",
        @SerializedName("commits") var commits: List<Commit> = listOf()

) {
    data class Commit(
            @SerializedName("sha") var sha: String = "",
            @SerializedName("author") var author: Author = Author(),
            @SerializedName("message") var message: String = "",
            @SerializedName("distinct") var distinct: Boolean = false,
            @SerializedName("url") var url: String = ""
    ) {
        data class Author(
                @SerializedName("email") var email: String = "",
                @SerializedName("name") var name: String = ""
        )
    }

}

data class Repo(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("url") var url: String = ""

) {
    override fun toString(): String {
        return "Repo(id=$id, name='$name', url='$url')"
    }
}

data class Actor(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("login") var login: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("avatar_url") var avatarUrl: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("display_login") var displayLogin: String = ""


) {
    override fun toString(): String {
        return "Actor(id=$id, login='$login', gravatarId='$gravatarId', avatarUrl='$avatarUrl', url='$url')"
    }
}

