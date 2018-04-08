package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import java.util.*

/**
 * @author zy
 * @date 2018/3/8
 * @des UserModel
 */
open class UserModel : RealmObject() {
    private var login: String = ""
    private var id: String = ""
    private var name: String = ""
    @SerializedName("avatar_url")
    private var avatarUrl: String = ""
    //个人主页
    @SerializedName("html_url")
    private var htmlUrl: String = ""
    //公司
    private var company: String = ""
    private var blog: String = ""
    private var location: String = ""
    private var email: String = ""
    private var bio: String = ""
    //个人仓库
    @SerializedName("public_repos")
    private var publicRepos: Int = 0
    //
    @SerializedName("public_gists")
    private var publicGists: Int = 0
    //追随者
    private var followers: Int = 0
    //跟随
    private var following: Int = 0
    //创建时间
    @SerializedName("created_at")
    private var createdAt: Date? = null
    //更新时间
    @SerializedName("updated_at")
    private var updatedAt: Date? = null



    fun getLogin(): String {
        return login
    }

    fun setLogin(login: String) {
        this.login = login
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getAvatarUrl(): String {
        return avatarUrl
    }

    fun setAvatarUrl(avatarUrl: String) {
        this.avatarUrl = avatarUrl
    }

    fun getHtmlUrl(): String {
        return htmlUrl
    }

    fun setHtmlUrl(htmlUrl: String) {
        this.htmlUrl = htmlUrl
    }

//    fun getType(): UserType {
//        return type
//    }
//
//    fun setType(type: UserType) {
//        this.type = type
//    }

    fun getCompany(): String {
        return company
    }

    fun setCompany(company: String) {
        this.company = company
    }

    fun getBlog(): String {
        return blog
    }

    fun setBlog(blog: String) {
        this.blog = blog
    }

    fun getLocation(): String {
        return location
    }

    fun setLocation(location: String) {
        this.location = location
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getBio(): String {
        return bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getPublicRepos(): Int {
        return publicRepos
    }

    fun setPublicRepos(publicRepos: Int) {
        this.publicRepos = publicRepos
    }

    fun getPublicGists(): Int {
        return publicGists
    }

    fun setPublicGists(publicGists: Int) {
        this.publicGists = publicGists
    }

    fun getFollowers(): Int {
        return followers
    }

    fun setFollowers(followers: Int) {
        this.followers = followers
    }

    fun getFollowing(): Int {
        return following
    }

    fun setFollowing(following: Int) {
        this.following = following
    }

    fun getCreatedAt(): Date? {
        return createdAt
    }

    fun setCreatedAt(createdAt: Date) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt(): Date? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: Date) {
        this.updatedAt = updatedAt
    }

//    fun isUser(): Boolean {
//        return UserType.User == type
//    }

}