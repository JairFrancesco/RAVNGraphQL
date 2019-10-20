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
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import android.widget.TextView
import android.text.Editable
import android.text.TextWatcher
import java.util.Timer
import java.util.TimerTask
import android.os.Handler
import com.apollographql.apollo.ravn.UserNextQuery

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserSearchFragment : Fragment(), InfiniteScrollListener.OnLoadMoreListener, InfiniteScrollListener.isLoadingListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewOfLayout: View
    private var listUsers:MutableList<User?> = ArrayList()
    private lateinit var mAdapter: UsersAdapter
    var infiniteScrollListener: InfiniteScrollListener? = null
    private var afterCursor:String? = null //Necessary for loading more (on scroll)
    private var isLoadingRecycler = false

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

        val manager = LinearLayoutManager(this.context)
        var infiniteScrollListener = InfiniteScrollListener(manager, this, this)

        RecycleUsers.layoutManager = manager
        RecycleUsers.addOnScrollListener(infiniteScrollListener)
        RecycleUsers.adapter = mAdapter
        RecycleUsers.addItemDecoration(
            DividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL)
        )

        val valEditSearch = viewOfLayout.findViewById<EditText>(R.id.editTextUser)
        valEditSearch.addTextChangedListener(
            object : TextWatcher {

                private var timer = Timer()
                private val DELAY: Long = 1000 // milliseconds
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun afterTextChanged(s: Editable) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                //Refresh the list
                                val editTextUser = viewOfLayout.findViewById<EditText>(R.id.editTextUser)
                                val querySearch = editTextUser.text.toString()
                                getUsers(querySearch)
                            }
                        },
                        DELAY
                    )
                }
            }
        )
        getUsers(" ") //Show no results
        return viewOfLayout
    }

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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
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
                afterCursor = dataResponse.data()?.search()?.pageInfo()?.endCursor().toString()
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
                    mAdapter.notifyDataSetChanged() //Notify change
                }
            }

            override fun onFailure(e: ApolloException) {
                //e.printStackTrace();
                Log.d("TAG ERROR HTTP",e.message.toString())
            }


        })
    }

    fun onItemUserClick(user: User){
        val newReposFragment = UserRepositoriesFragment()
        val arguments = Bundle()
        arguments.putString("user_login", user?.login)
        newReposFragment.arguments =  arguments
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(
            R.id.frameContainer,
            newReposFragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun isLoading(): Boolean {
        return isLoadingRecycler
    }

    override fun onLoadMore() {
        var RecycleUsers = viewOfLayout.findViewById(R.id.lrvUsers) as RecyclerView
        RecycleUsers.post{
            listUsers.add(null)
            mAdapter.notifyItemInserted(listUsers.size - 1)
        }

        val editTextUser = viewOfLayout.findViewById<EditText>(R.id.editTextUser)
        val querySearch = editTextUser.text.toString()
        Handler().postDelayed({
            mAdapter.removeNull()
            // Get next 10 users
            myApolloClient.myApolloClient.query(
                UserNextQuery.builder()
                    .query(querySearch)
                    .afterCursor(afterCursor.toString())
                    .build()
            ).enqueue(object : ApolloCall.Callback<UserNextQuery.Data>() {
                override fun onResponse(dataResponse: Response<UserNextQuery.Data>) {
                    val users = dataResponse.data()?.search()?.edges()
                    afterCursor = dataResponse.data()?.search()?.pageInfo()?.endCursor().toString()
                    users!!.forEach {
                        var item = it.node()

                        if (item is UserNextQuery.AsUser) { //item is automatically cast to User
                            listUsers.add(User(item.name().toString(), item.avatarUrl(), item.location().toString(), item.login().toString()))
                        }
                    }
                    // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                    activity?.runOnUiThread {
                        mAdapter.notifyDataSetChanged()
                        isLoadingRecycler = false
                    }
                }
                override fun onFailure(e: ApolloException) {
                    //e.printStackTrace();
                    Log.d("TAG",e.message.toString())
                }
            })
        }, 2000)

        isLoadingRecycler = true
    }

}
