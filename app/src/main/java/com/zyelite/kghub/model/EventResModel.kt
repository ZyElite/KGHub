package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import com.zyelite.kghub.annotations.Event
import java.util.*


/**
 * @author ZyElite
 * @create 2018/4/3
 * @description EventResModel
 */

data class EventResModel(
        @SerializedName("id") var id: String = "",
        @Event.Type
        @SerializedName("type") var type: String = Event.COMMIT_COMMENT_EVENT,
        @SerializedName("actor") var actor: User = User(),
        @SerializedName("repo") var repo: Repository = Repository(),
        @SerializedName("payload") var payload: EventPayload = EventPayload(),
        @SerializedName("public") var public: Boolean = false,
        @SerializedName("created_at") var createdAt: Date
)



