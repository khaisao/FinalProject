package com.example.myapplication.adapter

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.callback.OnItemFileClickListener
import com.example.myapplication.databinding.ItemFileBinding
import com.example.myapplication.getFileName
import com.example.myapplication.getFileType
import com.example.myapplication.isAudioFile
import com.example.myapplication.ui.bottomsheet.FileManagerBottomSheetFragment
import com.example.myapplication.util.FileUtils
import java.io.File

class ListFileAdapter(
    private val callback: OnItemFileClickListener
) : ListAdapter<String, ListFileAdapter.ViewHolder>(FileDiffCallBack) {

    private var originalListFilePath = emptyList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }

    override fun submitList(list: List<String>?) {
        originalListFilePath = list.orEmpty()
        filter("")
    }

    fun filter(query: String) {
        val filteredList = originalListFilePath.filter {
            getFileName(it).contains(query, true)
        }
        super.submitList(filteredList)
    }

    inner class ViewHolder(private val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(absolutePath: String) {
            val file = File(absolutePath)
            if (file.isFile && file.exists()) {
                try{
                    val typeFile = getFileType(absolutePath)
                    if (typeFile == ".png" || typeFile == ".jpg" || typeFile == ".webp" || typeFile == ".jpeg") {
                        Glide.with(binding.root.context)
                            .load(File(absolutePath)).into(binding.ivFile)
                        binding.ivPlayVideo.visibility = View.GONE
                    } else if (typeFile == ".mp4") {
                        binding.ivFile.setImageBitmap(getVideoThumbnail(File(absolutePath)))
                        binding.ivPlayVideo.visibility = View.VISIBLE
                    } else if (isAudioFile(absolutePath)) {
                        binding.ivPlayVideo.visibility = View.GONE
                        binding.ivFile.setImageResource(R.drawable.ic_audio)
                    } else {
                        binding.ivFile.setImageResource(R.drawable.ic_document)
                        binding.ivPlayVideo.visibility = View.GONE
                    }
                    val fileName = getFileName(absolutePath)
                    binding.tvFileName.text = fileName

                    val fileSizeInBytes = file.length()
                    val fileSizeInKB = fileSizeInBytes / 1024
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB >= 1) {
                        binding.tvFileSize.text = fileSizeInMB.toString() + " MB"
                    } else {
                        binding.tvFileSize.text = fileSizeInKB.toString() + " KB"
                    }

                    binding.clFile.setOnClickListener {
                        FileUtils.openFile(absolutePath,binding.root.context)
                    }

                    binding.ivMore.setOnClickListener {
                        callback.onClickMore(absolutePath)
                    }
                }catch (e:java.lang.Exception){
                    Log.d("aegawgawg", "bind: $e")
                }
            }
        }
    }

    private fun getVideoThumbnail(path: File): Bitmap? {
        return ThumbnailUtils.createVideoThumbnail(
            path.toString(),
            MediaStore.Images.Thumbnails.MINI_KIND
        )
    }
}

object FileDiffCallBack : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}

