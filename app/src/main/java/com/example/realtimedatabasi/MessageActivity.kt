package com.example.realtimedatabasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realtimedatabasi.adapter.MessageAdapter
import com.example.realtimedatabasi.databinding.ActivityMessageBinding
import com.example.realtimedatabasi.models.Message
import com.example.realtimedatabasi.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.ArrayList

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    private lateinit var currentUserUid:String
    private lateinit var user:User

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserUid=intent.getStringExtra("id")?:""
        user=intent.getSerializableExtra("user") as User

        firebaseDatabase= FirebaseDatabase.getInstance()
        reference=firebaseDatabase.getReference("users")

        messageList=ArrayList()
        messageAdapter= MessageAdapter(messageList,currentUserUid)
        binding.messageRv.adapter=messageAdapter

        binding.sendBtn.setOnClickListener {
            val text=binding.edit.text.toString()
            val message=Message(text,user.uid,currentUserUid,getDate())

            val key = reference.push().key
            reference.child(user.uid?:"").child("messages").child(currentUserUid)
                .child(key?:"").setValue(message)

            reference.child(currentUserUid).child("messages").child(user.uid?:"")
                .child(key?:"").setValue(message)

            binding.edit.setText("")

        }
        reference.child(currentUserUid).child("messages").child(user.uid?:"")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    val children = snapshot.children
                    children.forEach {
                        val value = it.getValue(Message::class.java)
                        if (value != null) {
                            messageList.add(value)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }


                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun getDate():String{
        val date=Date()
        val simpleDateFormat=SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
       return simpleDateFormat.format(date)
    }
}