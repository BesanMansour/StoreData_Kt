package com.project.firebaseex.Activity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.project.firebaseex.R
import com.project.firebaseex.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore
    var calender: Calendar? = null
    private lateinit var firebaseStorage: FirebaseStorage
    private var REQUEST_CODE: Int = 1
    private lateinit var image: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        firestore = FirebaseFirestore.getInstance()
        calender = Calendar.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }

        val al1 = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { result: Uri? ->
            if (result != null) {
                Glide.with(baseContext).load(result).circleCrop().error(R.drawable.user)
                    .into(binding.RegisterImg)
                if (firebaseUser != null) {
                    val reference: StorageReference =
                        firebaseStorage.getReference("users/" + "images/" + firebaseUser!!.uid)
//                    val reference: StorageReference =
//                        firebaseStorage.getReference("users/" + "images/" + firebaseUser!!.uid)
                    val uploadTask: StorageTask<UploadTask.TaskSnapshot> =
                        reference.putFile(result)
                    uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                        reference.downloadUrl.addOnCompleteListener { task: Task<Uri> ->
                            if (task.isSuccessful) {
                                image = task.result.toString()
                            }
                        }
                    }

                } else {
                    // المستخدم غير مسجل دخوله، يمكنك تنفيذ الإجراء المناسب هنا
                }
            }
        }

        binding.RegisterAddImgUser.setOnClickListener {
            al1.launch("image/*")
        }

        binding.RegisterBtnSave.setOnClickListener {
            register()
        }

        binding.RegisterBirth.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun register() {
        val fullName = binding.RegisterFullNameET.text.toString()
        val email = binding.RegisterEmailEt.text.toString()
        val userName = binding.RegisterUserNameEt.text.toString()
        val password = binding.RegisterPassEt.text.toString()
        val birth = binding.RegisterBirth.text.toString()
        val genderId = binding.RegisterRadioGroup.checkedRadioButtonId
        var gender = findViewById<View>(genderId).toString()

        gender = if (binding.RegisterMale.isChecked) {
            "Male"
        } else {
            "Female"
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid // Use safe call operator (?.)
                    if (userId != null) {

                        val data = HashMap<String, Any>()
                        data["fullName"] = fullName
                        data["email"] = email
                        data["userName"] = userName
                        data["Gender"] = gender
                        data["birth"] = birth

                        firestore.collection("Users").document(userId)
                            .set(data)
                            .addOnSuccessListener {
                                var intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }
                    } else {
                        // Handle the case where userId is null
                        // This should be an exceptional case and may require error handling
                    }
                } else {
                    // Handle the case where the task is not successful
                }
            }
            .addOnFailureListener {
                // Handle the failure case
            }

    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/${month + 1}/$year"
        binding.RegisterBirth.text = Editable.Factory.getInstance().newEditable(date)
    }
}


// ctr + shift + p => نوع المتغير
