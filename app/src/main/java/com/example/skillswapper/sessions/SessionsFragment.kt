package com.example.skillswapper.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.databinding.FragmentSessionsBinding
import com.google.android.material.tabs.TabLayoutMediator

class SessionsFragment : Fragment() {

    private var _binding: FragmentSessionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SessionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // تهيئة الـ ViewBinding هنا
        _binding = FragmentSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SessionsViewModel::class.java]
        initViews()
    }

    private fun initViews() {
        val adapter = SessionsPagerAdapter(this)
        binding.sessionsViewPager.adapter = adapter

        TabLayoutMediator(binding.sessionsTabLayout, binding.sessionsViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Incoming"
                else -> "Scheduled"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // تدمير الـ binding بعد التفاعل مع الـ view
    }
}
