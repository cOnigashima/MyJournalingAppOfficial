package com.yasushicohi.myjournalingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.yasushicohi.myjournalingapp.JournalingContentsDisplayActivity.Companion.KEY_CONTENT_ID
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_journaling_content_edit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class JournalingContentEditActivity : AppCompatActivity() {

    var mContentId :Long = 0
    var mContentIdForEdit :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journaling_content_edit)

        mContentIdForEdit = intent.getIntExtra(KEY_CONTENT_ID,0)


        edit_toolbar.setNavigationOnClickListener {
            //　端末バックキーもoverrideをした方がいい。
            //　保存するかどうかを聞いた方がいい。
                // カウントダウン中なら、なんかしらポップアップは出るべきだけど。
                    //　機能がどんどん増えていく。。。
                //　onTextChangedで変わっていなければ、保存しなくていいのでは？？？

            if (mContentIdForEdit == 0) {
                saveContent()
                startActivity(Intent(this, JournalingContentLIstActivity::class.java))
            } else {
                saveContent()
                setResult(Activity.RESULT_OK,Intent(this, JournalingContentsDisplayActivity::class.java))
                finish()
            }
        }

        subject_list_button.setOnClickListener {
            selectSubjectsDialog()
        }

        content_date.text = creationDate()
        fillInTextView(mContentIdForEdit)

    }

    fun selectSubjectsDialog(){
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            val itemList = getStringArrayItems()
            val items = arrayOf(itemList[0], itemList[1], itemList[2])
            val defaultItem = -1 // デフォルトでチェックされているアイテム
            builder.apply {
                var checkedItem  = journaling_content_title.text.toString()
                setSingleChoiceItems(items,defaultItem) { dialog, id ->
                    // do nothing
                    checkedItem = items[id].toString()
                }

                setTitle("お題を選んでください")

                setPositiveButton("選択") { dialog, id ->
                    journaling_content_title.setText(checkedItem)
                }

                setNegativeButton("キャンセル") { dialog, id ->
                    dialog.dismiss()
                }

                // 全部のお題から選べるようにする。
                // setNeutralButton("全て見る") {dialog, id -> }
            }
            // Set other dialog properties
            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }

    fun getStringArrayItems() :ArrayList<CharSequence>{
        val array = resources.getStringArray(R.array.journaling_string_array).toMutableList()
        val arrayList = ArrayList<CharSequence>()
        var randomInt : Int

        for (i in 1..3){
            randomInt = Random.nextInt(array.size)
            arrayList.add(array[randomInt])
            array.removeAt(randomInt)
        }

        return arrayList
    }

    fun saveContent(){
        if(mContentIdForEdit != 0) mContentId = mContentIdForEdit.toLong()

        val inputTitleText = findViewById<EditText>(R.id.journaling_content_title).text.toString()
        val inputEditText = findViewById<EditText>(R.id.journaling_content_edit_detail_text).text.toString()

        val single: Single<Long> =
            AppDatabaseSingleton.getDatabase(this@JournalingContentEditActivity).contentDao().save(
                JournalingContent(mContentId.toInt(), inputTitleText, inputEditText, creationDate())
            )

        val d: Disposable = single
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("subscribe", "success")
                    Toast.makeText(this@JournalingContentEditActivity, "保存しました", Toast.LENGTH_LONG).show()
                    mContentId = it
                },
                {
                    Toast.makeText(
                        this@JournalingContentEditActivity,
                        "何かしらエラーが起きました" + it.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
    }

    fun fillInTextView(id:Int){
        if(id == 0) return

        var d: Disposable = AppDatabaseSingleton.getDatabase(this).contentDao()
            .findById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                journaling_content_edit_detail_text.setText(it.journalingContentText)
            }, {
                // do nothing
            })
    }

    fun creationDate() = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPANESE).format(Date())

}
