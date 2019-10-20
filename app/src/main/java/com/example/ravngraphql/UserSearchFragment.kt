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
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import android.widget.TextView




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
    private lateinit var mAdapter: UsersAdapter

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
        mAdapter = UsersAdapter({ userItem : User -> onItemUserClick(userItem) })
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


        /*
        val btnFrag2 = viewOfLayout.findViewById<Button>(R.id.btnFrag2) // to open Fragment details
        btnFrag2.setOnClickListener {
            onItemUserClick()
        }
        */

        getUsers(" ") //Show first 20 demonstrative
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
                    //emptyview
                    var emptyView = viewOfLayout.findViewById(R.id.emptyView) as TextView
                    var RecycleUsers = viewOfLayout.findViewById(R.id.lrvUsers) as RecyclerView

                    if (listUsers.isEmpty()) {
                        RecycleUsers.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        RecycleUsers.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                    mAdapter.notifyDataSetChanged()

                }

            }

            override fun onFailure(e: ApolloException) {
                e.printStackTrace();
                Log.d("TAG",e.message.toString())
            }


        })
    }

    fun onItemUserClick(user: User){
        val newReposFragment = UserRepositoriesFragment()
        val arguments = Bundle()
        arguments.putString("user_login", user.login)
        newReposFragment.arguments =  arguments
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(
            R.id.frameContainer,
            newReposFragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
