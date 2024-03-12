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
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch

class PasswordFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view : View =  inflater.inflate(R.layout.fragment_password, container, false)

        val username : String = arguments?.getString("username").toString()
        val old_pass : EditText = view.findViewById(R.id.oldPassword)
        val create_pass : EditText = view.findViewById(R.id.createPassword)
        val confirm_pass : EditText = view.findViewById(R.id.confirmPassword)
        val confirmBtn : Button = view.findViewById(R.id.nextButton)
        val loading : ProgressBar = view.findViewById(R.id.progressBar)
        confirmBtn.setOnClickListener {
            val old : String = old_pass.text.toString()
            val create : String = create_pass.text.toString()
            val confirm : String = confirm_pass.text.toString()
            if (create.isEmpty() || confirm.isEmpty() || old.isEmpty()) {
                CustomToast.show(requireContext(), CustomToast.CROSS, "Password required!")
            }
            else if (!create.equals(confirm)) {
                CustomToast.show(requireContext(), CustomToast.CROSS, "Passwords do not match!")
            }
            else {
                wait(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                lifecycleScope.launch {
                    try {
                        val client = SupaDB.getClient()
                        val response = client.postgrest["Users"].select {
                            filter("username", FilterOperator.EQ, username)
                        }
                        val data = response.decodeSingle<forUsers>()
                        if ((data.password) != (old)){
                            start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                            CustomToast.show(requireContext(), CustomToast.CROSS, "Incorrect Old Password!")
                        }
                        else{
                            if (create.length >= 8) {
                                // Characters Only
                                if (Regex("^[a-zA-Z]*$").matches(create)) {
                                    start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                    CustomToast.show(requireContext(), CustomToast.CROSS, "Password must not contain only characters!")
                                }
                                // Numbers Only
                                else if (Regex("^[0-9]*$").matches(create)) {
                                    start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                    CustomToast.show(requireContext(), CustomToast.CROSS, "Password must not contain only numbers!")
                                }
                                // Specials Only
                                else if (Regex("^[^a-zA-Z0-9]*$").matches(create)) {
                                    start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                    CustomToast.show(requireContext(), CustomToast.CROSS, "Password must not contain only special characters!")
                                }
                                else {
                                    if (CheckInternet.isOnline(requireContext())) {
                                        client.postgrest["Users"].update(
                                            {
                                                set("password", confirm)
                                            }
                                        ) {
                                            filter("username", FilterOperator.EQ, username)
                                        }
                                        start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                        CustomToast.show(requireContext(), CustomToast.TICK, "Your password has been updated!")
                                    }
                                    else {
                                        start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                        CustomToast.show(requireContext(), CustomToast.CROSS, "Check Your Internet Connection")
                                    }
                                }
                            }
                            else {
                                start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                                CustomToast.show(requireContext(), CustomToast.CROSS, "Password must contain at least 8 characters!")
                            }
                        }
                    }
                    catch (e: Exception) {
                        start(loading, old_pass, create_pass, confirm_pass, confirmBtn)
                        CustomToast.show(requireContext(), CustomToast.WIFIOFF, "Check Your Internet Connection and Try Again")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }
        return view
    }
    private fun wait(loading: ProgressBar, old: EditText, create: EditText, confirm: EditText, next: Button) {
        loading.visibility = View.VISIBLE
        old.isEnabled = false
        create.isEnabled = false
        confirm.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading: ProgressBar, old: EditText, create: EditText, confirm: EditText, next: Button) {
        loading.visibility = View.INVISIBLE
        old.isEnabled = true
        create.isEnabled = true
        confirm.isEnabled = true
        next.isEnabled = true
    }
}