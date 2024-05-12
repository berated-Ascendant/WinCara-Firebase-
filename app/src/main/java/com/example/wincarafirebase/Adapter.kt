package com.example.wincarafirebase

import android.app.AlertDialog
import android.content.Context
import android.media.AudioMetadata.Key
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Adapter( private val c: Context, private val userList: ArrayList<UserData>): RecyclerView.Adapter<Adapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return MyViewHolder(itemView, userList , c)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = userList[position]
        holder.username.text = currentItem.username
        holder.password.text = currentItem.password
    }
    class MyViewHolder(itemView: View, val userList: ArrayList<UserData>, val c: Context):RecyclerView.ViewHolder(itemView){

        private lateinit var firebaseDB: FirebaseDatabase
        private lateinit var databaseReference: DatabaseReference


        var username: TextView
        var password: TextView
        var menu: ImageView

        init {

            username = itemView.findViewById(R.id.username_display)
            password = itemView.findViewById(R.id.password_display)
            menu = itemView.findViewById(R.id.display_menu)
            menu.setOnClickListener{popupMenus(itemView)}
        }

        private fun popupMenus(itemView: View?) {
            val position = userList[adapterPosition]
            val userId = position.id


            val popupMenus = PopupMenu(c, itemView)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editText -> {
                        val v = LayoutInflater.from(c).inflate(R.layout.update_user, null)
                        val newusername = v.findViewById<EditText>(R.id.update_userName)
                        val newpassword = v.findViewById<EditText>(R.id.update_password)
                        newusername.setText(position.username)
                        newpassword.setText(position.password)

                        AlertDialog.Builder(c)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                val updatedUsername = newusername.text.toString()
                                val updatedPassword = newpassword.text.toString()

                                // Get reference to the specific user node in the database
                                val userRef = FirebaseDatabase.getInstance().getReference("users")

                                // Update user information in the database
                                userRef.child("username").setValue(updatedUsername)
                                userRef.child("password").setValue(updatedPassword)
                                    .addOnSuccessListener {
                                        Toast.makeText(c, "User information updated successfully", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(c, "Failed to update user information", Toast.LENGTH_SHORT).show()
                                    }
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.delete -> {
                        val userRef = FirebaseDatabase.getInstance().getReference("users")
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.warning_black_24dp)
                            .setMessage("Are you sure you want to delete this user?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                userRef.removeValue().addOnSuccessListener {
                                    Toast.makeText(c, "User deleted successfully", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener {
                                    Toast.makeText(c, "Failed to delete user", Toast.LENGTH_SHORT).show()
                                }
                                userList.removeAt(adapterPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else -> false
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }

    }

}

