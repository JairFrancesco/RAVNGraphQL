package com.example.ravngraphql

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apollographql.apollo.ravn.RepositoryQuery


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


class MainActivity : AppCompatActivity(), UserSearchFragment.OnFragmentInteractionListener, UserRepositoriesFragment.OnFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userFragment = UserSearchFragment()
        getSupportFragmentManager().beginTransaction().add(R.id.frameContainer, userFragment);

        val transition = supportFragmentManager.beginTransaction()
        transition.replace(
            R.id.frameContainer,
            userFragment
        )
        transition.commit()

    }

    override fun onFragmentInteraction(uri: Uri) {}
}
