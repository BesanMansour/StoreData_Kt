package com.project.firebaseex.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.project.firebaseex.databinding.ItemImageBinding

class AddImageAdapter(
        private val uriList: List<Uri>,
        private val context: Context,
        private val listener: DeleteListener
    ) : RecyclerView.Adapter<AddImageAdapter.ImageHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding: ItemImageBinding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {

        if (uriList.isNotEmpty()){
            Glide.with(context)
                .load(uriList[position])
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(holder.img)

            holder.imgDelete.setOnClickListener{
                listener.delete(holder.adapterPosition)
            }

        }

    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    class ImageHolder(binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
        var img: ImageView
        var imgDelete: ImageView

        init {
            img = binding.img
            imgDelete = binding.imgDelete
        }
    }

    interface DeleteListener{
        fun delete(pos:Int)
    }
}