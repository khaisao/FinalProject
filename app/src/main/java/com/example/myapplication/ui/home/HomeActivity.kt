package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.view.Window
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.example.myapplication.adapter.ListFileAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.callback.OnItemFileClickListener
import com.example.myapplication.data.UiState
import com.example.myapplication.data.VolumeStats
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.gbToUse
import com.example.myapplication.getShiftUnits
import com.example.myapplication.service.WebService
import com.example.myapplication.ui.bottomsheet.FileManagerBottomSheetFragment
import com.example.myapplication.ui.bottomsheet.ServiceBottomSheetFragment
import com.example.myapplication.ui.listfile.ListFileActivity
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import kotlin.math.roundToLong


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), PickiTCallbacks,OnItemFileClickListener {

    private val viewModel: HomeViewModel by viewModels()


    override val layoutId: Int = R.layout.activity_home

    override fun getVM(): HomeViewModel =viewModel

    private val CSS_CONTENT_TYPE = "text/css;charset=utf-8"
    private lateinit var pickiT: PickiT
    private lateinit var adapter:ListFileAdapter
    companion object {
        fun Float.nice(fieldLength: Int = 6): String =
            String.format(Locale.US, "%$fieldLength.2f", this)
        const val KB = 1_000L
        const val MB = KB * KB
        const val GB = KB * KB * KB
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Constants.DIR.exists()) {
            Constants.DIR.mkdirs()
        }

        pickiT = PickiT(this, this, this)

        val window: Window = window
        window.setBackgroundDrawableResource(R.drawable.bg_top_gradient_grey)
        setSupportActionBar(binding.toolBar)
        initView()
        setOnClick()

    }
    private lateinit var mStorageManager: StorageManager
    private val mStorageVolumesByExtDir = mutableListOf<VolumeStats>()


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        val listFile = arrayListOf<String>()
        val directory = Constants.DIR
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                val filePath = file.absolutePath
                listFile.add(filePath)
            }
        }
        adapter = ListFileAdapter(this)

        binding.rv.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.VERTICAL,false)
        binding.rv.adapter = adapter
        mStorageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        lifecycleScope.launch {
            viewModel.listFileSent.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        showLoading()
                    }
                    is UiState.Success -> {
                        val allImagePaths = uiState.data
                        adapter.submitList(allImagePaths)
                        hiddenLoading()
                    }
                    is UiState.Failure -> {
                        hiddenLoading()
                        val errorMessage = uiState.error
                    }
                }
            }
        }

        getVolumeStats()
        showVolumeStats()
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun showVolumeStats() {
        val sb = StringBuilder()
        mStorageVolumesByExtDir.forEach { volumeStats ->
            val (usedToShift, usedSizeUnits) = getShiftUnits(volumeStats.mUsedSpace)
            val usedSpace = (100f * volumeStats.mUsedSpace / usedToShift).roundToLong() / 100f
            val (totalToShift, totalSizeUnits) = getShiftUnits(volumeStats.mTotalSpace)
            val totalSpace = (100f * volumeStats.mTotalSpace / totalToShift).roundToLong() / 100f
            sb.appendln("${usedSpace.nice()}$usedSizeUnits of${totalSpace.nice()}$totalSizeUnits used")
            binding.tvAvailable.text=sb.toString()
            binding.progressBar.max=totalSpace.toInt()
            binding.progressBar.progress=usedSpace.toInt()
        }
    }

    @SuppressLint("ServiceCast")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getVolumeStats() {
        val extDirs = getExternalFilesDirs(null)
        mStorageVolumesByExtDir.clear()
        extDirs.forEach { file ->
            val storageVolume: StorageVolume? = mStorageManager.getStorageVolume(file)
            if (storageVolume == null) {
            } else {
                val totalSpace: Long
                val usedSpace: Long
                if (storageVolume.isPrimary) {
                    val uuid = StorageManager.UUID_DEFAULT
                    val storageStatsManager =
                        getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                    totalSpace = (storageStatsManager.getTotalBytes(uuid) / 1_000_000_000) * gbToUse
                    usedSpace = totalSpace - storageStatsManager.getFreeBytes(uuid)
                } else {
                    totalSpace = file.totalSpace
                    usedSpace = totalSpace - file.freeSpace
                }
                mStorageVolumesByExtDir.add(
                    VolumeStats(storageVolume, totalSpace, usedSpace)
                )
            }
        }
    }

    private fun setOnClick() {
        binding.apply {
            llImage.setOnClickListener {
                val intent = Intent(this@HomeActivity, ListFileActivity::class.java)
                intent.putExtra("title","Image")
                startActivity(intent)
            }
            llDocument.setOnClickListener {
                val intent = Intent(this@HomeActivity, ListFileActivity::class.java)
                intent.putExtra("title","Document")
                startActivity(intent)
            }
            llAudio.setOnClickListener {
                val intent = Intent(this@HomeActivity, ListFileActivity::class.java)
                intent.putExtra("title","Audio")
                startActivity(intent)
            }
            tvSeeAll.setOnClickListener {
                val intent = Intent(this@HomeActivity, ListFileActivity::class.java)
                intent.putExtra("title","File Sent")
                startActivity(intent)
            }
            flSearch.setOnClickListener {
                val intent = Intent(this@HomeActivity, ListFileActivity::class.java)
                intent.putExtra("title","Search")
                startActivity(intent)
            }
            fabService.setOnClickListener {
                val serviceBottomSheet = ServiceBottomSheetFragment()
                WebService.start(applicationContext)
                serviceBottomSheet.show(supportFragmentManager,"Service")
            }

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllFileSent()
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

    override fun onClick(path: String) {

    }

    override fun onClickMore(filePath: String) {
        val bottomSheet = FileManagerBottomSheetFragment.newInstance(filePath)
        bottomSheet.show(supportFragmentManager, "bottom_sheet_tag")
    }
}