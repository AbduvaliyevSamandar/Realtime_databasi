package com.example.realtimedatabasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.realtimedatabasi.databinding.ActivityMainBinding
import com.example.realtimedatabasi.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var auth: FirebaseAuth

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.button.setOnClickListener {

            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent, 1)
        }

        binding.signOutBtn.setOnClickListener {
            mGoogleSignInClient.signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {

        try {
            val account: GoogleSignInAccount? = task?.getResult(ApiException::class.java)
            val user = User(
                account?.displayName,
                account?.id,
                account?.email,
                account?.photoUrl.toString(),
            )
            reference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var isHase = false
                        val children = snapshot.children
                        children.forEach {
                            val value = it.getValue(User::class.java)
                            if (value != null && value.uid == user.uid) {
                                isHase = true
                            }

                        }
                        if (isHase) {
                            val intent = Intent(this@MainActivity, UsersActivity::class.java)
                            intent.putExtra("id", user.uid)
                            startActivity(intent)
                        } else {
                            setNewUser(user)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        } catch (e: ApiException) {

            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun setNewUser(user: User) {
        reference.child(user.uid ?: "").setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this@MainActivity, UsersActivity::class.java)
                intent.putExtra("id", user.uid)
                startActivity(intent)
            }
    }
}