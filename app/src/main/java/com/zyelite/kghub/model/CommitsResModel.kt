package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName


/**
 * @author ZyElite
 * @create 2018/4/2
 * @description CommitsResModel
 */

data class CommitsResModel(
        @SerializedName("url") var url: String = "",
        @SerializedName("sha") var sha: String = "",
        @SerializedName("html_url") var htmlUrl: String = "",
        @SerializedName("comments_url") var commentsUrl: String = "",
        @SerializedName("commit") var commit: Commit = Commit(),
        @SerializedName("author") var author: Author = Author(),
        @SerializedName("committer") var committer: Committer = Committer(),
        @SerializedName("parents") var parents: List<Parent> = listOf()
)

data class CommitterDetails(
        @SerializedName("login") var login: String = "",
        @SerializedName("id") var id: Int = 0,
        @SerializedName("avatar_url") var avatarUrl: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("html_url") var htmlUrl: String = "",
        @SerializedName("followers_url") var followersUrl: String = "",
        @SerializedName("following_url") var followingUrl: String = "",
        @SerializedName("gists_url") var gistsUrl: String = "",
        @SerializedName("starred_url") var starredUrl: String = "",
        @SerializedName("subscriptions_url") var subscriptionsUrl: String = "",
        @SerializedName("organizations_url") var organizationsUrl: String = "",
        @SerializedName("repos_url") var reposUrl: String = "",
        @SerializedName("events_url") var eventsUrl: String = "",
        @SerializedName("received_events_url") var receivedEventsUrl: String = "",
        @SerializedName("type") var type: String = "",
        @SerializedName("site_admin") var siteAdmin: Boolean = false
)

data class AuthorDetails(
        @SerializedName("login") var login: String = "",
        @SerializedName("id") var id: Int = 0,
        @SerializedName("avatar_url") var avatarUrl: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("html_url") var htmlUrl: String = "",
        @SerializedName("followers_url") var followersUrl: String = "",
        @SerializedName("following_url") var followingUrl: String = "",
        @SerializedName("gists_url") var gistsUrl: String = "",
        @SerializedName("starred_url") var starredUrl: String = "",
        @SerializedName("subscriptions_url") var subscriptionsUrl: String = "",
        @SerializedName("organizations_url") var organizationsUrl: String = "",
        @SerializedName("repos_url") var reposUrl: String = "",
        @SerializedName("events_url") var eventsUrl: String = "",
        @SerializedName("received_events_url") var receivedEventsUrl: String = "",
        @SerializedName("type") var type: String = "",
        @SerializedName("site_admin") var siteAdmin: Boolean = false
)

data class Parent(
        @SerializedName("url") var url: String = "",
        @SerializedName("sha") var sha: String = ""
)

data class Commit(
        @SerializedName("url") var url: String = "",
        @SerializedName("author") var author: Author = Author(),
        @SerializedName("committer") var committer: Committer = Committer(),
        @SerializedName("message") var message: String = "",
        @SerializedName("tree") var tree: Tree = Tree(),
        @SerializedName("comment_count") var commentCount: Int = 0,
        @SerializedName("verification") var verification: Verification = Verification()
)

data class Committer(
        @SerializedName("name") var name: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("date") var date: String = ""
)

data class Tree(
        @SerializedName("url") var url: String = "",
        @SerializedName("sha") var sha: String = ""
)

data class Verification(
        @SerializedName("verified") var verified: Boolean = false,
        @SerializedName("reason") var reason: String = ""
)

data class Author(
        @SerializedName("name") var name: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("date") var date: String = ""
)