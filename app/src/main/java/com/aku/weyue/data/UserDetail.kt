package com.aku.weyue.data

/**
 * @author Zsc
 * @date   2019/6/8
 * @desc
 */
class UserDetail constructor(userBean: UserBean? = null) : UserBean() {
    var likebooks: List<BookBean> = listOf()

    init {
        userBean?.let {
            name = it.name
            icon = it.icon
            brief = it.brief
            token = it.token
            nickname = it.nickname
        }
    }

    fun getUser():UserBean{
       return UserBean().also {
            it.name = name
            it.icon = icon
            it.brief = brief
            it.token = token
            it.nickname = nickname
        }
    }


}