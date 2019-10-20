package com.example.ravngraphql
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


class InfiniteScrollListener(
    private val linearLayoutManager: LinearLayoutManager,
    private val listener: OnLoadMoreListener?,
    private val loadingListener: isLoadingListener?
) : RecyclerView.OnScrollListener() {
    private var pauseListening = false
    private var END_OF_FEED_ADDED = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dx == 0 && dy == 0)
            return

        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

        if (!loadingListener!!.isLoading() && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD && totalItemCount != 0 && !END_OF_FEED_ADDED && !pauseListening) {
            listener?.onLoadMore()
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    interface isLoadingListener {
        fun isLoading(): Boolean
    }

    fun addEndOfRequests() {
        this.END_OF_FEED_ADDED = true
    }

    fun pauseScrollListener(pauseListening: Boolean) {
        this.pauseListening = pauseListening
    }

    companion object {
        private val VISIBLE_THRESHOLD = 2
    }
}