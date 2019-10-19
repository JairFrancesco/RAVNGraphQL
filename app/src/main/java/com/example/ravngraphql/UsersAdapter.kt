package com.example.ravngraphql
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso;

class UsersAdapter :  RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    var users: MutableList<User>  = ArrayList()
    lateinit var context: Context

    fun UsersAdapter(users : MutableList<User>, context: Context){
        this.users = users
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameLocation = view.findViewById(R.id.txtNameLocation) as TextView
        val userLogin = view.findViewById(R.id.txtLogin) as TextView
        val userAvatar = view.findViewById(R.id.ivUserAvatar) as ImageView

        fun bind(user:User, context: Context){
            userNameLocation.text = user.name + "," +  user.location
            userLogin.text = user.location
            itemView.setOnClickListener(View.OnClickListener { Toast.makeText(context, user.name, Toast.LENGTH_SHORT).show() })
            userAvatar.loadUrl(user.avatar_url)
        }
        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }

}