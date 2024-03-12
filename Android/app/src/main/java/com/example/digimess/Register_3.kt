package com.example.digimess

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.launch

class Register_3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        val myEmail : String = getIntent().getStringExtra("myEmail").toString()
        val f_name : String = getIntent().getStringExtra("my_first_name").toString()

        val greetings : TextView = findViewById(R.id.nameLabel)
        greetings.setText("Welcome Onboard, $f_name!")
        val next_button : Button = findViewById(R.id.nextButton)
        val create_pass : EditText = findViewById(R.id.createPassword)
        val confirm_pass : EditText = findViewById(R.id.confirmPassword)
        val loading: ProgressBar = findViewById(R.id.progressBar)

        next_button.setOnClickListener {
            val create : String = create_pass.text.toString()
            val confirm : String = confirm_pass.text.toString()

            if (create.isEmpty() || confirm.isEmpty()) {
                CustomToast.show(this, CustomToast.CROSS, "Password required!")
            }
            else if (!create.equals(confirm)) {
                CustomToast.show(this, CustomToast.CROSS, "Passwords do not match!")
            }
            else {
                if (create.length >= 8) {
                    // Characters Only
                    if (Regex("^[a-zA-Z]*$").matches(create)) {
                        CustomToast.show(this, CustomToast.CROSS, "Password must not contain only characters!")
                    }
                    // Numbers Only
                    else if (Regex("^[0-9]*$").matches(create)) {
                        CustomToast.show(this, CustomToast.CROSS, "Password must not contain only numbers!")
                    }
                    // Specials Only
                    else if (Regex("^[^a-zA-Z0-9]*$").matches(create)) {
                        CustomToast.show(this, CustomToast.CROSS, "Password must not contain only special characters!")
                    }
                    else {
                        if (CheckInternet.isOnline(this)) {
                            wait(loading, create_pass, confirm_pass, next_button)
                            val username : String = extractUsername(myEmail).toString()
                            val user = forUsers(username, create, myEmail)
                            lifecycleScope.launch {
                                try {
                                    val client = SupaDB.getClient()
                                    client.postgrest["Users"].insert(user)
                                    val response = client.postgrest["Hostelites"].select {
                                        filter("email", FilterOperator.EQ, myEmail)
                                    }
                                    val data = response.decodeSingle<forHostelites>()
                                    start(loading, create_pass, confirm_pass, next_button)
                                    val intent = Intent(this@Register_3, Screen::class.java)
                                    intent.putExtra("username", username)
                                    intent.putExtra("name", "${data.first_name} ${data.last_name}")
                                    startActivity(intent)
                                    finish()
                                }
                                catch (e: Exception) {
                                    start(loading, create_pass, confirm_pass, next_button)
                                    CustomToast.show(this@Register_3, CustomToast.WIFIOFF, "Check Your Internet Connection")
                                }
                            }
                        }
                        else {
                            CustomToast.show(this, CustomToast.WIFIOFF, "Check Your Internet Connection")
                        }
                    }
                }
                else {
                    CustomToast.show(this, CustomToast.CROSS, "Password must contain at least 8 characters!")
                }
            }
        }
    }
    fun extractUsername(email: String): String? {
        val regex = Regex("^[^@]+")
        val match = regex.find(email)
        return match?.value
    }
    private fun wait(loading: ProgressBar, create: EditText, confirm: EditText, next: Button) {
        loading.visibility = View.VISIBLE
        create.isEnabled = false
        confirm.isEnabled = false
        next.isEnabled = false
    }
    private fun start(loading: ProgressBar, create: EditText, confirm: EditText, next: Button) {
        loading.visibility = View.INVISIBLE
        create.isEnabled = true
        confirm.isEnabled = true
        next.isEnabled = true
    }
}