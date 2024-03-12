package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessCompFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view = inflater.inflate(R.layout.fragment_mess_comp, container, false)

        val username : String = arguments?.getString("username").toString()
        val input : EditText = view.findViewById(R.id.messCompText)
        val btn : Button = view.findViewById(R.id.messCompConBtn)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        btn.setOnClickListener {
            val text: String = input.text.toString()
            wait(loading, input, btn)
            lifecycleScope.launch {
                try {
                    // Do whatever with text
                    val entry = forHistory(
                        username,
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString(),
                        "Registered a mess complaint"
                    )
                    val client = SupaDB.getClient()
                    client.postgrest["History"].insert(entry)
                    start(loading, input, btn)
                    CustomToast.show(requireContext(), CustomToast.TICK, "OK")
                    requireActivity().supportFragmentManager.popBackStack()
                }
                catch (e: Exception) {
                    start(loading, input, btn)
                    CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        return view
    }
    private fun wait(loading: ProgressBar, input: EditText, next: Button) {
        loading.visibility = View.VISIBLE
        input.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading: ProgressBar, input: EditText, next: Button) {
        loading.visibility = View.INVISIBLE
        input.isEnabled = true
        next.isEnabled = true
    }
}