package com.example.myapplication.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import com.example.myapplication.Constants
import com.example.myapplication.R
import org.greenrobot.eventbus.EventBus
import java.io.*
import java.util.*

object FileUtils {
    fun getFileUri(context: Context, filePath: String?): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(
            context, context.packageName + "" +
                    ".tlutransfer", file
        )
    }

    fun openFile(filePath: String?, context: Context): Boolean {
        val fileType = getFileType(filePath)
        val file = File(filePath)
        if (file.isFile) {
            var intent: Intent? = null
            val contentUri = getFileUri(context, filePath)
            when (fileType) {
                FileType.TYPE_IMAGE -> intent = getImageFileIntent(contentUri)
                FileType.TYPE_AUDIO -> intent = getAudioFileIntent(contentUri)
                FileType.TYPE_VIDEO -> intent = getVideoFileIntent(contentUri)
                FileType.TYPE_WEB -> intent = getHtmlFileIntent(contentUri)
                FileType.TYPE_TEXT -> intent = getTextFileIntent(contentUri)
                FileType.TYPE_EXCEL -> intent = getExcelFileIntent(contentUri)
                FileType.TYPE_WORD -> intent = getWordFileIntent(contentUri)
                FileType.TYPE_PPT -> intent = getPPTFileIntent(contentUri)
                FileType.TYPE_PDF -> intent = getPdfFileIntent(contentUri)
                FileType.TYPE_PACKAGE, FileType.TYPE_APK -> intent = getApkFileIntent(context, file)
                else -> AlertDialog.Builder(context)
                    .setMessage(R.string.no_program_open_it)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
            if (intent != null) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
                return true
            }
        }
        return false
    }

    fun getFileType(filePath: String?): Int {
        val file = File(filePath)
        val fileName = file.name
        for (i in 0 until FileType.FileTypes.size) {
            val j = checkStringEnds(fileName, FileType.FileTypes[i])
            if (j == -1) {
                continue
            }
            return FileType.TypeStart[i]
        }
        return FileType.TYPE_UNKNOWN
    }

    fun deleteFilesInFolder(folderPath: String) {
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }

    private fun checkStringEnds(item: String, array: Array<String>): Int {
        for (i in array.indices) {
            if (item.lowercase(Locale.getDefault()).endsWith(array[i])) {
                return i
            }
        }
        return -1
    }

    fun getShareType(path: String?): String {
        val fileType = getFileType(path)
        return when (fileType) {
            FileType.TYPE_IMAGE -> "image/*"
            FileType.TYPE_AUDIO -> "audio/*"
            FileType.TYPE_VIDEO -> "video/*"
            FileType.TYPE_WEB, FileType.TYPE_TEXT -> "text/*"
            FileType.TYPE_EXCEL, FileType.TYPE_WORD, FileType.TYPE_PPT, FileType.TYPE_PDF, FileType.TYPE_PACKAGE, FileType.TYPE_APK -> "application/*"
            else -> "*/*"
        }
    }

    @Throws(IOException::class)
    fun copyFile(`in`: InputStream, targetLocation: String?) {
        val out: OutputStream = FileOutputStream(targetLocation)
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }

    fun copyFile(absolutePath: String): Boolean {
        val filename: String = absolutePath.substring(absolutePath.lastIndexOf("/") + 1)
        Log.d("lkasgjkasg", filename.toString())
        val srcFile = File(
            absolutePath
        )
        val destFile = File(
            Constants.DIR_PATH.toString() + "/" + filename
        )
        try {
            srcFile.copyTo(
                target = destFile,
                overwrite = false,
                bufferSize = DEFAULT_BUFFER_SIZE
            )
          return  destFile.exists() && destFile.readBytes().contentEquals(srcFile.readBytes())

        } catch (e: Exception) {
            Log.e("sagasgasfasg", e.toString())
           return false
        }
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    private fun getHtmlFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.setDataAndType(uri, "text/html")
        return intent
    }

    private fun getImageFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "image/*")
        return intent
    }

    private fun getPdfFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/pdf")
        return intent
    }

    private fun getTextFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "text/plain")
        return intent
    }

    private fun getAudioFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        intent.setDataAndType(uri, "audio/*")
        return intent
    }

    private fun getVideoFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        intent.setDataAndType(uri, "video/*")
        return intent
    }

    private fun getWordFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/msword")
        return intent
    }

    private fun getExcelFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/vnd.ms-excel")
        return intent
    }

    private fun getPPTFileIntent(uri: Uri): Intent {
        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        return intent
    }

    private fun getApkFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        //兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(
                context, context.packageName + "" +
                        ".fileprovider", file
            )
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return intent
    }

    fun sortWithLastModified(files: Array<File>?) {
        Arrays.sort(files) { f1: File, f2: File ->
            val diff = f1.lastModified() - f2.lastModified()
            if (diff > 0) {
                return@sort -1
            } else if (diff == 0L) {
                return@sort 0
            } else {
                return@sort 1
            }
        }
    }

    /**
     * Writes a byte array to a file creating the file if it does not exist.
     *
     * @param file  the file to write to
     * @param data  the content to write to the file
     * @param append if `true`, then bytes will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since IO 2.1
     */
    @Throws(IOException::class)
    fun writeByteArrayToFile(file: File, data: ByteArray?, append: Boolean) {
        var out: OutputStream? = null
        try {
            out = openOutputStream(file, append)
            out.write(data)
            out.close() // don't swallow close Exception if copy completes normally
        } finally {
            out?.close()
        }
    }

    @Throws(IOException::class)
    private fun openOutputStream(file: File, append: Boolean): FileOutputStream {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (!file.canWrite()) {
                throw IOException("File '$file' cannot be written to")
            }
        } else {
            val parent = file.parentFile
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory) {
                    throw IOException("Directory '$parent' could not be created")
                }
            }
        }
        return FileOutputStream(file, append)
    }
}