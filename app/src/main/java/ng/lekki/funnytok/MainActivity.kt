package ng.lekki.funnytok

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import org.jsoup.Jsoup
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.VolleyLog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.breuhteam.apprate.AppRate
import kotlinx.android.synthetic.main.grid_item.view.*
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tag_row.view.*
import ng.lekki.funnytok.Infinite.EndlessRecyclerOnScrollListener
import ng.lekki.funnytok.Infinite.Radapter
import ng.lekki.funnytok.Infinite.adapterModel
import ng.lekki.funnytok.KeppBox.Downloaded


class MainActivity : AppCompatActivity() {

    var lit:Lit? = null
    var csrftoken = ""
    var rhxgis = ""
    var xgram = ""
    var hashment = ""
    var endCursor = ""
    var userResponse = ""
    var hasNext = false
    val url2 = "https://www.instagram.com/explore/tags/tiktok/?__a=1"
    val agent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36 "
    var tagChain:ArrayList<tagChain>? = null

    var adapterModel:ArrayList<adapterModel>? = null
    var radapter: Radapter? = null
    var tagAdaptar:EasyAdapterz? = null

    var isLoading = false
    var gridLayoutManager:GridLayoutManager? = null
    var taje = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lit = Lit()
        setSupportActionBar(tulz)
        tulz.setTitleTextColor(ContextCompat.getColor(this@MainActivity,R.color.colorWhite))

        AppRate.app_launched(this@MainActivity, packageName,0,4)


        shimmer.startShimmer()
        taje = "tiktokfunny"
        relatedTags("tiktok")

        loadTag("https://www.instagram.com/explore/tags/$taje/","$taje")

        gridLayoutManager =  GridLayoutManager(this@MainActivity,3)


        recycler.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (hasNext){

                    more(endCursor)

                    radapter!!.notifyDataSetChanged()

                }
            }


        })








    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolx,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){

            R.id.menu_saves ->{


                startActivity(Intent(this@MainActivity,Downloaded::class.java))
            }


            R.id.menu_rate ->{

                AppRate.app_launched(this, packageName)


            }


            R.id.menu_refresh ->{

                taje = "tiktokfunny"
                loadTag("https://www.instagram.com/explore/tags/$taje/",taje)

            }
        }

        return super.onOptionsItemSelected(item)
    }





    fun loadTag(urc:String,tagID:String) {
        var bing = ""
        adapterModel = ArrayList<adapterModel>()
        shimmer.startShimmer()
        shimmer.visibility = View.VISIBLE
        recycler.adapter = null
        launch(UI) {
            val result = async(CommonPool) {

                try {

                    val doc = Jsoup.connect(urc)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .timeout(100000)
                            .get()

                    val elements = doc.getElementsByTag("script")

                    for (element in elements) {


                        if (element.data().contains("window._sharedData =")){

                            val fulltext = element.data()
                            userResponse = fulltext.replace("window._sharedData =","")



                        }
                    }


                    val user_json =  JSONObject(userResponse)
                    csrftoken = user_json.getJSONObject("config").getString("csrf_token")
                    rhxgis =  user_json.getString("rhx_gis")
                    val stats = user_json.getJSONObject("entry_data").getJSONArray("TagPage").getJSONObject(0).getJSONObject("graphql")
                    val timeline_post = stats.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_media")
                    endCursor = timeline_post.getJSONObject("page_info").getString("end_cursor")




                    hashment =  rhxgis+":{\"tag_name\":\"$taje\",\"show_ranked\":true,\"first\":50,\"after\":\"$endCursor\"}"
                    xgram = Concord.getMd5Key(hashment)









                }catch (e:Exception){


                    bing = e.message.toString()
                }




            }.await()

            gettty()


        }
    }




    fun gettty(){

        val url = "https://www.instagram.com/graphql/query/?query_hash=f92f56d47dc7a55b606908374b43a314&variables={\"tag_name\":\"$taje\",\"show_ranked\":true,\"first\":50,\"after\":\"$endCursor\"}"
        val queue = Volley.newRequestQueue(applicationContext)

        val req = object : JsonObjectRequest(Request.Method.GET, url,
                null, Response.Listener { response ->

            val pageObject =  JSONObject(response.toString())
            val pageData = pageObject.getJSONObject("data")
            val pageEdge = pageData.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_ranked_media")
            val count =  pageEdge.getInt("count")
            endCursor = pageEdge.getJSONObject("page_info").getString("end_cursor")
            hasNext = pageEdge.getJSONObject("page_info").getBoolean("has_next_page")

            val postArray = pageEdge.getJSONArray("edges")

            if (postArray != null &&  postArray.length() > 0){


                for (i in 0 until postArray.length()){

                    val jsonx =  postArray.getJSONObject(i)
                    val type = jsonx.getJSONObject("node").getString("__typename")

                    if (type.contains("GraphVideo")){

                        val displayURL = jsonx.getJSONObject("node").getString("display_url")
                        val shortcode = jsonx.getJSONObject("node").getString("shortcode")


                        adapterModel!!.add(adapterModel(shortcode,displayURL))
                    }


                    radapter = Radapter(this@MainActivity, adapterModel!!)


                    recycler.setHasFixedSize(true)
                    recycler.adapter = radapter
                    recycler.layoutManager = gridLayoutManager
                    radapter!!.notifyDataSetChanged()
                    shimmer.stopShimmer()
                    shimmer.visibility= View.GONE
                    tag_recycler.visibility = View.VISIBLE


                }

            }else{


            }





        }, Response.ErrorListener { error ->


            VolleyLog.d("Error", "Error: " + error.message)
            Toast.makeText(this@MainActivity, ""+error.message, Toast.LENGTH_SHORT).show()


        }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["referer"] = "https://www.instagram.com/explore/tags/tiktok/"
                headers["user-agent"] = agent
                headers["x-instagram-gis"] = "$xgram"
                headers["x-requested-with"] = "XMLHttpRequest"

                try {
                    headers.putAll(super.getHeaders())
                } catch (authFailureError: AuthFailureError) {
                    authFailureError.printStackTrace()
                }

                return headers
            }
        }

        queue.add(req)

    }





    //Load more videos




    fun more(endID:String){

        isLoading =true

        val url = "https://www.instagram.com/graphql/query/?query_hash=f92f56d47dc7a55b606908374b43a314&variables={\"tag_name\":\"$taje\",\"show_ranked\":true,\"first\":50,\"after\":\"$endID\"}"
        val queue = Volley.newRequestQueue(applicationContext)

        val req = object : JsonObjectRequest(Request.Method.GET, url,
                null, Response.Listener { response ->

            val pageObject =  JSONObject(response.toString())
            val pageData = pageObject.getJSONObject("data")
            val pageEdge = pageData.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_ranked_media")
            val count =  pageEdge.getInt("count")
            endCursor = pageEdge.getJSONObject("page_info").getString("end_cursor")
            hasNext = pageEdge.getJSONObject("page_info").getBoolean("has_next_page")

            val postArray = pageEdge.getJSONArray("edges")

            if (postArray != null &&  postArray.length() > 0){


                for (i in 0 until postArray.length()){

                    val jsonx =  postArray.getJSONObject(i)
                    val type = jsonx.getJSONObject("node").getString("__typename")

                    if (type.contains("GraphVideo")){

                        val displayURL = jsonx.getJSONObject("node").getString("display_url")
                        val shortcode = jsonx.getJSONObject("node").getString("shortcode")

                        adapterModel!!.add(adapterModel(shortcode,displayURL))

                    }



                   radapter!!.notifyDataSetChanged()

                    isLoading = false


                }

            }else{


            }





        }, Response.ErrorListener { error ->


            VolleyLog.d("Error", "Error: " + error.message)
            Toast.makeText(this@MainActivity, ""+error.message, Toast.LENGTH_SHORT).show()
            isLoading = false


        }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["referer"] = "https://www.instagram.com/explore/tags/tiktok/"
                headers["user-agent"] = agent
                headers["x-instagram-gis"] = "$xgram"
                headers["x-requested-with"] = "XMLHttpRequest"

                try {
                    headers.putAll(super.getHeaders())
                } catch (authFailureError: AuthFailureError) {
                    authFailureError.printStackTrace()
                }

                return headers
            }
        }

        queue.add(req)

    }







    //load related tag!!







    fun relatedTags(tagID:String){

        tagChain = ArrayList<tagChain>()
        isLoading =true

        val url = "https://www.instagram.com/web/search/topsearch/?context=blended&query=%23$tagID"
        val queue = Volley.newRequestQueue(applicationContext)

        val req = object : JsonObjectRequest(Request.Method.GET, url,
                null, Response.Listener { response ->

            val pageObject =  JSONObject(response.toString())
            val hash = pageObject.getJSONArray("hashtags")


            for (i in 0 until hash.length()){

                val jsonobj = hash.getJSONObject(i)
                val name = jsonobj.getJSONObject("hashtag").getString("name")
                val id =   jsonobj.getJSONObject("hashtag").getInt("media_count").toString()

                 tagChain!!.add(tagChain(name,id))
            }

            tagAdaptar = EasyAdapterz(itemLayoutRes = R.layout.tag_row,
                    itemCount = tagChain!!.size,
                    binder = {


                        val tagData = tagChain!![it.adapterPosition].name
                        it.itemView.hash_text.text = "#$tagData"


                        it.itemView.hash_text.setOnClickListener {


                            val tagx = it.hash_text.text.toString()
                            taje = tagx.replace("#","")
                            loadTag("https://www.instagram.com/explore/tags/$taje/","$taje")
                            tulz.title = "#$taje"

                        }



                    })


            tag_recycler.adapter = tagAdaptar
            tag_recycler.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
            tagAdaptar!!.notifyDataSetChanged()




        }, Response.ErrorListener { error ->


            VolleyLog.d("Error", "Error: " + error.message)
            Toast.makeText(this@MainActivity, ""+error.message, Toast.LENGTH_SHORT).show()
            isLoading = false


        }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["referer"] = "https://www.instagram.com/explore/tags/tiktok/"


                try {
                    headers.putAll(super.getHeaders())
                } catch (authFailureError: AuthFailureError) {
                    authFailureError.printStackTrace()
                }

                return headers
            }
        }

        queue.add(req)

    }




}


data class tagChain(val name:String, val mediaCount:String)