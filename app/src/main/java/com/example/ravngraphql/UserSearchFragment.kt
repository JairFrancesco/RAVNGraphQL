package com.example.ravngraphql

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ravn.UserQuery
import kotlinx.android.synthetic.main.fragment_user_search.*
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.internal.notify
import java.util.logging.Logger

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserSearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewOfLayout: View
    private var listUsers:MutableList<User> = ArrayList()
    private var mAdapter:UsersAdapter = UsersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_user_search, container, false)
        mAdapter = UsersAdapter()
        mAdapter.UsersAdapter(listUsers, activity!!.applicationContext)

        //recyclerview
        var RecycleUsers = viewOfLayout.findViewById(R.id.lrvUsers) as RecyclerView
        RecycleUsers.layoutManager = LinearLayoutManager(this.context)
        RecycleUsers.adapter = mAdapter
        RecycleUsers.addItemDecoration(
            DividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL)
        )

        val btnSearch = viewOfLayout.findViewById<ImageButton>(R.id.btnSearch) // to trigger search
        btnSearch.setOnClickListener {
            val editTextUser = viewOfLayout.findViewById<EditText>(R.id.editTextUser)
            val querySearch = editTextUser.text.toString()
            getUsers(querySearch)
        }

        //getUsers(" ") //Show first 20 demonstrative
        return viewOfLayout
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserSearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun getUsers(querySearch:String){

        myApolloClient.myApolloClient.query(
            UserQuery.builder()
                .query(querySearch)
                .build()
        ).enqueue(object : ApolloCall.Callback<UserQuery.Data>() {

            override fun onResponse(dataResponse: Response<UserQuery.Data>) {

                listUsers.clear() //clear list
                val users = dataResponse.data()?.search()?.edges()

                users!!.forEach {
                    var item = it.node()

                    if (item is UserQuery.AsUser) { //item is automatically cast to User
                        listUsers.add(User(item.name().toString(), item.avatarUrl(), item.location().toString(), item.login().toString()))
                    }
                }

                // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                activity?.runOnUiThread {
                    mAdapter.notifyDataSetChanged()

                }

            }

            override fun onFailure(e: ApolloException) {
                e.printStackTrace();
                Log.d("TAG",e.message.toString())
            }


        })
    }
    
}
