package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.example.digimess.databinding.FragmentSickCaseBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SickCaseFragment : Fragment() {

    private lateinit var binding: FragmentSickCaseBinding
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentSickCaseBinding.inflate(inflater, container, false)

        val username : String = arguments?.getString("username").toString()
        val conBtn : Button = binding.root.findViewById(R.id.sickCaseConBtn)
        val loading : ProgressBar = binding.root.findViewById(R.id.progressBar)
        val explain : EditText = binding.root.findViewById(R.id.explainCase)
        val choose : AutoCompleteTextView = binding.root.findViewById(R.id.yourSickCase)

        val conditions = listOf("Diarrhea", "Typhoid", "Cholera", "Fever", "Malaria", "Others")
        val conditionsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items_list, conditions)
        choose.setAdapter(conditionsAdapter)
        choose.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.adapter.getItem(position).toString()
            if (selectedItem == "Others"){
                explain.visibility = View.VISIBLE
            }
            else{
                explain.visibility = View.INVISIBLE
            }
        }

        conBtn.setOnClickListener {
            val case = choose.text.toString()
            if (case.isEmpty()){
                CustomToast.show(requireContext(), CustomToast.CROSS, "Choose Your Case")
            }
            else if (case.isNotEmpty() && case != "Others") {
                // Do whatever with case
                lifecycleScope.launch {
                    try {
                        wait(loading, choose, explain, conBtn)
                        val entry = forHistory(
                            username,
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString(),
                            "Sick Case Registered"
                        )
                        val client = SupaDB.getClient()
                        client.postgrest["History"].insert(entry)
                        start(loading, choose, explain, conBtn)
                        CustomToast.show(requireContext(), CustomToast.TICK, "OK")
                    }
                    catch (e: Exception) {
                        start(loading, choose, explain, conBtn)
                        CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
            else if (case.isNotEmpty() && case == "Others"){
                val explanation : String = explain.text.toString()
                if (explanation.isEmpty()){
                    CustomToast.show(requireContext(), CustomToast.CROSS, "Write Your Case")
                }
                else{
                    lifecycleScope.launch {
                        try {
                            // Do whatever with case + explanation
                            wait(loading, choose, explain, conBtn)
                            val entry = forHistory(
                                username,
                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString(),
                                "Sick Case Registered"
                            )
                            val client = SupaDB.getClient()
                            client.postgrest["History"].insert(entry)
                            start(loading, choose, explain, conBtn)
                            CustomToast.show(requireContext(), CustomToast.TICK, "OK")
                        }
                        catch (e: Exception) {
                            start(loading, choose, explain, conBtn)
                            CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
        return binding.root
    }
    private fun wait(loading : ProgressBar, case : AutoCompleteTextView, explain : EditText, next : Button) {
        loading.visibility = View.VISIBLE
        case.isEnabled = false
        explain.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading : ProgressBar, case : AutoCompleteTextView, explain : EditText, next : Button) {
        loading.visibility = View.INVISIBLE
        case.isEnabled = true
        explain.isEnabled = true
        next.isEnabled = true
    }
}