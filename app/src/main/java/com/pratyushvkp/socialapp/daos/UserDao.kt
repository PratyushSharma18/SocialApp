package com.pratyushvkp.socialapp.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pratyushvkp.socialapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
  private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(user : User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                // This operation would be executing on background thread instead of main thread
                userCollection.document(user.uid).set(it)
            }

        }
    }

    fun getUserById(uId:String):Task<DocumentSnapshot>{
        return userCollection.document(uId).get()
    }
}