package com.aku.weyue.koin

import com.aku.aac.kchttp.KcHttp
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.local.AppDatabase
import com.aku.weyue.ui.UserViewModel
import com.aku.weyue.ui.book.BookDetailViewModel
import com.aku.weyue.ui.feedback.FeedbackViewModel
import com.aku.weyue.ui.list.BookListTypeViewModel
import com.aku.weyue.ui.scan.LocalBookViewModel
import com.aku.weyue.ui.shelf.BookShelfViewModel
import com.aku.weyue.ui.type.BookTotalViewModel
import com.aku.weyue.ui.user.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object Injector {

    internal val serviceModule: Module = module {
        //BookApi单例注入
        single {
            KcHttp.createApi<BookApi>(BookApi.BASE_URL)
        }
        //AppDatabase注入
        single {
            AppDatabase.getInstance(androidContext())
        }

        single {
            get<AppDatabase>().bookTypeDao()
        }
        single {
            get<AppDatabase>().bookRecordDao()
        }
        single {
            get<AppDatabase>().bookDao()
        }
    }
    /**
     * ！！！注意带参数的viewModel不能直接使用activityViewModel()注入
     */
    internal val viewModels = module {
        viewModel {
            //使用activityViewModel()注入则是同一个
            BookTotalViewModel()
        }
        viewModel {
            LoginViewModel()
        }
        viewModel {
            BookListTypeViewModel(get())
        }
        viewModel {
            UserViewModel()
        }
        viewModel { (bookId: String) ->
            BookDetailViewModel(bookId, get())
        }
        viewModel {
            BookShelfViewModel()
        }
        viewModel {
            FeedbackViewModel()
        }
        viewModel{
            LocalBookViewModel()
        }
    }

}
