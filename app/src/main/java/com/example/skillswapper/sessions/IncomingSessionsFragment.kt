package com.example.skillswapper.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillswapper.databinding.FragmentIncomingSessionsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class IncomingSessionsFragment : Fragment() {

    private lateinit var binding: FragmentIncomingSessionsBinding
    lateinit var  viewModel: IncomingSessionsViewModel
    private lateinit var adapter: IncomingSessionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomingSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this)[IncomingSessionsViewModel::class.java]



        // Setup RecyclerView
        binding.incomingSessionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe the incoming sessions
        viewModel.incomingSessions.observe(viewLifecycleOwner, Observer { sessions ->
            adapter = IncomingSessionsAdapter(
                sessions,
                onAcceptClicked = { session ->
                    viewModel.acceptSession(session)
                    Snackbar.make(binding.root, "Session Accepted", Snackbar.LENGTH_SHORT).show()

                },
                onRejectClicked = { session ->
                    viewModel.rejectSession(session.session.id)
                    Snackbar.make(binding.root, "Session Rejected", Snackbar.LENGTH_SHORT).show()

                }
            )

            binding.incomingSessionsRecyclerView.adapter = adapter
        })

        // Observe the loading state
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Assuming we get the current user's ID here
        val currentUserId = Firebase.auth.currentUser?.uid // Replace with actual ID
        viewModel.loadIncomingSessions(currentUserId!!)
    }
}
