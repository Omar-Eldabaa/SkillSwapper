package com.example.skillswapper.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.R
import com.example.skillswapper.databinding.ActivityRegisterBinding
import com.example.skillswapper.login.LoginActivity
import com.example.skillswapper.login.LoginActivityViewModel
import com.example.skillswapper.showMessage
import com.example.skillswapper.userskills.UserSkillsSetupActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
        subscribeToLiveData()
    }


    private fun subscribeToLiveData() {
        viewModel.messageLiveData.observe(this){message->
            showMessage(
                message.message ?: "Something went wrong",
                posActionName = "Ok",
                posAction = message.posActionClick,
                negActionName = message.negActionName,
                neAction = message.negActionClick,
                isCancelable = message.isCancelable
            )
        }

        viewModel.events.observe(this){events->
            when(events){
                RegisterViewEvent.NavigateToLogin->{
                    navigateToLogin()
                }
                RegisterViewEvent.NavigateToHome->{
                    navigateToHome()
                }
                RegisterViewEvent.NavigateToSetupSkills->{
                    navigateToSetupSkillsActivity()
                }
            }

        }
    }

    private fun navigateToSetupSkillsActivity() {
        val intent = Intent(this, UserSkillsSetupActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.slide_in_left,  // الأنيميشن لما يفتح
            android.R.anim.slide_out_right// الأنيميشن لما يرجع
        )
        startActivity(intent, options.toBundle())
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.slide_in_left,  // الأنيميشن لما يفتح
            android.R.anim.slide_out_right// الأنيميشن لما يرجع
        )
        startActivity(intent, options.toBundle())
    }

    private fun navigateToHome() {
            //Navigate To Home
    }

    private fun initViews() {
        viewModel = ViewModelProvider(this)[RegisterActivityViewModel::class.java]
        viewBinding.vm= viewModel
        viewBinding.lifecycleOwner= this
    }

}