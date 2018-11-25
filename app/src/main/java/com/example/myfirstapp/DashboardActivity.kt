package com.example.myfirstapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log.d
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_popup.view.*

class DashboardActivity : AppCompatActivity() {

    fun popUp() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.dashboard_popup, null)

        val popUpWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        // Set an elevation for the popup window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popUpWindow.elevation = 10.0F
        }

        // If API level 23 or higher then execute the code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popUpWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popUpWindow.exitTransition = slideOut
        }

        // widget references
        val btnClose = view.findViewById<Button>(R.id.btnPopClose)
//        val btnClose = dashboard_root.btnPopClose


        btnClose.setOnClickListener{
            // Dismiss the popup window
            popUpWindow.dismiss()
        }

        // Set a dismiss listener for popup window
        popUpWindow.setOnDismissListener {
            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(dashboard_root)

        d("shcs", "bef")

        Handler().postDelayed({
            run {
                popUpWindow.showAtLocation(
                        dashboard_root, // Location to display popup window
                        Gravity.TOP, // Exact position of layout to display popup
                        0, // X offset
                        0 // Y offset
                )
                d("shcs", "runrun")
            }

        }, 100)
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        popUp()

    }


}
