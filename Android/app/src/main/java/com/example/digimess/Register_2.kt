package com.example.digimess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Register_2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        val email : String = getIntent().getStringExtra("email").toString()
        val name : String = getIntent().getStringExtra("first_name").toString()
        val otp : String = getIntent().getStringExtra("otp").toString()
        val otpSent : Int = otp.toInt()

        val text_email : TextView = findViewById(R.id.emLabel)
        text_email.setText(email)
        val next : Button = findViewById(R.id.nextButtonOtp)
        val otpView : EditText = findViewById(R.id.emailInput)

        next.setOnClickListener {
            val otp_string : String = otpView.text.toString()
            val otp_int : Int = otp_string.toInt()
            if (otpSent == otp_int) {
                val intent = Intent(this, Register_3::class.java)
                intent.putExtra("myEmail", email)
                intent.putExtra("my_first_name", name)
                startActivity(intent)
            }
            else {
                CustomToast.show(this, CustomToast.CROSS, "Incorrect OTP!")
            }
        }
    }
}

