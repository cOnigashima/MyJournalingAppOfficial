package com.yasushicohi.myjournalingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yasushicohi.myjournalingapp.JournalingContentsDisplayActivity.Companion.KEY_CONTENT_ID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_journaling_contents_list_recycler.*

class JournalingContentLIstActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journaling_contents_list_recycler)

        val recyclerView: RecyclerView = findViewById(R.id.journaling_list_container_recycler_view)

        var d: Disposable = AppDatabaseSingleton.getDatabase(this).contentDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toast.makeText(this, "拾ってきたよ", Toast.LENGTH_SHORT).show()
                    // SQLでやれ
                    it.reverse()
                    //RecyclerViewにAdapterとLayoutManagerを設定
                    recyclerView.also { recyclerView: RecyclerView ->

                        val adapter = JournalingContentsListAdapter(this, it)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        setClickListenerJob(adapter)
                    }
                }
                , {
                    Toast.makeText(this, "エラーだよ", Toast.LENGTH_LONG).show()
                })


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.let {
                    //　データベース上は消える
                    val positon = viewHolder.adapterPosition

                    //adapterのidと、データベースのidを一致させて削除する必要がある。adapterのリストも消す
                    val contentId = (recyclerView.adapter as JournalingContentsListAdapter).getContentId(positon)
                    deleteContentItem(contentId)
                    (recyclerView.adapter as JournalingContentsListAdapter).removeItem(positon)

                    recyclerView.adapter?.notifyItemRemoved(positon)
                }
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        create_new_journaling_content_button.setOnClickListener{
            startActivity(Intent(this,JournalingContentEditActivity::class.java))
        }
    }

    fun deleteContentItem(itemId: Int){
        var d:Disposable = AppDatabaseSingleton.getDatabase(this).contentDao().deleteById(itemId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
                },
                {
                    Toast.makeText(this, "エラーだよ", Toast.LENGTH_SHORT).show()
                })
    }

    fun setClickListenerJob(adapter: JournalingContentsListAdapter) {
        adapter.setOnItemClickListener(object : JournalingContentsListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, journalingContent: JournalingContent) {
                startActivity(
                    Intent(this@JournalingContentLIstActivity, JournalingContentsDisplayActivity::class.java
                    ).putExtra(KEY_CONTENT_ID, journalingContent.journalingContentId)
                )
            }
        })
    }
}
