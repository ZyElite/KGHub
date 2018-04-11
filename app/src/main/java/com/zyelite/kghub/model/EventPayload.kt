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
        //PushEvent CreateEvent
        @SerializedName("ref") var ref: String = "",
        //CreateEvent
        @SerializedName("ref_type") var refType: String = "",
        @SerializedName("master_branch") var masterBranch: String = "",
        @SerializedName("pusher_type") var pusherType: String = "",
        @SerializedName("description") var description: String = "",
        //ForkEvent
        @SerializedName("forkee") var forkee: Forkee = Forkee()

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
            @SerializedName("html_url") var htmlUrl: String = "",
            @SerializedName("description") var description: String = "",
            @SerializedName("fork") var fork: Boolean = false
    )
}

