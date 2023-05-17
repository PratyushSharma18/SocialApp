package com.pratyushvkp.socialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.pratyushvkp.socialapp.daos.PostDao

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao : PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postDao = PostDao()
        val postButton: Button = findViewById(R.id.postButton)
        val postInput: EditText = findViewById(R.id.postInput)
        postButton.setOnClickListener {
            val input = postInput.text.toString().trim()
            if(input.isNotEmpty()){
                postDao.addPost(input)
                finish()
            }
        }
    }
}