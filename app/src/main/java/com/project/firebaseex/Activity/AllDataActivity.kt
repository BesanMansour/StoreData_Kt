package com.project.firebaseex.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.firebaseex.Adapter.AllDataAdapter
import com.project.firebaseex.R
import com.project.firebaseex.Utilities.Model
import com.project.firebaseex.databinding.ActivityAllDataBinding

class AllDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllDataBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var allDataAdapter: AllDataAdapter
    private lateinit var modelList : List<Model>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        modelList = ArrayList()

//        binding.DataProgress.visibility = View.VISIBLE

        firestore.collection("Uploads")
            .document(firebaseUser!!.uid)
            .collection("userUpload")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty){
                    binding.DataNoData.visibility = View.VISIBLE
                    binding.DataRecycler.visibility = View.GONE
//                    binding.DataProgress.visibility = View.GONE

                }else {
                    binding.DataNoData.visibility = View.GONE
                    binding.DataRecycler.visibility = View.VISIBLE
//                    binding.DataProgress.visibility = View.GONE

                    for (document in querySnapshot) {
                        modelList = querySnapshot.toObjects(Model::class.java)
                    }

                    allDataAdapter = AllDataAdapter(
                        modelList,
                        applicationContext,
                        object : AllDataAdapter.ClickListener {
                            override fun details(documentId: String) {
                                val intent =
                                    Intent(this@AllDataActivity, DetailsActivity::class.java)
                                intent.putExtra("documentId", documentId)
                                startActivity(intent)
                            }

                        })
                    binding.DataRecycler.layoutManager = LinearLayoutManager(this)
                    binding.DataRecycler.adapter = allDataAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.e("data", "Error getting documents: $exception")
            }

    }
}