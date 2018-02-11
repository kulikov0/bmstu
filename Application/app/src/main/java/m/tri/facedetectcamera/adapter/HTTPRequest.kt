package m.tri.facedetectcamera.adapter

import android.graphics.Bitmap
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean


class HTTPRequest {

    private val executionDispatcherExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val executionContextExecutor: ExecutorService = Executors.newFixedThreadPool(
            Math.max(Runtime.getRuntime().availableProcessors() * 2 - 1, 2)
    )

    private val isRunning: AtomicBoolean = AtomicBoolean(false)

    private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { (HttpLoggingInterceptor.Level.BODY) })
            .build()

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://mydomain.com")
            .validateEagerly(true)
            .client(client)
            .build()

    private val api: Api = retrofit.create(Api::class.java)

    private val queue: BlockingQueue<Bitmap> = LinkedBlockingQueue()

    fun offerToSendQueue(bitmap: Bitmap) {
        queue.offer(bitmap)
    }

    fun start() {
        if(isRunning.compareAndSet(false, true)) {
            executionDispatcherExecutor.execute {
                while(isRunning.get()) {
                    try {
                        val bmp = queue.poll(5, TimeUnit.SECONDS)
                        executionContextExecutor.execute({ sendOctetStream(bmp) })
                    } catch(e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun stop() {
        isRunning.set(false)
    }

    private fun sendOctetStream(bmp: Bitmap): Response<ResponseBody>
        = api.sendOctetStream("jpeg", bmp.toOctetStream())
            .execute()

    private fun sendMultipart(bmp: Bitmap): Response<ResponseBody>
        = api.sendMultipart("jpeg".toPart(), bmp.toPart())
            .execute()

    private fun String.toPart(): RequestBody
            = RequestBody.create(MediaType.parse("text/plain"), this)

    private fun Bitmap.toOctetStream(): RequestBody {
        val baos = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return RequestBody.create(
                MediaType.parse("application/octet-stream"),
                baos.toByteArray()
        )
    }

    private fun Bitmap.toPart(): RequestBody {
        val baos = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return RequestBody.create(
                MediaType.parse("image/jpeg"),
                baos.toByteArray()
        )
    }

    private interface Api {

        @POST("/some/endpoint")
        fun sendOctetStream(
                @Query("extension") extension: String,
                @Body octetStream: RequestBody
        ): Call<ResponseBody>

        @Multipart
        @POST("/some/endpoint")
        fun sendMultipart(
                @Part("extension") extension: RequestBody,
                @Part("image") image: RequestBody
        ): Call<ResponseBody>
    }
}