package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.parcel.Parcelize
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object{
        val IMGREQUEST=10
        val TAG = "RegisterActivity"
    }
    var selectedphotouri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        //val username = findViewById<EditText>(R.id.etUsernameRegister)



        val selectphoto = findViewById<Button>(R.id.btSelectPhotoRegister)
        selectphoto.setOnClickListener {

            Log.d(TAG,"selecting photo")

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type="image/*"
            startActivityForResult(intent, IMGREQUEST)

        }



        val register = findViewById<Button>(R.id.btRegisterRegister)
        register.setOnClickListener {

            //val email = findViewById<EditText>(R.id.etEmailRegister).text.toString()
            //val password = findViewById<EditText>(R.id.etPasswordRegister).text.toString()

            performRegister()

            //Log.d(TAG, "Email id: $email")
            //Log.d(TAG, "Password : $password")


        }

        val alreadyhaveaccount = findViewById<TextView>(R.id.tvAlreadyHaveAccountRegister)
        alreadyhaveaccount.setOnClickListener {


            //Log.d("MainActivity","move to next")

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMGREQUEST && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.d(TAG,"photo selected")

            val selectedphoto = findViewById<CircleImageView>(R.id.cvSelectPhotoRegister)
            val selectphoto = findViewById<Button>(R.id.btSelectPhotoRegister)

            selectedphotouri = data.data
            selectedphoto.setImageURI(data.data)

            Log.d(TAG,"photo ${data.data}")

            selectphoto.alpha = 0f

            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data)
            //selectedphoto.setImageBitmap(bitmap)
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister()
    {
        val email = findViewById<EditText>(R.id.etEmailRegister).text.toString()
        val password = findViewById<EditText>(R.id.etPasswordRegister).text.toString()
        val imageView = findViewById<CircleImageView>(R.id.cvSelectPhotoRegister)
        if(email.isEmpty() || password.isEmpty() )
        {
            Toast.makeText(this,"enter email and password",Toast.LENGTH_SHORT).show()
            return
        }

        if (imageView.drawable == null)
        {
            Toast.makeText(this,"upload image",Toast.LENGTH_SHORT).show()
            return
        }

        /*if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("MainActivity", "not a valid email")

            Toast.makeText(this, "not a valid email", Toast.LENGTH_SHORT).show()

            return

        }*/

        //if (password.length<=8)
        {
            Toast.makeText(this, "password must have at least 8 character", Toast.LENGTH_SHORT).show()
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful)
            {
                Toast.makeText(this,"welcome ${it.result?.user?.uid}",Toast.LENGTH_SHORT).show()

                //val userid = it.result?.user?.uid.toString()
                uploadImageToFirebaseStorage ()


            } else return@addOnCompleteListener

        }

                .addOnFailureListener {
                    Toast.makeText(this,"error: ${it.message}",Toast.LENGTH_SHORT).show()
                }

    }

    private fun uploadImageToFirebaseStorage () {
        val username = findViewById<EditText>(R.id.etUsernameRegister)

        /*if (selectedphotouri == null) {
            Log.d(TAG, "uri is null")
            return
        }*/

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedphotouri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded images: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "file location: $it")

                        val uid = FirebaseAuth.getInstance().uid ?: ""
                        val dbref = FirebaseDatabase.getInstance().getReference("/users/$uid")


                        val user = User(uid, username.text.toString(), it.toString())

                        dbref.setValue(user)
                                .addOnSuccessListener {
                                    Log.d(TAG, "saved the user to database")

                                    val intent = Intent(this,StartConvActivity::class.java)
                                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, "error: ${it.message}")
                                }
                    }

                }

    }
}

@Parcelize
class User(val uid: String,val username: String, val profilephotouri: String) : Parcelable
{
    constructor() : this("","","")
}