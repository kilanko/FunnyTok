package ng.lekki.funnytok

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.esafirm.rxdownloader.RxDownloader
import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.video_player.*
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class VideoPlayer : AppCompatActivity(), BetterVideoCallback, android.support.v7.widget.Toolbar.OnMenuItemClickListener , AutoPermissionsListener {


    var videoSource = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_player)







        val shotInts = intent.extras
        val shortcode = shotInts.getString("shortcode")
        loadVideo(shortcode)




    }




    fun loadVideo(shorty:String){

        val url = "https://www.instagram.com/p/$shorty?__a=1"
        val queue = Volley.newRequestQueue(applicationContext)

        val req = object : JsonObjectRequest(Request.Method.GET, url,
                null, Response.Listener { response ->

            val pageResponse =  JSONObject(response.toString())
            videoSource = pageResponse.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
            video.setCallback(this)

            val uri = Uri.parse(videoSource)
            video.setSource(uri)
            video.toolbar.inflateMenu(R.menu.save_menu)
            video.toolbar.title = getString(R.string.player)
            video.toolbar.setOnMenuItemClickListener(this)

        }, Response.ErrorListener { error ->


            VolleyLog.d("Error", "Error: " + error.message)
            Toast.makeText(this@VideoPlayer, ""+error.message, Toast.LENGTH_SHORT).show()


        }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["referer"] = "https://www.instagram.com/explore/tags/tiktok/"

                return headers
            }
        }

        queue.add(req)

    }


    override fun onPrepared(player: BetterVideoPlayer?) {


    }

    override fun onStarted(player: BetterVideoPlayer?) {


    }

    override fun onCompletion(player: BetterVideoPlayer?) {


    }

    override fun onBuffering(percent: Int) {


    }

    override fun onPreparing(player: BetterVideoPlayer?) {


    }

    override fun onError(player: BetterVideoPlayer?, e: Exception?) {


    }

    override fun onToggleControls(player: BetterVideoPlayer?, isShowing: Boolean) {


    }

    override fun onPaused(player: BetterVideoPlayer?) {


    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {

        when(item!!.itemId){

            R.id.download_id ->{


                if (checkPermission()) {

                    downloader(videoSource)


                } else {

                    AutoPermissions.loadActivityPermissions(this@VideoPlayer, 1)


                }



            }
        }

        return false
    }





    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.parsePermissions(this@VideoPlayer, requestCode, permissions, this)
    }





    override fun onDenied(requestCode: Int, permissions: Array<String>) {


    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {

        downloader(videoSource)


    }








    fun downloader(downloadURL:String){

        Toast.makeText(applicationContext,""+getString(R.string.start),Toast.LENGTH_LONG).show()
        val rxDownloader = RxDownloader(applicationContext)
        val desc = getString(R.string.saving)
        val timeStamp =  System.currentTimeMillis()
        val file = "tiktok_"+"_"+timeStamp
        val ext = "mp4"
        val name = file + "." + ext
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "tokfunny")
        if (!dex.exists())
            dex.mkdirs()

        val Download_Uri = Uri.parse(downloadURL)
        val downloadManager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =  DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true)
        request.setTitle( "♫ $name")
        request.setVisibleInDownloadsUi(true)
        request.setDescription(desc)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/tokfunny",  name)

        rxDownloader.download(request).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {


                    }

                    override fun onNext(t: String) {


                    }

                    override fun onSubscribe(d: Disposable) {


                    }


                })

    }

}
