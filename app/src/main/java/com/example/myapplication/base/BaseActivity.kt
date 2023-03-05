package com.example.myapplication.base

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.myapplication.Constants
import com.example.myapplication.base.dialog.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

abstract class BaseActivity<BD : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var binding: BD
    private lateinit var viewModel: VM

    private var lastTimeClick: Long = 0

    @get: LayoutRes
    abstract val layoutId: Int
    abstract fun getVM(): VM
    private var isHandleDisPatch: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(WeakReference(this).get()!!, layoutId)
        binding.lifecycleOwner = this

        viewModel = getVM()
    }

    override fun onDestroy() {
        binding.unbind()
        LoadingDialog.getInstance(this)?.destroyLoadingDialog()
        super.onDestroy()
    }

    fun showLoading() {
        LoadingDialog.getInstance(this)?.show()
    }

    fun hiddenLoading() {
        LoadingDialog.getInstance(this)?.hidden()
    }

    // click able
    val isDoubleClick: Boolean
        get() {
            val timeNow = System.currentTimeMillis()
            if (timeNow - lastTimeClick >= Constants.DURATION_TIME_CLICKABLE) {
                // click able
                lastTimeClick = timeNow
                return false
            }
            return true
        }

    /**
     * Close SoftKeyboard when user click out of EditText
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isHandleDisPatch) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun setHandleDispathTouch(enable: Boolean) {
        this.isHandleDisPatch = enable
    }

}
