package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.*
import com.example.myapplication.adapter.ListFileAdapter
import com.example.myapplication.callback.OnItemFileClickListener
import com.example.myapplication.databinding.ActivityListFileBinding
import okhttp3.internal.notify

class ListFileActivity : AppCompatActivity(), OnItemFileClickListener {
    private lateinit var binding: ActivityListFileBinding
    private lateinit var adapter: ListFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val window: Window = window
        window.setBackgroundDrawableResource(R.drawable.bg_top_gradient_grey)
        setSupportActionBar(binding.toolBar)
        initView()
        setOnClick()
    }

    private fun initView() {
        val title = intent.getStringExtra("title")
        binding.tvTitleFileType.text = title
        binding.rv.layoutManager = LinearLayoutManager(this@ListFileActivity,
            LinearLayoutManager.VERTICAL,false)

        if(title == "Image"){
            val listFileImage = getAllImagePaths()
            adapter = ListFileAdapter(listFileImage,this)
            binding.rv.adapter = adapter
        }
        if(title == "Audio"){
            val listFileImage = getAllAudioPaths()
            Log.d("awgssssawgawg", "initView: ${listFileImage}")
            adapter = ListFileAdapter(listFileImage,this)
            binding.rv.adapter = adapter
        }
        else{
            val fileList = getAllDocumentFilePaths(this@ListFileActivity)
            adapter = ListFileAdapter(fileList,this)
            binding.rv.adapter = adapter
        }

    }

    private fun setOnClick() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    override fun onClick(path: String) {

    }


}