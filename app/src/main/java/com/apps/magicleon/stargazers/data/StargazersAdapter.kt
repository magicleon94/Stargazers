package com.apps.magicleon.stargazers.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.apps.magicleon.stargazers.R
import com.apps.magicleon.stargazers.models.Stargazer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.loader_list_item.view.*
import kotlinx.android.synthetic.main.stargazer_list_item.view.*

class StargazersAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showLoader: Boolean = false
    private val VIEWTYPE_ITEM = 1
    private val VIEWTYPE_LOADER = 2

    private val items = ArrayList<Stargazer>()

    private val onClickListener = View.OnClickListener {
        val profileURL = it.tag as String
        val intent = Intent(Intent.ACTION_VIEW)

        intent.data = Uri.parse(profileURL)
        context.startActivity(intent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEWTYPE_ITEM) {
            val view = LayoutInflater.from(context).inflate(R.layout.stargazer_list_item, parent, false)
            view.setOnClickListener(onClickListener)
            return ViewHolder(view)
        } else if (viewType == VIEWTYPE_LOADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.loader_list_item, parent, false)
            return LoaderViewHolder(view)
        }

        throw IllegalArgumentException("Invalid ViewType: $viewType")
    }

    override fun getItemCount(): Int {
        return if (items.size == 0) {
            0
        } else {
            items.size + 1 //count the loader too
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {

            val stargazer = items[position]

            holder.run {
                tvStargazerUsername.text = stargazer.Name
                Glide.with(context)
                    .load(stargazer.AvatarURL)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(RequestOptions().encodeQuality(50))
                    .apply(RequestOptions().circleCrop())
                    .into(ivStargazerAvatar)

                itemView.tag = stargazer.ProfileURL
            }
        } else if (holder is LoaderViewHolder) {
            holder.run {
                if (showLoader) {
                    loadingMoreProgressBar.visibility = View.VISIBLE
                } else {
                    loadingMoreProgressBar.visibility = View.GONE
                }
            }
        }

    }

    fun add(stargazer: Stargazer) {
        items.add(stargazer)
    }

    fun addAll(stargazers: ArrayList<Stargazer>) {
        items.addAll(stargazers)
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(context.getString(R.string.stargazers_adapter_data_key), items)
    }

    fun restoreSavedInstanceState(savedInstanceState: Bundle) {
        if (savedInstanceState.containsKey(context.getString(R.string.stargazers_adapter_data_key))) {
            val savedItems =
                savedInstanceState.getParcelableArrayList<Stargazer>(context.getString(R.string.stargazers_adapter_data_key))
            items.clear()
            items.addAll(savedItems!!)
            notifyDataSetChanged()
        }
    }

    fun showLoading(status: Boolean) {
        showLoader = status
    }

    override fun getItemViewType(position: Int): Int {
        if (position != 0 && position == itemCount - 1) {
            return VIEWTYPE_LOADER
        }
        return VIEWTYPE_ITEM
    }

    override fun getItemId(position: Int): Long {
        if (position != 0 && position == itemCount - 1) {
            return -1 //loader id is -1
        }
        return super.getItemId(position)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvStargazerUsername: TextView = view.tv_stargazer_username
    val ivStargazerAvatar: ImageView = view.iv_stargazer_avatar
}

class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val loadingMoreProgressBar: ProgressBar = view.loading_more_progressbar
}

