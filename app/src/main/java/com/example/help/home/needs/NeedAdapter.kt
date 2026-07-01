package com.example.help.home.needs

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.help.R

class NeedAdapter(private var needs: List<Need>) : RecyclerView.Adapter<NeedAdapter.NeedViewHolder>() {

    class NeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTv: TextView = view.findViewById(R.id.user_name_tv)
        val needTextTv: TextView = view.findViewById(R.id.need_text_tv)
        val locationTv: TextView = view.findViewById(R.id.delivery_location_tv)
        val totalPriceTv: TextView = view.findViewById(R.id.total_price_tv)
        val timestampTv: TextView = view.findViewById(R.id.timestamp_tv)
        val deliveredBtn: Button = view.findViewById(R.id.delivered_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_need, parent, false)
        return NeedViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "UseKtx")
    override fun onBindViewHolder(holder: NeedViewHolder, position: Int) {
        val need = needs[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        holder.userNameTv.text = need.userName
        holder.needTextTv.text = need.needText
        holder.locationTv.text = "Deliver to: ${need.deliveryLocation}"

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.timestampTv.text = timeFormat.format(Date(need.timestamp))

        // --- UI STATUS LOGIC ---
        if (need.status == "Delivered") {
            holder.totalPriceTv.text = "COMPLETED SUCCESSFULLY"
            holder.totalPriceTv.setTextColor(Color.parseColor("#2E7D32")) // Green
            holder.deliveredBtn.visibility = View.GONE
        } else {
            holder.totalPriceTv.text = "Total: ₹ ${need.totalPrice}"
            holder.totalPriceTv.setTextColor(Color.BLACK)

            // Show button ONLY if I am the helper and the status is Accepted
            if (need.status == "Accepted" && need.acceptedBy == currentUserId) {
                holder.deliveredBtn.visibility = View.VISIBLE
            } else {
                holder.deliveredBtn.visibility = View.GONE
            }
        }

        // --- BUTTON CLICK: MARK DELIVERED & SAVE PERMANENT EARNINGS ---
        holder.deliveredBtn.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("Needs")
            val earningsRef = FirebaseDatabase.getInstance().getReference("Earnings")
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            need.id?.let { id ->
                val updates = hashMapOf<String, Any>(
                    "status" to "Delivered",
                    "deliveredAt" to ServerValue.TIMESTAMP
                )

                // 1. Mark as delivered in the Needs node (Temporary node)
                dbRef.child(id).updateChildren(updates).addOnSuccessListener {

                    // 2. Save to PERMANENT Earnings node (Stays forever even if Need is deleted)
                    if (userId != null) {
                        val earningRecord = hashMapOf(
                            "needId" to id,
                            "amount" to need.extraCharge, // Saving the profit/earning amount
                            "helperId" to userId,
                            "timestamp" to ServerValue.TIMESTAMP, // Permanent timestamp
                            "needTitle" to need.needText
                        )

                        // Save under Earnings -> UserID -> AutoID
                        earningsRef.child(userId).push().setValue(earningRecord)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, NeedDetailActivity::class.java)
            intent.putExtra("NEED_DATA", need)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = needs.size

    fun updateData(newList: List<Need>) {
        needs = newList
        notifyDataSetChanged()
    }
}
