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


class NotificationsFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view : View = inflater.inflate(R.layout.fragment_notifications, container, false)

        val username : String = arguments?.getString("username").toString()
        val recycler : RecyclerView = view.findViewById(R.id.recyclerViewNotBar)
        val emptyLbl : TextView = view.findViewById(R.id.emptyLabel)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)
        val items = mutableListOf<NotificationDC>()
        loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val client = SupaDB.getClient()
                val response = client.postgrest["History"].select {
                    filter("username", FilterOperator.EQ, username)
                }
                val data  = response.decodeList<forHistory>()
                if (data.isNotEmpty()) {
                    for (d in data){
                        items += NotificationDC(d.action, d.date)
                    }
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    items.sortByDescending { item ->
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(item.date)
                        calendar }
                    val adapter = NotificationAdapter(items)
                    loading.visibility = View.INVISIBLE
                    recycler.adapter = adapter
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