package com.example.ravngraphql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


data class Repository(
    var name:String,
    var description:String,
    var pullRequestsCount: String
)

data class User(
    var name:String,
    var avatar_url:String,
    var location:String,
    var login:String
)


class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}
