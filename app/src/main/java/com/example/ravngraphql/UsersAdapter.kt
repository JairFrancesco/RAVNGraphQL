package com.example.ravngraphql
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso;


class UsersAdapter(val clickListener: (User) -> Unit) :  RecyclerView.Adapter<UsersAdapter.CustomViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    var users: MutableList<User?>  = ArrayList()
    lateinit var context: Context

    fun UsersAdapter(users : MutableList<User?>, context: Context){
        this.users = users
        this.context = context
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = users.get(position)
        if (holder is DataViewHolder) {
            holder.bind(item, clickListener)
        } else {
            //
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var root: View?
        if (viewType == VIEW_TYPE_ITEM) {
            root = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return DataViewHolder(root!!)
        } else {
            root = LayoutInflater.from(parent.context).inflate(R.layout.item_progressbar, parent, false)
            return ProgressViewHolder(root!!)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun getItemViewType(position: Int): Int {
        if (users.get(position) != null) {
            return VIEW_TYPE_ITEM
        } else {
            return VIEW_TYPE_LOADING
        }
    }


    class DataViewHolder(view: View) : CustomViewHolder(view) {
        val userNameLocation = view.findViewById(R.id.txtNameLocation) as TextView
        val userLogin = view.findViewById(R.id.txtLogin) as TextView
        val userAvatar = view.findViewById(R.id.ivUserAvatar) as ImageView

        fun bind(user:User?, clickListener: (User) -> Unit){
            userNameLocation.text = user?.name + ", " +  user?.location
            userLogin.text = user?.login
            itemView.setOnClickListener { clickListener(user!!)} //Click listener
            userAvatar.loadUrl(user?.avatar_url.toString())
        }
        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }


    class ProgressViewHolder(itemView: View) : CustomViewHolder(itemView) {
    }

    open class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    fun removeNull() {
        users.removeAt(users.size - 1)
        notifyItemRemoved(users.size)
    }

}