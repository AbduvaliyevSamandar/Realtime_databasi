package com.example.realtimedatabasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.realtimedatabasi.adapter.UsersAdapter
import com.example.realtimedatabasi.databinding.ActivityUsersBinding
import com.example.realtimedatabasi.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUsersBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var usersAdapter: UsersAdapter
    private lateinit var list:ArrayList<User>

    private lateinit var auth:FirebaseAuth

    private lateinit var uid:String

    private val TAG = "UsersActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid=intent.getStringExtra("id")?:""

        auth= FirebaseAuth.getInstance()

        firebaseDatabase= FirebaseDatabase.getInstance()
        reference=firebaseDatabase.getReference("users")

        list= ArrayList()
        usersAdapter= UsersAdapter(list){
            val intent=Intent(this,MessageActivity::class.java)
            intent.putExtra("user",it)
            intent.putExtra("id",uid)
            startActivity(intent)
        }

        binding.rv.adapter=usersAdapter

        reference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                children.forEach {
                    val value = it.getValue(User::class.java)
                    Log.d(TAG, "onDataChange: $value")
                    if (value != null && uid!=value.uid) {
                        list.add(value)
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}