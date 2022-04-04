package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val login = findViewById<Button>(R.id.btLoginLogin)
        login.setOnClickListener {


            val email = findViewById<EditText>(R.id.etEmailLogin).text.toString()
            val password = findViewById<EditText>(R.id.etPasswordLogin).text.toString()

            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this,"enter email and password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LoginActivity","email is :$email")
            Log.d("LoginActivity","password :$password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Toast.makeText(this,"welcome ${it.result?.user?.uid}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,StartConvActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else return@addOnCompleteListener

            }

                    .addOnFailureListener {
                        Toast.makeText(this,"error:  ${it.message}", Toast.LENGTH_SHORT).show()
                    }


        }

        val backtoregister = findViewById<TextView>(R.id.tvBackToRegisterLogin)
        backtoregister.setOnClickListener {
            finish()
        }
    }
}