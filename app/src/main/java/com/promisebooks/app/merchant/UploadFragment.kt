package com.promisebooks.app.merchant

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.UploadFragmentBinding
import com.promisebooks.app.model.Book
import com.promisebooks.app.util.K
import java.io.IOException


class UploadFragment : Fragment() {
    private lateinit var binding:UploadFragmentBinding
    private  var imageurl: Uri? = null
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private val PICK_IMAGE_REQUEST = 1
    private val PERMISSION_CODE = 2
    private var mStorageRef: StorageReference? = null

    companion object {
        fun newInstance() = UploadFragment()
    }

    private lateinit var viewModel: UploadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.upload_fragment, container, false)
        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        mStorageRef = FirebaseStorage.getInstance().reference
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
        binding.upload.setOnClickListener {
             binding.progressCircular.visibility = View.VISIBLE
            binding.container.visibility = View.GONE
          val  title = binding.titleInput.text.toString()
            val  descripe = binding.descripInput.text.toString()
            val  price = binding.priceInput.text.toString()
            if (title.isNotEmpty() and descripe.isNotEmpty() and price.isNotEmpty() and (imageurl != null)){

                val riversRef: StorageReference = mStorageRef?.child("flutterwave/${System.currentTimeMillis()}")!!
                riversRef.putFile(imageurl!!).addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {it1 ->
                         val book = Book(title, it1.toString(), descripe, price)
                collection.document().set(book).addOnSuccessListener {
                    activity?.let { it1 -> K.alert("UPLOADED", binding.progressCircular, it1, false) }
                    clear()
                    binding.container.visibility = View.VISIBLE
                }
                    }

                }.addOnFailureListener {
                    activity?.let { it1 -> K.alert("Failed", binding.progressCircular, it1, false) }
                    binding.container.visibility = View.VISIBLE
                }

            }else{
                binding.progressCircular.visibility = View.GONE
                binding.container.visibility = View.VISIBLE

            }
        }

        binding.image.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let { checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == PermissionChecker.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions( permission, PERMISSION_CODE);
            } else {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activity?.let {
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
                }

            }
        } else{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activity?.let {
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
            }

        }
    }

    private fun clear() {
        binding.apply {
           titleInput.setText("")
            descripInput.setText("")
            priceInput.setText("")
            invalidateAll()
        }
    }

    override fun onStart() {
        super.onStart()
        setUpListener()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null){
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                /* val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                 activity?.startActivity(intent)
                 activity?.finish()*/
                activity?.let {it1 ->
                    it1.startActivity(
                        Intent(it1, AuthActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    it1.finish()
                }

            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data!!
            if (uri != null) {
                imageurl = uri
            }
            try {
             /*   val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                // Log.d(TAG, String.valueOf(bitmap));
                binding.imageView.setImageBitmap(bitmap)*/
                val bitmap = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        uri
                    )
                    else -> {
                        val source =
                            activity?.contentResolver?.let { uri?.let { it1 ->
                                ImageDecoder.createSource(it,
                                    it1
                                )
                            } }
                        source?.let { ImageDecoder.decodeBitmap(it) }
                    }
                }
                binding.imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activity?.let {
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
                }

            } else {
                Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}