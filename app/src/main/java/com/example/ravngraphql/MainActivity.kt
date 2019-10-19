package com.example.ravngraphql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ravn.UserQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userFragment = UserSearchFragment()
        //getSupportFragmentManager().beginTransaction().add(R.id.frameContainer, userFragment);
        getUsers()
    }

    fun getUsers(){

        myApolloClient.myApolloClient.query(
            UserQuery.builder()
                .query("jairfrancesco")
                .build()
        ).enqueue(object : ApolloCall.Callback<UserQuery.Data>() {

            override fun onResponse(dataResponse: Response<UserQuery.Data>) {

                val buffer = StringBuffer()
                val users = dataResponse.data()?.search()?.edges()

                users!!.forEach {
                    buffer.append("name:" + it.node()?.toString())
                    buffer.append("\n~~~~~~~~~~~")
                    buffer.append("\n\n")
                }


                // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                this@MainActivity.runOnUiThread {
                    val txtResponse = findViewById<TextView>(R.id.txtTest)
                    txtResponse.text = buffer.toString()
                }

            }

            override fun onFailure(e: ApolloException) {
                e.printStackTrace();
                Log.d("TAG",e.message.toString())
            }


        })
    }
}
