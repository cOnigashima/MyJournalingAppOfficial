package com.yasushicohi.myjournalingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class JournalingContentsListAdapter(private val context: Context, private val journalingList: MutableList<JournalingContent>) :
    RecyclerView.Adapter<JournalingContentsListAdapter.JournalingContentViewHolder>() {

    lateinit var listener: OnItemClickListener

    class JournalingContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentTitleTextView: TextView = view.findViewById(R.id.journaling_item_title)
        val contentSentenceTextView :TextView = view.findViewById(R.id.journaling_item_sentence)
        val dateTextView: TextView = view.findViewById(R.id.journaling_item_create_date)
        val cardView :CardView = view.findViewById(R.id.list_card_view_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalingContentViewHolder =
        JournalingContentViewHolder(LayoutInflater.from(context).inflate(R.layout.journaling_list_contents_item, parent, false))

    override fun getItemCount(): Int = journalingList.size

    override fun onBindViewHolder(holder: JournalingContentViewHolder, position: Int) {
        holder.dateTextView.text = journalingList[position].createDate
        holder.contentTitleTextView.text = journalingList[position].jornalingContentTitle
        holder.contentSentenceTextView.text = journalingList[position].journalingContentText

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position, journalingList[position])
        }

        holder.cardView.radius = 16F

    }

    fun getContentId(position: Int): Int {
        return journalingList.get(position).journalingContentId
    }

    fun removeItem(position: Int){
        journalingList.remove(journalingList.get(position))
    }


    //インターフェースの作成
    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, journalingContent: JournalingContent)
    }

    // リスナー
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


}