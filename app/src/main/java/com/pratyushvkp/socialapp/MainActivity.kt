package com.pratyushvkp.socialapp

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.pratyushvkp.socialapp.daos.PostDao
import com.pratyushvkp.socialapp.databinding.ActivityMainBinding
import com.pratyushvkp.socialapp.models.Post

class MainActivity : AppCompatActivity() , IPostAdapter{
    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private lateinit var auth: FirebaseAuth
//    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mainBinding= ActivityMainBinding.inflate(layoutInflater)
//        setContentView(mainBinding.root)
         val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener{
            val intent = Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
            }
        auth = Firebase.auth
        val signOutButton : Button = findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
       postDao = PostDao()
        val postCollections = postDao.postCollection
        val query = postCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions,this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
      adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
      postDao.updateLikes(postId)
    }
}