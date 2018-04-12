package com.zyelite.kghub.annotations

import android.support.annotation.StringDef
import com.zyelite.kghub.model.constant.IssuesEvent

/**
 * @author ZyElite
 * @create 2018/4/12
 * @description Issues
 */


@StringDef(
        IssuesEvent.EDITED,
        IssuesEvent.ASSIGNED,
        IssuesEvent.UNASSIGNED,
        IssuesEvent.LABELED,
        IssuesEvent.UNLABELED,
        IssuesEvent.OPENED,
        IssuesEvent.REOPENED,
        IssuesEvent.MILESTONED,
        IssuesEvent.DEMILESTONED,
        IssuesEvent.CLOSED)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE) //表示注解所存活的时间,在运行时,而不会存在. class 文件.
annotation class Issues//接口，定义新的注解类型w