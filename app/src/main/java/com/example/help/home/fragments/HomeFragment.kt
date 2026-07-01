package com.example.help.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.help.R
import com.example.help.home.needs.Need
import com.example.help.home.needs.NeedAdapter
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NeedAdapter
    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var needList = mutableListOf<Need>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.needs_rv)
        progressBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        // Initialize adapter with the list
        adapter = NeedAdapter(needList)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("Needs")

        fetchNeeds()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNeeds(newText)
                return true
            }
        })

        return view
    }

    private fun fetchNeeds() {
        progressBar.visibility = View.VISIBLE

        // Use a query to get only items where status is "Pending"
        // This automatically removes "Accepted" items from the Home Fragment
        val query = database.orderByChild("status").equalTo("Pending")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                needList.clear()
                val currentTime = System.currentTimeMillis()
                val fiveHoursInMillis = 5 * 60 * 60 * 1000L

                for (data in snapshot.children) {
                    val need = data.getValue(Need::class.java)
                    if (need != null) {
                        // 1. Auto-delete logic: If older than 5 hours, remove from DB
                        if (currentTime - need.timestamp > fiveHoursInMillis) {
                            database.child(need.id).removeValue()
                        } else {
                            // 2. Otherwise, add to the local list for display
                            needList.add(need)
                        }
                    }
                }

                // Sort by newest first
                needList.sortByDescending { it.timestamp }

                // Update the UI
                adapter.updateData(needList)
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun filterNeeds(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            needList
        } else {
            needList.filter {
                it.needText.contains(query, ignoreCase = true) ||
                        it.userName.contains(query, ignoreCase = true) ||
                        it.deliveryLocation.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(filteredList as MutableList<Need>)
    }
}