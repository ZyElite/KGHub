package com.zyelite.kghub.utils

/**
 * @author ZyElite
 * @create 2018/4/10
 * @description Constant
 */
object Constant {
    var CURRENT_LOGIN = "CURRENT_LOGIN"
    var TOKEN = "TOKEN"

    // CreateEvent ref_type The object that was created. Can be one of "repository", "branch", or "tag"
    //The object that was deleted. Can be "branch" or "tag".
    var REPOSITORY = "repository"
    var BRANCH = "branch"
    var TAG = "tag"

    //GollumEvent  The action that was performed on the page. Can be "created" or "edited".
    //InstallationEvent  The action that was performed. Can be either "created" or "deleted".
    var CREATED = "created"
    var EDITED = "edited"
    var DELETED = "deleted"
    //The action that was performed. Can be one of "assigned", "unassigned", "labeled", "unlabeled", "opened", "edited", "milestoned", "demilestoned", "closed", or "reopened".

    var assigned = ""
    var unassigned = ""
    var labeled = ""
    var unlabeled = ""
    var opened = ""
    var reopened = ""
    var milestoned = ""
    var demilestoned = ""
    var closed = ""

}