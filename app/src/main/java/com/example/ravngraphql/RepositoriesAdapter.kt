package com.example.ravngraphql
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast


class RepositoriesAdapter :  RecyclerView.Adapter<RepositoriesAdapter.ViewHolder>() {

    var repos: MutableList<Repository>  = ArrayList()
    lateinit var context: Context

    fun RepositoriesAdapter(repos : MutableList<Repository>, context: Context){
        this.repos = repos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = repos.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_repository, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repoName = view.findViewById(R.id.txtName) as TextView
        val repoDescription = view.findViewById(R.id.txtDescription) as TextView
        val repoPrCount = view.findViewById(R.id.txtPrCount) as TextView

        fun bind(repo: Repository, context: Context){
            repoName.text = repo.name
            repoDescription.text = repo.description
            repoPrCount.text = repo.pullRequestsCount
            itemView.setOnClickListener(View.OnClickListener {
                Toast.makeText(context, repo.name, Toast.LENGTH_SHORT).show()
            })
        }
    }
}