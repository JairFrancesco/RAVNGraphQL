package com.example.ravngraphql
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso


class RepositoriesAdapter :  RecyclerView.Adapter<RepositoriesAdapter.CustomViewHolder>() {

    var repos: MutableList<Repository?>  = ArrayList()
    lateinit var context: Context
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    fun RepositoriesAdapter(repos : MutableList<Repository?>, context: Context){
        this.repos = repos
        this.context = context
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = repos.get(position)
        if (holder is DataViewHolder) {
            holder.bind(item)
        } else {
            //
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var root: View?
        if (viewType == VIEW_TYPE_ITEM) {
            root = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
            return DataViewHolder(root!!)
        } else {
            root = LayoutInflater.from(parent.context).inflate(R.layout.item_progressbar, parent, false)
            return ProgressViewHolder(root!!)
        }
    }

    override fun getItemCount(): Int {
        return repos.size
    }


    override fun getItemViewType(position: Int): Int {
        if (repos.get(position) != null) {
            return VIEW_TYPE_ITEM
        } else {
            return VIEW_TYPE_LOADING
        }
    }


    class DataViewHolder(view: View) : CustomViewHolder(view) {
        val repoName = view.findViewById(R.id.txtName) as TextView
        val repoDescription = view.findViewById(R.id.txtDescription) as TextView
        val repoPrCount = view.findViewById(R.id.txtPrCount) as TextView

        fun bind(repo: Repository?){
            repoName.text = repo?.name
            repoDescription.text = repo?.description
            repoPrCount.text =  "PR Count: " +  repo?.pullRequestsCount
        }
    }


    class ProgressViewHolder(itemView: View) : CustomViewHolder(itemView) {
    }

    open class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    fun removeNull() {
        repos.removeAt(repos.size - 1)
        notifyItemRemoved(repos.size)
    }
}