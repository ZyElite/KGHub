package com.zyelite.kghub.model.constant

/**
 * @author ZyElite
 * @create 2018/4/16
 * @description CreateEvent
 */
object CreateEvent {
    // CreateEvent ref_type The object that was created. Can be one of "repository", "branch", or "tag"
    //The object that was deleted. Can be "branch" or "tag".
    const val REPOSITORY = "repository"
    const val BRANCH = "branch"
    const val TAG = "tag"
}