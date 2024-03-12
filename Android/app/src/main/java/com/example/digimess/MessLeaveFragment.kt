package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.digimess.databinding.FragmentMessLeaveBinding
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessLeaveFragment : Fragment() {
    // joining - leaving ----- leaving is included but joining is not
    private lateinit var binding: FragmentMessLeaveBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentMessLeaveBinding.inflate(inflater, container, false)

        val username : String = arguments?.getString("username").toString()
        val confirm : Button = binding.root.findViewById(R.id.messLeaveConBtn)
        val loading : ProgressBar = binding.root.findViewById(R.id.progressBar)
        val leavingDD : AutoCompleteTextView = binding.root.findViewById(R.id.leaving_dropdown)
        val joiningDD : AutoCompleteTextView = binding.root.findViewById(R.id.joining_dropdown)
        val error : TextView = binding.root.findViewById(R.id.errorLabel)

        wait(loading, leavingDD, joiningDD, confirm)
        lifecycleScope.launch {
            try {
                val client = SupaDB.getClient()
                val response1 = client.postgrest["Leave"].select {
                    filter("username", FilterOperator.EQ, username)
                }
                val data1 = response1.decodeList<forLeave>()
                var leaveCal: Calendar
                var joinCal: Calendar
                var isMessOff : Boolean = false
                for (d in data1) {
                    leaveCal = dateToCalendar(d.leaving)
                    joinCal = dateToCalendar(d.joining)
                    val t1: Int = getCurrentDate().compareTo(leaveCal)
                    val t2: Int = getCurrentDate().compareTo(joinCal)
                    val t3: Int = addDays(getCurrentDate(), 2).compareTo(leaveCal)
                    if ((t1 >= 0 && t2 <= 0) || t3 == 0) {
                        isMessOff = true
                        break
                    }
                }
                val today: String = formatDate(getCurrentDate()).substring(3)
                val response2 = client.postgrest["Leave"].select {
                    filter("username", FilterOperator.EQ, username)
                    filter("leaving", FilterOperator.LIKE, "%%-${today}")
                }
                val data2 = response2.decodeList<forLeave>()
                var total: Int = 0
                for (d in data2) {
                    total += getDateDifference(d.leaving, d.joining)
                }
                if (total < 12 && isMessOff == false) {
                    val leavingDays = mutableListOf((formatDate(addDays(getCurrentDate(), 2)) + " " + getDayOfWeek(addDays(getCurrentDate(), 2))))
                    val leavingAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items_list, leavingDays)
                    leavingDD.setAdapter(leavingAdapter)

                    val joiningDays = mutableListOf<String>()
                    for (i in 1 .. 12 - total) {
                        if (!isNextMonth(addDays(getCurrentDate(), 2+i))) {
                            joiningDays += (formatDate(addDays(getCurrentDate(), 2+i)) + " " + getDayOfWeek(addDays(getCurrentDate(), 2+i)))
                        }
                        else {
                            break
                        }
                    }
                    val joiningAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items_list, joiningDays)
                    joiningDD.setAdapter(joiningAdapter)
                    start(loading, leavingDD, joiningDD, confirm)
                }
                else {
                    start(loading, leavingDD, joiningDD, confirm)
                    error.setText("You cannot request for mess leave at the moment")
                    confirm.isEnabled = false
                    joiningDD.isEnabled = false
                    leavingDD.isEnabled = false
                }
            }
            catch (e: Exception) {
                start(loading, leavingDD, joiningDD, confirm)
                CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        confirm.setOnClickListener {
            val leaving = leavingDD.text.toString()
            val joining = joiningDD.text.toString()
            if (leaving.isEmpty() || joining.isEmpty()){
                CustomToast.show(requireContext(), R.drawable.baseline_close_24, "Select a Date Range")
            }
            else{
                wait(loading, leavingDD, joiningDD, confirm)
                val date_l: Date = SimpleDateFormat("dd-MM-yyyy EEEE", Locale.getDefault()).parse(leaving) ?: Date()
                val date_j: Date = SimpleDateFormat("dd-MM-yyyy EEEE", Locale.getDefault()).parse(joining) ?: Date()
                val formattedLeaving: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date_l)
                val formattedJoining: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date_j)

                val entry_1 = forLeave(username, formattedLeaving, formattedJoining)
                val entry_2 = forHistory(
                    username,
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString(),
                    "Mess Leave from ${leaving} to ${joining}"
                )
                lifecycleScope.launch {
                    try {
                        val client = SupaDB.getClient()
                        client.postgrest["Leave"].insert(entry_1)
                        client.postgrest["History"].insert(entry_2)
                        start(loading, leavingDD, joiningDD, confirm)
                        CustomToast.show(requireContext(), CustomToast.TICK, "OK")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    catch (e: Exception) {
                        start(loading, leavingDD, joiningDD, confirm)
                        CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }
        return binding.root
    }
    private fun formatDate(calendar : Calendar): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        return dateFormat.format(calendar.time)
    }
    private fun getCurrentDate(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }
    private fun addDays(calendar: Calendar, days: Int): Calendar {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar
    }
    private fun getDayOfWeek(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> "Invalid Day"
        }
    }
    private fun dateToCalendar(dateString: String) : Calendar {
        val date: Date = SimpleDateFormat("dd-MM-yyyy").parse(dateString) ?: Date()
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }
    private fun wait(loading : ProgressBar, leave : AutoCompleteTextView, join : AutoCompleteTextView, next : Button) {
        loading.visibility = View.VISIBLE
        leave.isEnabled = false
        join.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading : ProgressBar, leave : AutoCompleteTextView, join : AutoCompleteTextView, next : Button) {
        loading.visibility = View.INVISIBLE
        leave.isEnabled = true
        join.isEnabled = true
        next.isEnabled = true
    }
    private fun getDateDifference(startDateStr: String, endDateStr: String): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.time = dateFormat.parse(startDateStr) ?: Date()
        endDate.time = dateFormat.parse(endDateStr) ?: Date()
        val differenceMillis = endDate.timeInMillis - startDate.timeInMillis
        return (differenceMillis / (24 * 60 * 60 * 1000)).toInt()
    }
    private fun isNextMonth(calendar: Calendar): Boolean {
        val currentCalendar = getCurrentDate()
        return calendar.get(Calendar.YEAR) > currentCalendar.get(Calendar.YEAR) ||
                (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        calendar.get(Calendar.MONTH) > currentCalendar.get(Calendar.MONTH))
    }
}