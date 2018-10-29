package ng.lekki.funnytok.Infinite

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.grid_item.view.*
import ng.lekki.funnytok.GlideApp
import ng.lekki.funnytok.R
import ng.lekki.funnytok.VideoPlayer

data class adapterModel(val shortcode:String,val display:String)
class Radapter(var context: Context, var lists: ArrayList<adapterModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

        holder.itemView.setOnClickListener {

            val sendIntent = Intent(context,VideoPlayer::class.java)
            sendIntent.putExtra("shortcode",lists[position].shortcode)
            context.startActivity(sendIntent)



        }




    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: adapterModel) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_place)
            GlideApp.with(itemView.context).setDefaultRequestOptions(requestOptions).load(_list.display).into(itemView.video_thumb)


                itemView.indie.visibility = View.VISIBLE

        }
    }
}