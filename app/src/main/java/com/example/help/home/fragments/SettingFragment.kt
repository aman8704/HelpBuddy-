package com.example.help.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.help.R
import com.example.help.home.needs.Need
import com.example.help.home.needs.NeedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingFragment : Fragment() {

    private lateinit var myNeedsRv: RecyclerView
    private lateinit var acceptedNeedsRv: RecyclerView
    private lateinit var totalEarningsTv: TextView

    private lateinit var myNeedsAdapter: NeedAdapter
    private lateinit var acceptedNeedsAdapter: NeedAdapter

    private var myNeedsList = mutableListOf<Need>()
    private var acceptedNeedsList = mutableListOf<Need>()

    private lateinit var database: DatabaseReference
    private lateinit var earningsDatabase: DatabaseReference // New Reference
    private lateinit var auth: FirebaseAuth

    private val EXPIRY_TIME = 5 * 60 * 60 * 1000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Needs")
        // Initialize the Earnings reference
        earningsDatabase = FirebaseDatabase.getInstance().getReference("Earnings")

        myNeedsRv = view.findViewById(R.id.my_needs_rv)
        acceptedNeedsRv = view.findViewById(R.id.accepted_needs_rv)
        totalEarningsTv = view.findViewById(R.id.profile_total_earnings)

        myNeedsRv.layoutManager = LinearLayoutManager(context)
        acceptedNeedsRv.layoutManager = LinearLayoutManager(context)

        myNeedsAdapter = NeedAdapter(myNeedsList)
        acceptedNeedsAdapter = NeedAdapter(acceptedNeedsList)

        myNeedsRv.adapter = myNeedsAdapter
        acceptedNeedsRv.adapter = acceptedNeedsAdapter

        fetchData()
        fetchTotalEarnings() // New function call

        return view
    }

    private fun fetchData() {
        val currentUid = auth.currentUser?.uid ?: return

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myNeedsList.clear()
                acceptedNeedsList.clear()

                val currentTime = System.currentTimeMillis()

                for (data in snapshot.children) {
                    val need = data.getValue(Need::class.java) ?: continue

                    // 1. Delete expired posts from DB (This is why we separate earnings!)
                    if (currentTime - need.timestamp >= EXPIRY_TIME) {
                        data.ref.removeValue()
                        continue
                    }

                    // 2. Filter for My Requests
                    if (need.userId == currentUid) {
                        myNeedsList.add(need)
                    }

                    // 3. Filter for My Accepted Tasks
                    if (need.acceptedBy == currentUid) {
                        acceptedNeedsList.add(need)
                    }
                }

                myNeedsList.sortByDescending { it.timestamp }
                acceptedNeedsList.sortByDescending { it.timestamp }

                myNeedsAdapter.updateData(myNeedsList)
                acceptedNeedsAdapter.updateData(acceptedNeedsList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // NEW FUNCTION: Fetches earnings from a permanent node
    private fun fetchTotalEarnings() {
        val currentUid = auth.currentUser?.uid ?: return

        earningsDatabase.child(currentUid).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0
                for (data in snapshot.children) {
                    // Get amount. We use Double to avoid precision issues
                    val amount = data.child("amount").getValue(Double::class.java) ?: 0.0
                    total += amount
                }
                totalEarningsTv.text = "₹ %.2f".format(total)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}