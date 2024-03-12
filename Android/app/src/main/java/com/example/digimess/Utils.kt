package com.example.digimess

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.Serializable

@Serializable
data class forHistory (val username: String = "",
                       val date: String = "",
                       val action: String = "")

@Serializable
data class forHostelites (val email: String = "",
                          val first_name: String = "",
                          val last_name: String = "",
                          val hostel: String = "",
                          val room_no: Int = 0)

@Serializable
data class forLeave (val username: String = "",
                     val leaving: String = "",
                     val joining: String = "")

@Serializable
data class forUsers (val username: String = "",
                     val password: String = "",
                     val email: String = "")

@Serializable
data class forAttendance (val email: String = "",
                          val date: String = "",
                          val state: String = "")

@Serializable
data class forMenu (val day: String = "",
                    val breakfast: String = "",
                    val lunch: String = "",
                    val dinner: String = "")

@Serializable
data class forBill (val per_day: Int = 0)

data class NotificationDC(val action: String, val date: String)

class NotificationAdapter(private val itemList: List<NotificationDC>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_rec_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.rvAction.text = item.action
        holder.rvDate.text = item.date
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvAction: TextView = itemView.findViewById(R.id.actionText)
        val rvDate: TextView = itemView.findViewById(R.id.dateText)
    }
}

data class AttendanceDC(var date: String, var icon: Int)

class AttendanceAdapter(private val itemList: List<AttendanceDC>) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_rec_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.rvDate.text = item.date
        holder.rvIcon.setImageResource(item.icon)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvDate: TextView = itemView.findViewById(R.id.dateAttendance)
        val rvIcon: ImageView = itemView.findViewById(R.id.iconAttendance)
    }
}

class CustomToast private constructor(
    private val context: Context,
    private val vector: Int,
    private val text: String
) {
    companion object {
        val TICK : Int = R.drawable.baseline_check_24
        val CROSS : Int = R.drawable.baseline_close_24
        val WIFIOFF : Int = R.drawable.baseline_wifi_off_24
        fun show(context: Context, vector: Int, text: String) {
            val toast = CustomToast(context, vector, text)
            toast.display()
        }
    }
    private fun display() {
        val inflater = LayoutInflater.from(context.applicationContext)
        val layout: View = inflater.inflate(R.layout.front_toast, null)

        val textView: TextView = layout.findViewById(R.id.tickToastText)
        textView.text = text

        val imageView: ImageView = layout.findViewById(R.id.tickToastImage)
        imageView.setImageResource(vector)

        val toast = Toast(context.applicationContext)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}