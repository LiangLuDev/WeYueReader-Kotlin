package com.aku.weyue.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.page.view.data.CollBookBean


/**
 * Created by Liang_Lu on 2017/12/6.
 */
@Entity(tableName = "book")
class BookBean {

    /**
     * bookId : 59ba0dbb017336e411085a4e
     * title : 元尊
     * author : 天蚕土豆
     * longIntro : 彼时的归途，已是一条命运倒悬的路。 昔日的荣华，如白云苍狗，恐大梦一场。 少年执笔，龙蛇飞动。 是为一抹光芒劈开暮气沉沉之乱世，问鼎玉宇苍穹。 复仇之路，与吾同行。 一口玄黄真气定可吞天地日月星辰，雄视草木苍生。 铁画夕照，雾霭银钩，笔走游龙冲九州。 横姿天下，墨洒青山，鲸吞湖海纳百川。
     * cover : /agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F2107590%2F2107590_55d1f1bf10684e62a51d9f0ca3dd08fc.jpg%2F
     * majorCate : 玄幻
     * minorCate : 东方玄幻
     * hasCopyright : true
     * contentType : txt
     * latelyFollower : 95840
     * wordCount : 432088
     * serializeWordCount : 3717
     * retentionRatio : 51.69
     * updated : 2017-12-06T15:49:21.246Z
     * chaptersCount : 169
     * lastChapter : 正文 第一百六十八章 再遇
     * rating : {"count":6755,"score":8.119,"isEffect":true}
     * tags : []
     * gender : ["male"]
     */
    @PrimaryKey
    var _id: String = ""

    var title: String? = null

    var author: String? = null

    var longIntro: String? = null

    var cover: String? = null

    var majorCate: String? = null

    var minorCate: String? = null

    var isHasCopyright: Boolean = false
    var isCollect: Boolean = false

    var contentType: String? = null

    var latelyFollower: Int = 0

    var wordCount: Int = 0

    var serializeWordCount: Int = 0

    var retentionRatio: String? = null

    var updated: String? = null

    var chaptersCount: Int = 0

    var lastChapter: String? = null

    var copyright: String? = null

    var rating: RatingBean? = null
    var tags: List<String>? = null
    var gender: List<String>? = null

    //是否更新或未阅读
    var isUpdate = true
    //是否是本地文件
    var isLocal = false


    class RatingBean {
        /**
         * count : 6755
         * score : 8.119
         * isEffect : true
         */

        var count: Int = 0
        var score: Double = 0.0
        var isEffect: Boolean = false
    }

    fun createCollBookBean(): CollBookBean {
        val bean = CollBookBean()
        bean.bookId = _id
        bean.isLocal = isLocal
        //        bean.setHasCp(isHasCp());
        bean.lastChapter = lastChapter
        return bean
    }

}
