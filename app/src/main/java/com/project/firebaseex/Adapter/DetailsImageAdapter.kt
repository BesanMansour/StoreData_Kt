package com.project.firebaseex.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.project.firebaseex.databinding.ItemDataBinding
import com.project.firebaseex.databinding.ItemDetailsImageBinding
import com.project.firebaseex.databinding.ItemImageBinding

class DetailsImageAdapter(
    private val uriList: List<String>,
    private val context: Context,
) : RecyclerView.Adapter<DetailsImageAdapter.DetailsImageHolder>() {

    private var currentPage = 0
    private var totalPage = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsImageAdapter.DetailsImageHolder {
        val binding: ItemDetailsImageBinding = ItemDetailsImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return DetailsImageHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsImageAdapter.DetailsImageHolder, position: Int) {
        if (uriList.isNotEmpty()) {
            Glide.with(context)
                .load(uriList[position])
                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                .into(holder.img)
        }

        // تحديث الصفحة الحالية
        currentPage = position + 1
        // عدد الصور
        totalPage = uriList.size

        holder.tv.text = "$currentPage/$totalPage"
    }


    override fun getItemCount(): Int {
        return uriList.size
    }



    inner class DetailsImageHolder(private val binding: ItemDetailsImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img = binding.ItemDetailsImg
        val tv = binding.ItemDetailsTv

    }
}