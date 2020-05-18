package com.aku.weyue.api

import com.aku.aac.kchttp.data.ResultApi
import com.aku.weyue.data.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
interface BookApi {

    companion object {
        const val BASE_URL = "http://www.luliangdev.cn/"

        const val BOOK_IMG_URL = "http://statics.zhuishushenqi.com"
    }

    @GET("api/classify")
    fun bookClassifyAsync(): Deferred<ResultApi<BookTotal>>

    /**
     * 用户注册
     *
     * @return
     */
    @POST("api/user/register")
    @FormUrlEncoded
    fun registerAsync(@Field("name") username: String, @Field("password") password: String)
            : Deferred<ResultApi<String>>

    /**
     * 用户登录
     *
     * @return
     */
    @GET("api/user/login")
    fun loginAsync(
        @Query("name") username: String,
        @Query("password") password: String
    ): Deferred<ResultApi<UserBean>>

    /**
     * 修改用户密码
     *
     * @param password 用户密码
     * @return
     */
    @PUT("api/user/password")
    @FormUrlEncoded
    fun updatePasswordAsync(@Field("password") password: String)
            : Deferred<ResultApi<String>>

    /**
     * 修改用户信息
     *
     * @param nickname 昵称
     * @param brief    简介
     * @return
     */
    @PUT("api/user/userinfo")
    @FormUrlEncoded
    fun updateUserInfoAsync(@Field("nickname") nickname: String, @Field("brief") brief: String): Deferred<ResultApi<String>>

    /**
     * 修改用户信息
     *
     * @return
     */
    @GET("api/user/userinfo")
    fun getUserInfoAsync(): Deferred<ResultApi<UserDetail>>


    /**
     * 更换用户头像
     *
     * @return
     */
    @Multipart
    @POST("api/user/uploadavatar")
    fun avatarAsync(@Part part: MultipartBody.Part): Deferred<ResultApi<String>>

    /**
     * 获取服务器书架信息
     *
     * @return
     */
    @GET("/user/bookshelf")
    fun getBookShelf(): Deferred<ResultApi<List<BookBean>>>

    /**
     * 加入书架到服务器
     *
     * @param bookid 书籍id
     * @return
     */
    @POST("api/user/bookshelf")
    @FormUrlEncoded
    fun addBookShelfAsync(@Field("bookid") bookid: String): Deferred<ResultApi<String>>

    /**
     * 移除书架
     *
     * @return
     */
//    @HTTP(method = "DELETE", path = "/user/bookshelf", hasBody = true)
//    fun deleteBookShelf(@Body bean: DeleteBookBean): Deferred<ResultApi<String>>

    /**
     * 用户反馈
     *
     * @param qq       qq
     * @param feedback 反馈内容
     * @return
     */
    @POST("api/feedback")
    @FormUrlEncoded
    fun userFeedBackAsync(
        @Field("qq") qq: String,
        @Field("feedback") feedback: String
    ): Deferred<ResultApi<String>>

    /**
     * 用户反馈
     *
     * @return
     */
//    @GET("api/appupdate")
//    fun appUpdate(): Deferred<ResultApi<AppUpdateBean>>

    /**
     * 获取分类下的书籍
     *
     * @param type
     * @param major
     * @param page
     * @return
     */
    @GET("api/books")
    fun booksAsync(
        @Query("type") type: String,
        @Query("major") major: String,
        @Query("page") page: Int
    ): Deferred<ResultApi<List<BookBean>>>

    /**
     * 获取书籍信息
     *
     * @param bookId
     * @return
     */
    @GET("api/books/{bookId}")
    fun bookInfoAsync(@Path("bookId") bookId: String)
            : Deferred<ResultApi<BookBean>>

    /**
     * 获取书籍目录
     *
     * @param bookId
     * @return
     */
    @GET("api/books/{bookId}/chapters")
    fun bookChaptersAsync(@Path("bookId") bookId: String)
            : Deferred<ResultApi<BookChaptersBean>>

    /**
     * 根据link获取正文
     *
     * @param link 正文链接
     * @return
     */
    @GET("http://chapterup.zhuishushenqi.com/chapter/{link}")
    fun bookContentAsync(@Path("link") link: String)
            : Deferred<ChapterContentBean>

    /**
     * 根据tag获取书籍
     *
     * @param bookTag
     * @param page
     * @return
     */
    @GET("api/books/tag")
    fun booksByTagAsync(
        @Query("bookTag") bookTag: String,
        @Query("page") page: Int
    ): Deferred<ResultApi<MutableList<BookBean>>>


}