package com.example.digimess

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView

class HomeFragment : Fragment() {
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view : View = inflater.inflate(R.layout.fragment_home, container, false)

        val username : String = arguments?.getString("username").toString()
        val name : String = arguments?.getString("name").toString()
        val usernameView : TextView = view.findViewById(R.id.usernameText)
        usernameView.setText(username)
        val nameView : TextView = view.findViewById(R.id.nameText)
        nameView.setText(name)

        val messLeave : CardView? = view.findViewById(R.id.messLeaveBtn)
        messLeave?.setOnClickListener { replaceFragment(MessLeaveFragment(), username) }
        val sickCase : CardView? = view.findViewById(R.id.sickCaseBtn)
        sickCase?.setOnClickListener { replaceFragment(SickCaseFragment(), username) }
        val billEstimate : CardView? = view.findViewById(R.id.billEstimateBtn)
        billEstimate?.setOnClickListener { replaceFragment(BillEstmFragment(), username) }
        val messComp : CardView? = view.findViewById(R.id.messCompBtn)
        messComp?.setOnClickListener { replaceFragment(MessCompFragment(), username) }
        val attendance : CardView? = view.findViewById(R.id.attendanceBtn)
        attendance?.setOnClickListener { replaceFragment(AttendanceFragment(), username) }
        val messMenu : CardView? = view.findViewById(R.id.messMenuBtn)
        messMenu?.setOnClickListener { replaceFragment(MenuFragment(), username) }

        return view
    }
    private fun replaceFragment(fragment: Fragment, username: String) {
        val bundle = Bundle()
        bundle.putString("username", username)
        fragment.arguments = bundle
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}