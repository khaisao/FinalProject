package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
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
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


class GetImageActivity : AppCompatActivity(), PickiTCallbacks {
    private lateinit var binding: ActivityGetImageBinding
    private lateinit var tcpClient: MyTcpClient

    private lateinit var pickiT: PickiT

    var fileTypeCurrent = 0

    enum class PICK_FILE_TYPE(val type: Int) {
        Image(100),
        Txt(101),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickiT = PickiT(this, this, this)

        lifecycleScope.launch(Dispatchers.IO) {
            tcpClient = MyTcpClient()
            tcpClient.connect()
        }



        binding.tvChooseImage.setOnClickListener {
            fileTypeCurrent = PICK_FILE_TYPE.Image.type
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_FILE_TYPE.Image.type
            )
        }

        binding.tvChooseTxt.setOnClickListener {
            fileTypeCurrent = PICK_FILE_TYPE.Txt.type
            val intent = Intent()
            intent.setType("*/*");
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Txt File"),
                PICK_FILE_TYPE.Txt.type
            )
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                pickiT.getPath(data.data, Build.VERSION.SDK_INT)
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
            try {
                lifecycleScope.launch(Dispatchers.IO) {
                    tcpClient.sendFile(path)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }


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