package com.example.ravngraphql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userFragment = UserSearchFragment()
        getSupportFragmentManager().beginTransaction().add(R.id.frameContainer, userFragment);
    }
}
