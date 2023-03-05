package com.example.myapplication.ui.listfile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.*
import com.example.myapplication.adapter.ListFileAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.callback.OnItemFileClickListener
import com.example.myapplication.databinding.ActivityListFileBinding
import com.example.myapplication.data.UiState
import com.example.myapplication.ui.bottomsheet.FileManagerBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFileActivity : BaseActivity<ActivityListFileBinding, ListFileViewModel>(),
    OnItemFileClickListener {
    private val viewModel: ListFileViewModel by viewModels()

    override val layoutId: Int = R.layout.activity_list_file

    override fun getVM(): ListFileViewModel = viewModel
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
        binding.rv.layoutManager = LinearLayoutManager(
            this@ListFileActivity,
            LinearLayoutManager.VERTICAL, false
        )

        adapter = ListFileAdapter(this)
        binding.rv.adapter = adapter

        if (title == "Image") {
            viewModel.getAllImages()
        } else if (title == "Audio") {
            viewModel.getAllAudio()
        } else if (title == "Recent Files") {
            viewModel.getAllRecentFile()
        } else if (title == "Document") {
            viewModel.getAllDocument()
        } else {
            viewModel.getAllFile()
            binding.edtSearch.requestFocus();
        }

        lifecycleScope.launch {
            viewModel.listFile.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        showLoading()
                        Log.d("awgawgawg", "initView: loading")
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

    }

    private fun setOnClick() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    override fun onClick(path: String) {

    }

    override fun onClickMore(filePath: String) {
        val bottomSheet = FileManagerBottomSheetFragment.newInstance(filePath)
        bottomSheet.show(supportFragmentManager, "bottom_sheet_tag")
    }


}