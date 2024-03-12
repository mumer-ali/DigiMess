package com.example.digimess

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email : EditText = findViewById(R.id.emailInput)
        val nextButton : Button = findViewById(R.id.nextButton1)
        val loading: ProgressBar = findViewById(R.id.progressBar)

        nextButton.setOnClickListener {
            val emailString : String = email.text.toString()
            if (CheckInternet.isOnline(this) && emailString.isNotEmpty()){
                wait(loading, email, nextButton)
                lifecycleScope.launch {
                    try {
                        val client = SupaDB.getClient()
                        val response = client.postgrest["Users"].select {
                            filter("email", FilterOperator.EQ, emailString)
                        }
                        val data = response.decodeList<forUsers>()
                        if (data.isNotEmpty()){
                            start(loading, email, nextButton)
                            CustomToast.show(this@Register, CustomToast.CROSS, "Account already exists!")
                        }
                        else{
                            val response = client.postgrest["Hostelites"].select {
                            filter("email", FilterOperator.EQ, emailString)
                            }
                            val data = response.decodeList<forHostelites>()
                            if (data.isEmpty()){
                                start(loading, email, nextButton)
                                CustomToast.show(this@Register, CustomToast.CROSS, "Record not found")
                            }
                            else{
                                val data = response.decodeSingle<forHostelites>()
                                val otp : Int = (100001..999998).random()
                                SendEmail.send_email(emailString, otp)
                                start(loading, email, nextButton)
                                val intent = Intent(this@Register, Register_2::class.java)
                                intent.putExtra("email", emailString)
                                intent.putExtra("first_name", data.first_name)
                                intent.putExtra("otp", otp.toString())
                                startActivity(intent)
                            }
                        }
                    }
                    catch (e: Exception) {
                        start(loading, email, nextButton)
                        CustomToast.show(this@Register, CustomToast.WIFIOFF, "Check Your Internet Connection")
                    }
                }
            }
            else{
                if (emailString.isEmpty()){
                    CustomToast.show(this, CustomToast.CROSS, "Insert your NUST registered email address")
                }
                else if(!CheckInternet.isOnline(this)){
                    CustomToast.show(this, CustomToast.WIFIOFF, "Check Your Internet Connection")
                }
            }
        }
    }
    private fun wait(loading: ProgressBar, email: EditText, next: Button) {
        loading.visibility = View.VISIBLE
        email.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading: ProgressBar, email: EditText, next: Button) {
        loading.visibility = View.INVISIBLE
        email.isEnabled = true
        next.isEnabled = true
    }
}
