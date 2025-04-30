package com.example.skillswapper.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.R
import com.example.skillswapper.chat.ChatFragment
import com.example.skillswapper.settings.SettingsActivity
import com.example.skillswapper.databinding.ActivityHomeBinding
import com.example.skillswapper.matching.MatchingFragment
import com.example.skillswapper.search.SearchFragment
import com.example.skillswapper.sessions.SessionsFragment

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
    private fun initViews() {
        viewModel=ViewModelProvider(this)[HomeActivityViewModel::class.java]
        push(MatchingFragment())
        viewBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_matching -> {
                    push(MatchingFragment())
                }
                R.id.nav_search -> {
                    push(SearchFragment())
                }
                R.id.nav_chats -> {
                    push(ChatFragment())
                }
                R.id.nav_sessions -> {
                    push(SessionsFragment())
                }
            }
            return@setOnItemSelectedListener true
        }

    }

    fun push (fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container , fragment)
            .commit()
    }
}