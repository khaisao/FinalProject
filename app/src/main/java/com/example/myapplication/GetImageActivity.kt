package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.encodeToString
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityGetImageBinding
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class GetImageActivity : AppCompatActivity(),PickiTCallbacks {
    private lateinit var binding: ActivityGetImageBinding
    private lateinit var tcpClient: MyTcpClient

    private lateinit var pickiT: PickiT
    var myStr = ""
    private lateinit var byteArray: ByteArray


    private val PICK_IMAGE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pickiT = PickiT(this, this, this)

        lifecycleScope.launch {
            tcpClient = MyTcpClient()
            tcpClient.connect()
        }
        binding.tvChooseImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        binding.tvDecode.setOnClickListener {
            val bytes = android.util.Base64.decode(myStr,android.util.Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            binding.iv.setImageBitmap(bmp)
        }


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
//                    pickiT.getPath(data.data, Build.VERSION.SDK_INT)
                    val uri: Uri? = data.data
                    try {
                        lifecycleScope.launch(Dispatchers.IO){

                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        // initialize byte stream
                        val stream = ByteArrayOutputStream()
                        // compress Bitmap
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        // Initialize byte array
                         byteArray = stream.toByteArray()
                             myStr = encodeToString(byteArray,android.util.Base64.DEFAULT)
                            withContext(Dispatchers.Main){
                                binding.tvCode.text = myStr
                            }
                                tcpClient.sendMessage(myStr)

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
        }
    }

    override fun PickiTonUriReturned() {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (path != null) {
//                tcpClient.sendImageToServer(File(path))

        }
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        TODO("Not yet implemented")
    }
}