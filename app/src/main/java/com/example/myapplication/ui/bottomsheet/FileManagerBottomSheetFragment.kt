package com.example.myapplication.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBottomSheetMoreBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FileManagerBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_FILE_PATH = "file_path"

        fun newInstance(filePath: String): FileManagerBottomSheetFragment {
            return FileManagerBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILE_PATH, filePath)
                }
            }
        }
    }

    private lateinit var binding: FragmentBottomSheetMoreBinding
    private val filePath: String by lazy {
        arguments?.getString(ARG_FILE_PATH) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llOpen.setOnClickListener {
            com.example.myapplication.util.FileUtils.openFile(filePath,requireContext())
        }
        // Set up the views and listeners here
//        binding.tvFilePath.text = filePath
//        binding.btnClose.setOnClickListener {
//            dismiss()
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            // Set the height of the dialog here, if needed
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_height)
            )
        }
    }
}
