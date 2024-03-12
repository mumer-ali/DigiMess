package com.example.digimess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uRegister: EditText = findViewById(R.id.userRegister)
        val pRegister: EditText = findViewById(R.id.PasswordRegister)
        val nextRegister: Button = findViewById(R.id.nextButtonRegister)
        val createAccView: TextView = findViewById(R.id.createAccLabel)
        val loading: ProgressBar = findViewById(R.id.progressBar)

        createAccView.setOnClickListener {
            if (CheckInternet.isOnline(this)) {
                val nextScreen = Intent(this, Register::class.java)
                startActivity(nextScreen)
            } else {
                CustomToast.show(this, CustomToast.WIFIOFF, "Check Your Internet Connection")
            }
        }

        nextRegister.setOnClickListener {
            if (CheckInternet.isOnline(this)) {
                wait(loading, uRegister, pRegister, nextRegister)
                val userString: String = uRegister.text.toString()
                val passString: String = pRegister.text.toString()
                lifecycleScope.launch {
                    try {
                        val client = SupaDB.getClient()
                        val response = client.postgrest["Users"].select {
                            filter("username", FilterOperator.EQ, userString)
                        }
                        val data = response.decodeList<forUsers>()
                        if (data.isEmpty()) {
                            start(loading, uRegister, pRegister, nextRegister)
                            CustomToast.show(
                                this@MainActivity,
                                CustomToast.CROSS,
                                "Record not found"
                            )
                        } else {
                            val data = response.decodeSingle<forUsers>()
                            if (passString == data.password) {
                                val response = client.postgrest["Hostelites"].select {
                                    filter("email", FilterOperator.LIKE, "${userString}@%")
                                }
                                val data = response.decodeSingle<forHostelites>()
                                start(loading, uRegister, pRegister, nextRegister)
                                val intent = Intent(this@MainActivity, Screen::class.java)
                                intent.putExtra("username", userString)
                                intent.putExtra("name", "${data.first_name} ${data.last_name}")
                                startActivity(intent)
                                finish()
                            } else {
                                start(loading, uRegister, pRegister, nextRegister)
                                CustomToast.show(
                                    this@MainActivity,
                                    CustomToast.CROSS,
                                    "Incorrect Username or Password"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        start(loading, uRegister, pRegister, nextRegister)
                        CustomToast.show(
                            this@MainActivity,
                            CustomToast.WIFIOFF,
                            "Check Your Internet Connection"
                        )
                    }
                }
            } else {
                CustomToast.show(this, CustomToast.WIFIOFF, "Check Your Internet Connection")
            }
        }
    }
    private fun wait(loading : ProgressBar, unField : EditText, pwField : EditText, next : Button) {
        loading.visibility = View.VISIBLE
        unField.isEnabled = false
        pwField.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading : ProgressBar, unField : EditText, pwField : EditText, next : Button) {
        loading.visibility = View.INVISIBLE
        unField.isEnabled = true
        pwField.isEnabled = true
        next.isEnabled = true
    }
}
