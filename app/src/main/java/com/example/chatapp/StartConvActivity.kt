package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class StartConvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_conv)
         val startconv = findViewById<Button>(R.id.btStartConvStertConv)
        val signout = findViewById<Button>(R.id.btSignOut)

        signout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        verifyuserisloggedin()
        startconv.setOnClickListener {
            val intent = Intent(this, AllUsersActivity::class.java)
            startActivity(intent)
        }
    }
    private fun verifyuserisloggedin()
    {
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null)
        {
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}