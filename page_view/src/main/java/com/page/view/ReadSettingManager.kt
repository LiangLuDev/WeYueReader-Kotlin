package com.page.view


import com.page.view.utils.ScreenUtils
import com.page.view.utils.SharedPreUtils

/**
 * 阅读器的配置管理
 * fixme 放入主题设置里
 */
object ReadSettingManager {

    private val sharedPreUtils: SharedPreUtils = SharedPreUtils.getInstance()

    @JvmStatic
    var brightness: Int
        get() = sharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40)
        set(progress) = sharedPreUtils.putInt(SHARED_READ_BRIGHTNESS, progress)
    @JvmStatic
    val isBrightnessAuto: Boolean
        get() = sharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false)
    @JvmStatic
    var textSize: Int
        get() = sharedPreUtils.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(28))
        set(textSize) = sharedPreUtils.putInt(SHARED_READ_TEXT_SIZE, textSize)
    @JvmStatic
    val isDefaultTextSize: Boolean
        get() = sharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false)
    @JvmStatic
    var pageMode: Int
        get() = sharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageView.PAGE_MODE_COVER)
        set(mode) = sharedPreUtils.putInt(SHARED_READ_PAGE_MODE, mode)
    @JvmStatic
    val readBgTheme: Int
        get() = sharedPreUtils.getInt(SHARED_READ_BG, READ_BG_DEFAULT)
    @JvmStatic
    var isNightMode: Boolean
        get() = sharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false)
        set(isNight) = sharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE, isNight)
    @JvmStatic
    var isVolumeTurnPage: Boolean
        get() = sharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false)
        set(isTurn) = sharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn)
    @JvmStatic
    var isFullScreen: Boolean
        get() = sharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN, false)
        set(isFullScreen) = sharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen)

    @JvmStatic
    fun setReadBackground(theme: Int) {
        sharedPreUtils.putInt(SHARED_READ_BG, theme)
    }

    @JvmStatic
    fun setAutoBrightness(isAuto: Boolean) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto)
    }


    /*************实在想不出什么好记的命名方式。。 */
    const val READ_BG_DEFAULT = 0
    const val READ_BG_1 = 1
    const val READ_BG_2 = 2
    const val READ_BG_3 = 3
    const val READ_BG_4 = 4
    const val NIGHT_MODE = 5

    private const val SHARED_READ_BG = "shared_read_bg"
    private const val SHARED_READ_BRIGHTNESS = "shared_read_brightness"
    private const val SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto"
    private const val SHARED_READ_TEXT_SIZE = "shared_read_text_size"
    private const val SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default"
    private const val SHARED_READ_PAGE_MODE = "shared_read_mode"
    private const val SHARED_READ_NIGHT_MODE = "shared_night_mode"
    private const val SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page"
    private const val SHARED_READ_FULL_SCREEN = "shared_read_full_screen"


}
