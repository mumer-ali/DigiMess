package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch
import java.util.Calendar

class MenuFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view : View = inflater.inflate(R.layout.fragment_menu, container, false)

        val breakfast : TextView = view.findViewById(R.id.breakfast)
        val lunch : TextView = view.findViewById(R.id.lunch)
        val dinner : TextView = view.findViewById(R.id.dinner)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val client = SupaDB.getClient()
                val response = client.postgrest["Menu"].select {
                    filter("day", FilterOperator.EQ, getDayOfWeek())
                }
                val data = response.decodeSingle<forMenu>()
                loading.visibility = View.INVISIBLE
                breakfast.setText(data.breakfast)
                lunch.setText(data.lunch)
                dinner.setText(data.dinner)
            }
            catch (e: Exception) {
                loading.visibility = View.INVISIBLE
                CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return view
    }
    private fun getDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Invalid day"
        }
    }
}