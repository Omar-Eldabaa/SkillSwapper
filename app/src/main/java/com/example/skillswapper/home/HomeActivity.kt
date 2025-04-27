package com.example.skillswapper.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.settings.SettingsActivity
import com.example.skillswapper.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var viewModel: HomeActivityViewModel
    lateinit var viewBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding =ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
        initViews()
        viewBinding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            startActivity(intent, options.toBundle())
        }
    }
// add 1 feature
    private fun initViews() {
        viewModel=ViewModelProvider(this)[HomeActivityViewModel::class.java]
    }
}