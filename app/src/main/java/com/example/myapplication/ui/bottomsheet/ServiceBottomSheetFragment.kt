package com.example.myapplication.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBootomSheetServiceBinding
import com.example.myapplication.util.WifiUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ServiceBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBootomSheetServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBootomSheetServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ip = WifiUtils().getDeviceIdAddress()
        val address = java.lang.String.format(
            requireContext().getString(R.string.http_address),
            ip,
            Constants.HTTP_PORT
        )

        binding.tvAddress.text = address

        binding.llOk.setOnClickListener {
            dismiss()
        }
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
