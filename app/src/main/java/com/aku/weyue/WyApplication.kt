package com.aku.weyue

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.aku.aac.kchttp.KcHttp
import com.aku.common.widget.StateLayout
import com.aku.weyue.api.HttpConfig
import com.aku.weyue.data.source.SpSource
import com.aku.weyue.koin.Injector
import com.aku.weyue.util.ThemeUtils
import com.blankj.utilcode.util.BarUtils
import com.page.view.utils.BookUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import com.scwang.smartrefresh.layout.header.BezierRadarHeader
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
class WyApplication : Application() {


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initHttp()
        initKoin()
        initRefresh()
        initLoadLayout()
        initPageView()
        BookUtils.init(this)
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ) {
                    setTranslucentStatus(activity, true)
                }
                activity.setTheme(ThemeUtils.getSelectTheme(SpSource.appTheme))

            }

            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

        })

    }

    @TargetApi(19)
    private fun setTranslucentStatus(activity: Activity, on: Boolean) {
        BarUtils.setStatusBarColor(
            activity,
            ContextCompat.getColor(activity, R.color.transparent)
        )
        /*val win = activity.window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams*/
    }

    /**
     * fixme 在需要pageView的时候再去加载 数据源从数据库读取
     *
     */
    private fun initPageView() {

    }

    @SuppressLint("ResourceType")
    private fun initRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            layout.setPrimaryColorsId(typedValue.resourceId, R.color.white)
            BezierRadarHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            BallPulseFooter(context).setSpinnerStyle(
                SpinnerStyle.Translate
            )
        }
    }

    private fun initHttp() {
        KcHttp.okHttpClientBuilder
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .addInterceptor(HttpConfig.tokenInterceptor())
    }

    private fun initLoadLayout() {
        StateLayout.config
            .setErrorText("出错啦~请稍后重试！")
            .setEmptyText("抱歉，暂无数据")
            .setNoNetworkText("无网络连接，请检查您的网络···")
            .setErrorImage(R.drawable.ic_error_icon)
            .setEmptyImage(R.drawable.ic_empty_error)
            .setNoNetworkImage(R.drawable.ic_net_error)
            .setAllTipTextColor(R.color.black)
            .setAllTipTextSize(14)
            .setReloadButtonText("点我重试哦")
            .setReloadButtonTextSize(14)
            .setReloadButtonTextColor(R.color.black)
            .setReloadButtonWidthAndHeight(150, 40)
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@WyApplication)
            modules(
                Injector.serviceModule,
                Injector.viewModels
            )
        }
    }

}