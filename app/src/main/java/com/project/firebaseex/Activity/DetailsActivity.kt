package com.project.firebaseex.Activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.firebaseex.Adapter.AllDataAdapter
import com.project.firebaseex.Adapter.DetailsImageAdapter
import com.project.firebaseex.R
import com.project.firebaseex.Utilities.Model
import com.project.firebaseex.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imageAdapter : DetailsImageAdapter
    private lateinit var modelList : List<Model>
    private var currentPage = 0
    private var totalPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        modelList = ArrayList()
        val intent = intent
        val documentId = intent.getStringExtra("documentId")

        if (documentId != null) {
            firestore.collection("Uploads")
                .document(firebaseUser!!.uid)
                .collection("userUpload")
                .document(documentId)
                .get()
                .addOnSuccessListener { documentSnapshot->
                    if (documentSnapshot.exists()){
                        val data = documentSnapshot.data
                        val title = data?.get("title") as String
                        val des = data["description"] as String
                        val images = data["images"] as ArrayList<String>
                        binding.DetailsTitle.text = title
                        binding.DetailsDes.text = des

//                        imageAdapter = DetailsImageAdapter(images, this)
//                        binding.DetailsRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
//                        binding.DetailsRV.adapter = imageAdapter

                        binding.DetailsVP.adapter = DetailsImageAdapter(images, this)
                        binding.DetailsVP.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        binding.DetailsVP.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                // تحديث المتغيرات currentPage و totalPage
                                currentPage = position + 1
                                totalPage = images.size
                                Log.e("total",totalPage.toString())
                                Log.e("currentPage",currentPage.toString())
                            }
                        })

                        binding.DetailsCircle.setViewPager(binding.DetailsVP)

                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("data", "Error getting documents: $exception")
                }
        }

    }
}