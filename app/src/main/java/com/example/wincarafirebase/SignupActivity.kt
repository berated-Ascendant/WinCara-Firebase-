package com.example.wincarafirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wincarafirebase.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseDB = FirebaseDatabase.getInstance()
        databaseReference = firebaseDB.reference.child("users")

        binding.signupbtn.setOnClickListener {

            val signupUsername = binding.signupUsernameinput.editText?.text.toString().trim()
            val signupPassword = binding.signupPasswordinput.editText?.text.toString().trim()

            if( signupUsername.isNotEmpty() && signupPassword.isNotEmpty()){
                signupUser(signupUsername, signupPassword)
            }else{
                Toast.makeText(this@SignupActivity, "Please fill out all the fields", Toast.LENGTH_SHORT).show()

            }
        }

        binding.loginredirect.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }


    }

    private fun signupUser(username: String, password: String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    val id = databaseReference.push().key
                    val userData = UserData(id, username, password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignupActivity, "Sign up Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                }else {
                    Toast.makeText(this@SignupActivity, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignupActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }
}