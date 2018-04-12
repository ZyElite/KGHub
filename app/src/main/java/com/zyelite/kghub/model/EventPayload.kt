package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName


/**
 * @author ZyElite
 * @create 2018/4/11
 * @description EventPayload
 */


data class EventPayload(
        @SerializedName("push_id") var pushId: Long = 0,
        @SerializedName("size") var size: Int = 0,
        @SerializedName("distinct_size") var distinctSize: Int = 0,
        @SerializedName("head") var head: String = "",
        @SerializedName("before") var before: String = "",
        @SerializedName("commits") var commits: List<Commit> = listOf(),
        //PushEvent CreateEvent DeleteEvent
        @SerializedName("ref") var ref: String = "",
        //CreateEvent DeleteEvent
        @SerializedName("ref_type") var refType: String = "",
        @SerializedName("master_branch") var masterBranch: String = "",
        @SerializedName("pusher_type") var pusherType: String = "",
        @SerializedName("description") var description: String = "",
        //ForkEvent
        @SerializedName("forkee") var forkee: Forkee = Forkee(),
        //WatchEvent The action that was performed. Currently, can only be started.
        //IssueCommentEvent The action that was performed on the comment. Can be one of "created", "edited", or "deleted".
        @SerializedName("action") var action: String = "",
        //CommitCommentEvent Triggered when a commit comment is created.
        @SerializedName("comment") var comment: Comment = Comment(),
        //GollumEvent
        @SerializedName("pages") var pages: List<Page> = listOf(),

        //IssueCommentEvent
        @SerializedName("issue") var issue: Issue = Issue(),

        //IssueCommentEvent
        @SerializedName("member") var member: User = User(),

        //OrgBlockEvent
        @SerializedName("blocked_user") var blockedUser: User = User(),
        @SerializedName("organization") var organization: User = User()




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

    /**
     * ForkEvent
     */
    data class Forkee(
            @SerializedName("id") var id: Int = 0,
            @SerializedName("name") var name: String = "",
            @SerializedName("full_name") var fullName: String = "",
            @SerializedName("private") var private: Boolean = false,
            @SerializedName("description") var description: String = "",
            @SerializedName("fork") var fork: Boolean = false
    )

    data class Comment(
            @SerializedName("url") var url: String = "",
            @SerializedName("html_url") var htmlUrl: String = "",
            @SerializedName("id") var id: Int = 0,
            @SerializedName("user") var user: User = User(),
            @SerializedName("commit_id") var commitId: String = "",
            @SerializedName("created_at") var createdAt: String = "",
            @SerializedName("updated_at") var updatedAt: String = "",
            @SerializedName("body") var body: String = ""
    )

    data class Page(
            @SerializedName("page_name") var pageName: String = "",
            @SerializedName("title") var title: String = "",
            //The action that was performed on the page. Can be "created" or "edited".
            @SerializedName("action") var action: String = "",
            @SerializedName("sha") var sha: String = "",
            @SerializedName("html_url") var htmlUrl: String = ""
    )

    data class Issue(
            @SerializedName("url") var url: String = "",
            @SerializedName("id") var id: Int = 0,
            @SerializedName("number") var number: Int = 0,
            @SerializedName("title") var title: String = ""
    )
}

