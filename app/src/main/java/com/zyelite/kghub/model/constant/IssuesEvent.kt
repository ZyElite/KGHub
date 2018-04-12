package com.zyelite.kghub.model.constant

/**
 * @author ZyElite
 * @create 2018/4/12
 * @description IssuesEvent
 */
object IssuesEvent {

    //The action that was performed. Can be one of "assigned", "unassigned", "labeled", "unlabeled", "opened", "edited", "milestoned", "demilestoned", "closed", or "reopened".
    const val EDITED = "edited"
    const val ASSIGNED = "assigned"
    const val UNASSIGNED = "unassigned"
    const val LABELED = "labeled"
    const val UNLABELED = "unlabeled"
    const val OPENED = "opened"
    const val REOPENED = "reopened"
    const val MILESTONED = "milestoned"
    const val DEMILESTONED = "demilestoned"
    const val CLOSED = "closed"

}