package com.example.wincarafirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wincarafirebase.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Thread.sleep

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sleep(3000)
        installSplashScreen()
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseDB = FirebaseDatabase.getInstance()
        databaseReference = firebaseDB.reference.child("users")

        binding.loginbtn.setOnClickListener {
            val loginUsername = binding.loginUsernameinput.editText?.text.toString().trim()
            val loginPassword = binding.loginPasswordinput.editText?.text.toString().trim()

            if(loginUsername.isNotEmpty() && loginPassword.isNotEmpty()){
                loginUser(loginUsername, loginPassword)
            }else{
                Toast.makeText(this@LoginActivity, "Please fill out all the fields", Toast.LENGTH_SHORT).show()

            }
        }

        binding.signinredirect.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }

    }

    private fun loginUser(username: String, password: String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(userSnapshot in dataSnapshot.children){
                        val userData = userSnapshot.getValue(UserData::class.java)

                        if(userData != null && userData.password == password){
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                            return
                        }
                    }
                }
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }
}