package com.example.digimess

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        val username : String = getIntent().getStringExtra("username").toString()
        val name : String = getIntent().getStringExtra("name").toString()
        replaceFragment(HomeFragment(), username, name)
        val bnv : BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bnv.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(HomeFragment(), username, name)
                R.id.profile -> replaceFragment(NotificationsFragment(), username, name)
                R.id.settings -> {
                    showDialog(username, name)
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment, username : String, name : String){
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("name", name)
        fragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showDialog(username: String, name: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)
        val security = dialog.findViewById<LinearLayout>(R.id.securityInBSL)
        val policy = dialog.findViewById<LinearLayout>(R.id.messPolicyInBSL)
        val log_out = dialog.findViewById<LinearLayout>(R.id.logOutInBSL)
        security.setOnClickListener {
            replaceFragment(PasswordFragment(), username, name)
            dialog.dismiss()
        }
        policy.setOnClickListener {
            dialog.dismiss()
        }
        log_out.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
}