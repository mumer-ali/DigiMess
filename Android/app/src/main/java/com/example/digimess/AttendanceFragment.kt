package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendanceFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        val username : String = arguments?.getString("username").toString()
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val emptyLbl : TextView = view.findViewById(R.id.emptyLabel)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val items = mutableListOf<AttendanceDC>()
        loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val client = SupaDB.getClient()
                val response = client.postgrest["Attendance"].select {
                    filter("email", FilterOperator.LIKE, "${username}@%")
                }
                val data  = response.decodeList<forAttendance>()
                if (data.isNotEmpty()) {
                    for (d in data){
                        if (d.state == "A"){
                            items += AttendanceDC(d.date, R.drawable.baseline_close_24)
                        }
                        else{
                            items += AttendanceDC(d.date, R.drawable.baseline_check_24)
                        }
                    }
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    items.sortByDescending { item ->
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(item.date)
                        calendar
                    }
                    val adapter = AttendanceAdapter(items)
                    loading.visibility = View.INVISIBLE
                    recyclerView.adapter = adapter
                }
                else {
                    loading.visibility = View.INVISIBLE
                    emptyLbl.setText("There is nothing to show right now!")
                }
            }
            catch (e: Exception) {
                loading.visibility = View.INVISIBLE
                CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return view
    }
}
