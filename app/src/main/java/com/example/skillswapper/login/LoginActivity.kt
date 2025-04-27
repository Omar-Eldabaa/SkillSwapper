package com.example.skillswapper.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.databinding.ActivityLoginBinding
import com.example.skillswapper.home.HomeActivity
import com.example.skillswapper.register.RegisterActivity
import com.example.skillswapper.showMessage
import com.example.skillswapper.userskills.UserSkillsSetupActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel:LoginActivityViewModel
    private lateinit var viewBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
        subscribeToLiveData()

    }

    private fun subscribeToLiveData() {
        viewModel.events.observe(this){events->
            when(events){
                LoginViewEvent.NavigateToHome->{
                    navigateToHome()
                }
                LoginViewEvent.NavigateToRegister->{
                    navigateToRegister()
                }
                LoginViewEvent.NavigateToUserSkillsSetup->{
                    navaigateToUserSkillsSetup()
                }
            }
        }
        viewModel.messageLiveData.observe(this){
            showMessage(
                it.message?:"Something went wrong",
                posActionName = "Ok",
                posAction =it.posActionClick
            )
        }
    }

    private fun navaigateToUserSkillsSetup() {
        val intent = Intent(this, UserSkillsSetupActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.slide_in_left,  // الأنيميشن لما يفتح
            android.R.anim.slide_out_right// الأنيميشن لما يرجع
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.slide_in_left,  // الأنيميشن لما يفتح
            android.R.anim.slide_out_right// الأنيميشن لما يرجع
        )
        startActivity(intent, options.toBundle())

    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        startActivity(intent, options.toBundle())

        finish()
    }

    private fun initViews() {
        viewModel =ViewModelProvider(this)[LoginActivityViewModel::class.java]
        viewBinding.vm= viewModel
        viewBinding.lifecycleOwner= this
    }

    }

