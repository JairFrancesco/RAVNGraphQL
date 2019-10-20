package com.example.ravngraphql

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.ravn.RepositoryQuery
import com.apollographql.apollo.ravn.RepositoryNextQuery


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserRepositoriesFragment : Fragment(), InfiniteScrollListener.OnLoadMoreListener, InfiniteScrollListener.isLoadingListener  {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewOfLayout: View
    private var listRepos:MutableList<Repository?> = ArrayList()
    private var mAdapter:RepositoriesAdapter = RepositoriesAdapter()
    private var afterCursor:String? = null //Necessary for loading more (on scroll)
    private var isLoadingRecycler = false
    private var user_login:String? = null
    val args: UserRepositoriesFragmentArgs by navArgs()

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
        viewOfLayout = inflater.inflate(R.layout.fragment_user_repositories, container, false)
        user_login = args.userLogin


        val txtUsername = viewOfLayout.findViewById<TextView>(R.id.txtUsername) // to trigger search
        txtUsername.text = user_login

        val btnBack = viewOfLayout.findViewById<Button>(R.id.btnBack) // to trigger search
        btnBack.setOnClickListener {
            onBackClick(it)
        }


        mAdapter = RepositoriesAdapter()
        mAdapter.RepositoriesAdapter(listRepos, activity!!.applicationContext)

        val manager = LinearLayoutManager(this.context)
        var infiniteScrollListener = InfiniteScrollListener(manager, this, this)
        //recyclerview
        var RecycleRepos = viewOfLayout.findViewById(R.id.lrvRepos) as RecyclerView
        RecycleRepos.layoutManager = manager
        RecycleRepos.adapter = mAdapter
        RecycleRepos.addOnScrollListener(infiniteScrollListener)
        RecycleRepos.addItemDecoration(
            DividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL)
        )

        getRepos(user_login.toString()) //Load repos of user

        return viewOfLayout
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
            UserRepositoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun getRepos(querySearch:String){
        myApolloClient.myApolloClient.query(
            RepositoryQuery.builder()
                .user(querySearch)
                .build()
        ).enqueue(object : ApolloCall.Callback<RepositoryQuery.Data>() {
            override fun onResponse(dataResponse: Response<RepositoryQuery.Data>) {
                listRepos.clear() //clear list
                val repositories = dataResponse.data()?.user()?.repositories()?.edges()
                afterCursor = dataResponse.data()?.user()?.repositories()?.pageInfo()?.endCursor().toString()
                repositories!!.forEach {
                    var item = it.node()
                    if (item is RepositoryQuery.Node) { //item is automatically cast to User
                            listRepos.add(Repository(item.name(), item.description().toString(), item.pullRequests().totalCount().toString()))
                    }
                }
                // onResponse returns on a background thread. If you want to make UI updates make sure they are done on the Main Thread.
                activity?.runOnUiThread {
                    //emptyview
                    var emptyView = viewOfLayout.findViewById(R.id.emptyView) as TextView
                    var RecycleRepos = viewOfLayout.findViewById(R.id.lrvRepos) as RecyclerView

                    if (listRepos.isEmpty()) {
                        RecycleRepos.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        RecycleRepos.setVisibility(View.VISIBLE);
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

    fun onBackClick(view: View){
        val action = UserRepositoriesFragmentDirections.actionUserRepositoriesFragmentToUserSearchFragment()
        view.findNavController().navigate(action)
    }


    override fun isLoading(): Boolean {
        return isLoadingRecycler
    }

    override fun onLoadMore() {
        var RecycleRepos = viewOfLayout.findViewById(R.id.lrvRepos) as RecyclerView
        RecycleRepos.post{
            listRepos.add(null)
            mAdapter.notifyItemInserted(listRepos.size - 1)
        }

        Handler().postDelayed({
            mAdapter.removeNull()
            // Get next 10 users
            myApolloClient.myApolloClient.query(
                RepositoryNextQuery.builder()
                    .user(user_login.toString())
                    .afterCursor(afterCursor.toString())
                    .build()
            ).enqueue(object : ApolloCall.Callback<RepositoryNextQuery.Data>() {
                override fun onResponse(dataResponse: Response<RepositoryNextQuery.Data>) {
                    val repositories = dataResponse.data()?.user()?.repositories()?.edges()
                    afterCursor = dataResponse.data()?.user()?.repositories()?.pageInfo()?.endCursor().toString()
                    repositories!!.forEach {
                        var item = it.node()

                        if (item is RepositoryNextQuery.Node) { //item is automatically cast to User
                            listRepos.add(Repository(item.name(), item.description().toString(), item.pullRequests().totalCount().toString()))
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
