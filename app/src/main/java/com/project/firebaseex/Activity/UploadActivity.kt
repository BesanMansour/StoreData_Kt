package com.project.firebaseex.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.project.firebaseex.Adapter.AddImageAdapter
import com.project.firebaseex.Utilities.Model
import com.project.firebaseex.databinding.ActivityUploadBinding
import kotlin.collections.ArrayList
import com.google.firebase.storage.StorageReference
import com.project.firebaseex.R


class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var uriList: ArrayList<Uri>
    var uriFromStorage: ArrayList<String>? = null
    private lateinit var addImageAdapter: AddImageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inculd.ToolTv.text = "Home Page2"
        binding.inculd.ToolMenuIcon.visibility = View.VISIBLE
        binding.inculd.ToolMenuIcon.setOnClickListener {
//            openOptionsMenu()
            showPopupMenu(it)
        }


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        uriList = ArrayList()
        uriFromStorage = ArrayList()


        val al1 = registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) { result ->
                if (result != null) uriList.add(result)
                if (uriList.size != 0) {
                    binding.UploadRcImg.visibility = View.VISIBLE
                    binding.UploadImgAdd.visibility = View.VISIBLE
                    binding.UploadImgAddImage.visibility = View.GONE
                } else {
                    binding.UploadRcImg.visibility = View.GONE
                    binding.UploadImgAdd.visibility = View.GONE
                    binding.UploadImgAddImage.visibility = View.VISIBLE
                }
                addImageAdapter = AddImageAdapter(uriList, baseContext, object : AddImageAdapter.DeleteListener {
                        override fun delete(pos: Int) {
                            if (pos >= 0 && pos < uriList.size) {
                                uriList.removeAt(pos)
                                addImageAdapter.notifyItemRemoved(pos)
                                addImageAdapter.notifyItemRangeChanged(pos, uriList.size)
                                if (uriList.isEmpty()) {
                                    binding.UploadRcImg.visibility = View.GONE
                                    binding.UploadImgAdd.visibility = View.GONE
                                    binding.UploadImgAddImage.visibility = View.VISIBLE
                                } else {
                                    binding.UploadRcImg.visibility = View.VISIBLE
                                    binding.UploadImgAdd.visibility = View.VISIBLE
                                    binding.UploadImgAddImage.visibility = View.GONE

                                }
                            }
                        }
                    })

                binding.UploadRcImg.setLayoutManager(LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false))

                binding.UploadRcImg.setAdapter(addImageAdapter)

            }

        binding.UploadImgAddImage.setOnClickListener { al1.launch("image/*") }
        binding.UploadImgAdd.setOnClickListener { al1.launch("image/*") }

        binding.UploadBtnSave.setOnClickListener {
            saveData()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun showPopupMenu(view:View){
        val popupMenu =  PopupMenu(applicationContext,view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener{item->
            when (item.itemId) {
                R.id.MenuAllData -> {
                    val intent = Intent(this, AllDataActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.MenuLogout-> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@UploadActivity, LoginActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        popupMenu.show()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.MenuAllData -> {
//                val intent = Intent(this, AllDataActivity::class.java)
//                startActivity(intent)
//                return true
//            }
//            R.id.MenuLogout-> {
//                // تم النقر على عنصر القائمة 2
//                // قم بتنفيذ الإجراءات الخاصة بك هنا
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    private fun createModel(description: String, uid: String, title: String, time: Long) {
        val model = Model(title, description,uid + time, uriFromStorage!!)
        uploadUpload(model, uid + time)
    }

    private fun uploadUpload(model: Model, documentName: String) {
        firestore.collection("Uploads").document(firebaseUser!!.uid)
            .collection("userUpload").document(documentName)
            .set(model)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                val documentId = documentName
                if (task.isSuccessful) {
                    binding.UploadProgress.visibility = View.GONE
                    binding.UploadBtnSave.visibility = View.VISIBLE
                    Snackbar.make(binding.UploadCoor, "تم اضافة النموذج بنجاح", Snackbar.LENGTH_SHORT)
                        .setAction("إغلاق") {}
                        .show()
                } else {

                }
            })
    }

    private fun saveData() {
        val des: String = binding.UploadDes.text.toString()
        val title: String = binding.UploadTitle.text.toString()
        if (TextUtils.isEmpty(des)) {
            binding.UploadDes.setError("يجب ملء هذا الحقل")
        } else if (TextUtils.isEmpty(title)) {
            binding.UploadTitle.setError("يجب ملء هذا الحقل")
        } else {
                binding.UploadProgress.visibility = View.VISIBLE
                binding.UploadBtnSave.visibility = View.GONE
            val uid = firebaseUser!!.uid
            val time = System.currentTimeMillis()
            if (uriList!!.size != 0) {
                //نرفع الصور ونخزنهم
                uriFromStorage = ArrayList<String>()
                for (i in uriList.indices) {
                    val reference: StorageReference = firebaseStorage.getReference(
                        "Uploads/" + "/" + uid + time + "/" + uriList.get(
                            i
                        ).getLastPathSegment()
                    )
                    val uploadTask: UploadTask = reference.putFile(uriList.get(i))
                    val finalI: Int = i
                    val uriTask: Task<Uri> = uploadTask.continueWithTask(
                        Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }
                            reference.downloadUrl // استخدام downloadUrl بدلاً من getDownloadUrl()
                        }
                    ).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val uriString = task.result.toString()
                            if (!uriString.isEmpty()) {
                                uriFromStorage!!.add(uriString)
                                if (finalI == uriList.size - 1) {
                                    createModel(des, uid, title, time)
                                }
                            }
                        } else {
                            //Todo Add LLField
                        }
                    })

                }
            } else {
                createModel(des, uid, title, time)
            }
        }
    }
}