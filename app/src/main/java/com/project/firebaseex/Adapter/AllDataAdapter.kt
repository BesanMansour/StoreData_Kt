package com.project.firebaseex.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.project.firebaseex.Utilities.Model
import com.project.firebaseex.databinding.ItemDataBinding
import com.project.firebaseex.databinding.ItemImageBinding

class AllDataAdapter(
    private val modelList: List<Model>,
    private val context: Context,
    private val listener : ClickListener
) : RecyclerView.Adapter<AllDataAdapter.dataHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): dataHolder {
        val binding: ItemDataBinding = ItemDataBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return dataHolder(binding)
    }


    override fun onBindViewHolder(holder: dataHolder, position: Int) {
        val model = modelList[position]

        holder.title.text = model.title
        Glide.with(context)
            .load(model.images[0])
            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
            .into(holder.img)

        holder.itemView.setOnClickListener{
            listener.details(model.documentId)
        }
    }


    override fun getItemCount(): Int {
        return modelList.size
    }


    inner class dataHolder(private val binding: ItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img = binding.ItemImg
        val title = binding.ItemTitle

    }

    interface ClickListener{
        fun details(documentId: String)
    }
}