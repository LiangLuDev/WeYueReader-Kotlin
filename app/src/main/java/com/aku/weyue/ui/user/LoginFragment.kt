package com.aku.weyue.ui.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseVMFragment
import com.aku.weyue.R
import com.aku.weyue.databinding.LoginFragBinding
import com.aku.weyue.ui.UserViewModel
import kotlinx.android.synthetic.main.main_frag_1.*

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
class LoginFragment : BaseVMFragment<LoginFragBinding>(),
    View.OnClickListener {

    override val layout = R.layout.login_frag

    private val viewModel: LoginViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        binding.login = this
        binding.user = viewModel
        viewModel.userLogin.observe(this, Observer {
            if (it != null) {
                userViewModel.user.postValue(it)
                findNavController().popBackStack()
            }
        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> {
                viewModel.loginIn()
            }
        }
    }
}