package com.promisebooks.app.merchant

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.promisebooks.app.databinding.UploadFragmentBinding
import com.promisebooks.app.util.BaseFragment
import com.promisebooks.app.util.CartCallback
import com.promisebooks.app.util.BookView
import java.io.IOException


class UploadFragment : BaseFragment<UploadFragmentBinding, UploadViewModel>(), CartCallback {

    private  var imageurl: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val PERMISSION_CODE = 2


    companion object {
        fun newInstance() = UploadFragment()
    }


    override fun setUpViews() {
        super.setUpViews()
        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.image.setOnClickListener {
            chooseImage()
        }

        binding.upload.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            binding.container.visibility = View.GONE
            val  title = binding.titleInput.text.toString()
            val  descripe = binding.descripInput.text.toString()
            val  price = binding.priceInput.text.toString()
            if (title.isNotEmpty() and descripe.isNotEmpty() and price.isNotEmpty() and (imageurl != null)){

                imageurl?.let { it1 -> viewModel.uploadImage(it1, title,descripe, price, this) }

            }else{
                binding.progressCircular.visibility = View.GONE
                binding.container.visibility = View.VISIBLE

            }
        }
    }


    private fun chooseImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let { checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } == PermissionChecker.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions( permission, PERMISSION_CODE)
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


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data!!
            if (uri != null) {
                imageurl = uri
            }
            try {
             /*   val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                // Log.d(TAG, String.valueOf(bitmap));
                binding.imageView.setImageBitmap(bitmap)*/
                val bitmap = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
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

    override fun getViewModel(): Class<UploadViewModel> {
     return UploadViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): UploadFragmentBinding = UploadFragmentBinding.inflate(inflater, container, false)

    override fun callback() {
        activity?.let { it1 -> BookView.alert("UPLOADED", binding.progressCircular, it1, false) }
        clear()
        binding.container.visibility = View.VISIBLE
    }

    override fun callbackError() {
        activity?.let { it1 -> BookView.alert("Failed", binding.progressCircular, it1, false) }
        binding.container.visibility = View.VISIBLE
    }
}