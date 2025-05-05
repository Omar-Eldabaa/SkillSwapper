package com.example.skillswapper.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.skillswapper.databinding.FragmentScheduledSessionsBinding
import com.example.skillswapper.model.SessionWithDetails
import com.google.firebase.auth.FirebaseAuth

class ScheduledSessionsFragment : Fragment() {

    private var _binding: FragmentScheduledSessionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ScheduledSessionsViewModel
    private lateinit var adapter: ScheduledSessionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduledSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ScheduledSessionsViewModel::class.java]

        adapter = ScheduledSessionsAdapter(emptyList())
        binding.scheduledSessionsRecyclerView.adapter = adapter

        observeViewModel()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.loadScheduledSessions(currentUserId)
        }
    }

    private fun observeViewModel() {
        viewModel.sessions.observe(viewLifecycleOwner, Observer { sessions ->
            binding.progressBar.visibility = View.GONE
            updateUI(sessions)
        })
    }

    private fun updateUI(sessions: List<SessionWithDetails>) {
        adapter.updateData(sessions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
