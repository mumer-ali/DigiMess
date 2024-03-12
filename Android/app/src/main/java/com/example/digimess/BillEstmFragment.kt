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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BillEstmFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view = inflater.inflate(R.layout.fragment_bill_estm, container, false)

        val username: String = arguments?.getString("username").toString()
        val billLbl: TextView = view.findViewById(R.id.billEstimateLabel)
        val bill: TextView = view.findViewById(R.id.billLabel)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        billLbl.setText("Your mess bill for the month of ${getCurrentMonth()} estimates to be")
        loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val client = SupaDB.getClient()
                val response1 = client.postgrest["Leave"].select {
                    filter("username", FilterOperator.EQ, username)
                    filter("leaving", FilterOperator.LIKE, "%-${getCurrentMonthAndYear()}")
                }
                val data1 = response1.decodeList<forLeave>()
                val response2 = client.postgrest["Bill"].select()
                val perDay = response2.decodeSingle<forBill>().per_day
                var daysOff : Int = 0
                if (data1.isEmpty()){
                    loading.visibility = View.INVISIBLE
                    bill.setText("RS.${perDay * getDaysInCurrentMonth()}")
                }
                else{
                    for (d in data1){
                        daysOff += (getDateDifference(d.leaving, d.joining))
                    }
                    loading.visibility = View.INVISIBLE
                    bill.setText("RS.${perDay * (getDaysInCurrentMonth() - daysOff)}")
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
    private fun getCurrentMonth(): String {
        val currentDate = Date()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(currentDate)
    }
    private fun getCurrentMonthAndYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-yyyy")
        return dateFormat.format(calendar.time)
    }
    private fun getDaysInCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    private fun getDateDifference(startDateStr: String, endDateStr: String): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.time = dateFormat.parse(startDateStr) ?: Date()
        endDate.time = dateFormat.parse(endDateStr) ?: Date()
        // Calculate the difference in days
        val differenceMillis = endDate.timeInMillis - startDate.timeInMillis
        return (differenceMillis / (24 * 60 * 60 * 1000)).toInt()
    }
}