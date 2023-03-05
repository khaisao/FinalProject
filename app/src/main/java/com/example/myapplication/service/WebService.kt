package com.example.myapplication.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.example.myapplication.Constants
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.ByteBufferList
import com.koushikdutta.async.DataEmitter
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.http.body.AsyncHttpRequestBody
import com.koushikdutta.async.http.body.MultipartFormDataBody
import com.koushikdutta.async.http.body.MultipartFormDataBody.MultipartCallback
import com.koushikdutta.async.http.body.Part
import com.koushikdutta.async.http.body.UrlEncodedFormBody
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat

class WebService : Service() {
    var fileUploadHolder = FileUploadHolder()
    private val server: AsyncHttpServer = AsyncHttpServer()
    private val mAsyncServer: AsyncServer = AsyncServer()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (ACTION_START_WEB_SERVICE == action) {
            startServer()
        } else if (ACTION_STOP_WEB_SERVICE == action) {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
        mAsyncServer.stop()
    }

    private fun startServer() {

        //Lấy dữ liệu từ images, script, css
        server["/images/.*", { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            sendResources(
                request,
                response
            )
        }]
        server["/scripts/.*", { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            sendResources(
                request,
                response
            )
        }]
        server["/css/.*", { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            sendResources(
                request,
                response
            )
        }]
        //index page
        server["/", { request: AsyncHttpServerRequest?, response: AsyncHttpServerResponse ->
            try {
                response.send(indexContent)
            } catch (e: IOException) {
                e.printStackTrace()
                response.code(500).end()
            }
        }]
        //query upload list
        server["/files", { request: AsyncHttpServerRequest?, response: AsyncHttpServerResponse ->
            val array = JSONArray()
            val dir: File = Constants.DIR
            if (dir.exists() && dir.isDirectory) {
                val fileNames = dir.list()
                if (fileNames != null) {
                    for (fileName in fileNames) {
                        val file = File(dir, fileName)
                        if (file.exists() && file.isFile) {
                            try {
                                val jsonObject = JSONObject()
                                jsonObject.put("name", fileName)
                                val fileLen = file.length()
                                val df = DecimalFormat("0.00")
                                if (fileLen > 1024 * 1024) {
                                    jsonObject.put(
                                        "size",
                                        df.format((fileLen * 1f / 1024 / 1024).toDouble())
                                                + "MB"
                                    )
                                } else if (fileLen > 1024) {
                                    jsonObject.put(
                                        "size",
                                        df.format((fileLen * 1f / 1024).toDouble()) + "KB"
                                    )
                                } else {
                                    jsonObject.put("size", fileLen.toString() + "B")
                                }
                                array.put(jsonObject)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            response.send(array.toString())
        }]
        //delete
        server.post("/files/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            val body =
                request.getBody<AsyncHttpRequestBody<*>>() as UrlEncodedFormBody
            if ("delete".equals(body.get().getString("_method"), ignoreCase = true)) {
                var path: String? = request.path.replace("/files/", "")
                try {
                    path = URLDecoder.decode(path, "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
//                val file: File = File(Constants.DIR, path)
//                if (file.exists() && file.isFile && file.delete()) {
//                    RxBus.get().post(Constants.RxBusEventType.LOAD_BOOK_LIST, 0)
//                }
            }
            response.end()
        }
        //download
        server["/files/.*", { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            var path: String? = request.path.replace("/files/", "")
            try {
                path = URLDecoder.decode(path, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            val file: File = File(Constants.DIR, path)
            if (file.exists() && file.isFile) {
                try {
                    response.headers.add(
                        "Content-Disposition", "attachment;filename=" +
                                URLEncoder.encode(file.name, "utf-8")
                    )
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                response.sendFile(file)
            }
            response.code(404).send("Not found!")
        }]
        //upload
        server.post(
            "/files"
        ) { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            val body =
                request.getBody<AsyncHttpRequestBody<*>>() as MultipartFormDataBody
            body.multipartCallback = MultipartCallback { part: Part ->
                if (part.isFile) {
                    body.dataCallback =
                        DataCallback { emitter: DataEmitter?, bb: ByteBufferList ->
                            fileUploadHolder.write(bb.allByteArray)
                            bb.recycle()
                        }
                    Log.d("awwgawg", "startServer: " + part.isFile)
                } else {
                    if (body.dataCallback == null) {
                        body.dataCallback =
                            DataCallback { emitter: DataEmitter?, bb: ByteBufferList ->
                                try {
                                    val fileName = URLDecoder.decode(
                                        String(
                                            bb
                                                .allByteArray
                                        ), "UTF-8"
                                    )
                                    fileUploadHolder.setFileName(fileName)
                                    Log.d("awwgawg", "startServer: not a file ")
                                } catch (e: UnsupportedEncodingException) {
                                    e.printStackTrace()
                                }
                                bb.recycle()
                            }
                    }
                }
            }
            request.endCallback = CompletedCallback { e: Exception? ->
                fileUploadHolder.reset()
                response.end()
            }
        }

        server.listen(mAsyncServer, Constants.HTTP_PORT)
    }

    @get:Throws(IOException::class)
    private val indexContent: String
        private get() {
            var bInputStream: BufferedInputStream? = null
            return try {
                bInputStream = BufferedInputStream(assets.open("wifi/index.html"))
                val baos = ByteArrayOutputStream()
                var len = 0
                val tmp = ByteArray(10240)
                while (bInputStream.read(tmp).also { len = it } > 0) {
                    baos.write(tmp, 0, len)
                }
                baos.toString("utf-8")
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            } finally {
                if (bInputStream != null) {
                    try {
                        bInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun sendResources(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse) {
        try {
            var fullPath = request.path
            fullPath = fullPath.replace("%20", " ")
            var resourceName = fullPath
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1)
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"))
            }
            if (!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                response.setContentType(getContentTypeByResourceName(resourceName))
            }
            val bInputStream = BufferedInputStream(
                assets.open(
                    "wifi/" +
                            resourceName
                )
            )
            response.sendStream(bInputStream, bInputStream.available().toLong())
        } catch (e: IOException) {
            e.printStackTrace()
            response.code(404).end()
            return
        }
    }

    private fun getContentTypeByResourceName(resourceName: String): String {
        if (resourceName.endsWith(".css")) {
            return CSS_CONTENT_TYPE
        } else if (resourceName.endsWith(".js")) {
            return JS_CONTENT_TYPE
        } else if (resourceName.endsWith(".swf")) {
            return SWF_CONTENT_TYPE
        } else if (resourceName.endsWith(".png")) {
            return PNG_CONTENT_TYPE
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return JPG_CONTENT_TYPE
        } else if (resourceName.endsWith(".woff")) {
            return WOFF_CONTENT_TYPE
        } else if (resourceName.endsWith(".ttf")) {
            return TTF_CONTENT_TYPE
        } else if (resourceName.endsWith(".svg")) {
            return SVG_CONTENT_TYPE
        } else if (resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE
        } else if (resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE
        } else if (resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE
        }
        return ""
    }

    class FileUploadHolder {
        private var fileName: String? = null
        private var recievedFile: File? = null
        var fileOutPutStream: BufferedOutputStream? = null
            private set
        private var totalSize: Long = 0
        fun setFileName(fileName: String?) {
            this.fileName = fileName
            totalSize = 0
            if (!Constants.DIR.exists()) {
                Constants.DIR.mkdirs()
            }
            recievedFile = File(Constants.DIR, this.fileName)
            try {
                fileOutPutStream = BufferedOutputStream(FileOutputStream(recievedFile))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        fun reset() {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            fileOutPutStream = null
        }

        fun write(data: ByteArray) {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream!!.write(data)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            totalSize += data.size.toLong()
        }
    }

    companion object {
        const val ACTION_START_WEB_SERVICE = "me.pengtao.filetransfer.action" +
                ".START_WEB_SERVICE"
        const val ACTION_STOP_WEB_SERVICE = "me.pengtao.filetransfer.action" +
                ".STOP_WEB_SERVICE"
        private const val CSS_CONTENT_TYPE = "text/css;charset=utf-8"
        private const val JS_CONTENT_TYPE = "application/javascript"
        private const val PNG_CONTENT_TYPE = "application/x-png"
        private const val JPG_CONTENT_TYPE = "application/jpeg"
        private const val SWF_CONTENT_TYPE = "application/x-shockwave-flash"
        private const val WOFF_CONTENT_TYPE = "application/x-font-woff"
        private const val TTF_CONTENT_TYPE = "application/x-font-truetype"
        private const val SVG_CONTENT_TYPE = "image/svg+xml"
        private const val EOT_CONTENT_TYPE = "image/vnd.ms-fontobject"
        private const val MP3_CONTENT_TYPE = "audio/mp3"
        private const val MP4_CONTENT_TYPE = "video/mpeg4"
        fun start(context: Context) {
            val intent = Intent(context, WebService::class.java)
            intent.action = ACTION_START_WEB_SERVICE
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, WebService::class.java)
            intent.action = ACTION_STOP_WEB_SERVICE
            context.startService(intent)
        }
    }
}