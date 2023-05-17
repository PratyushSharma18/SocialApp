package com.pratyushvkp.socialapp

import android.app.appsearch.GlobalSearchSession
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pratyushvkp.socialapp.daos.UserDao
import com.pratyushvkp.socialapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int =123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        auth = Firebase.auth

        val signInButton : (SignInButton) = findViewById(R.id.signInButton)
        signInButton.setOnClickListener{
            signIn()
        }
    }

    override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult (requestCode, resultCode, data)
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
         val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        Log.d("task","$task  $data")
       handleSignInResult(task)
    }
}

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult (ApiException::class.java)!!
            Log.d("AccountId", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.e("AccountId","Google sign in failed",e)
            // ...
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        val signInButton : (SignInButton) = findViewById(R.id.signInButton)
        val progressBar : (ProgressBar) = findViewById(R.id.progressBar)
         signInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.IO){
                updateUI(firebaseUser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
if(firebaseUser != null){
    Log.d("firebaseuser","$firebaseUser")
    val user =
        firebaseUser.photoUrl?.let { User(firebaseUser.uid, firebaseUser.displayName as String, it.toString()) }
    val userDao = UserDao()
    userDao.addUser(user)
    val mainActivityIntent = Intent(this,MainActivity::class.java)
    startActivity(mainActivityIntent)
    finish()
}
        else{
    Log.d("firebaseuserttt","$firebaseUser")
    val signInButton : (SignInButton) = findViewById(R.id.signInButton)
    val progressBar : (ProgressBar) = findViewById(R.id.progressBar)
    signInButton.visibility = View.VISIBLE
    progressBar.visibility = View.GONE
        }
    }

}
