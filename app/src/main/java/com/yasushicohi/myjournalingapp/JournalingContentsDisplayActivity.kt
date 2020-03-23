package com.yasushicohi.myjournalingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_journaling_contents_display.*

class JournalingContentsDisplayActivity : AppCompatActivity() {

    companion object {
        const val KEY_CONTENT_ID = "key_content_id"
    }

    lateinit var journalingContent : JournalingContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journaling_contents_display)
        findJournalingContentById(intent.getIntExtra(KEY_CONTENT_ID,0))

        back_from_content_display_button.setOnClickListener{
            finish()
        }

        edit_content_display_button.setOnClickListener{
            startActivityForResult(
                Intent(this, JournalingContentEditActivity::class.java).putExtra(KEY_CONTENT_ID, journalingContent.journalingContentId)
                , journalingContent.journalingContentId
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == journalingContent.journalingContentId){
                findJournalingContentById(requestCode)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun findJournalingContentById(id: Int) {
        var d: Disposable = AppDatabaseSingleton.getDatabase(this).contentDao()
            .findById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                journalingContent = it
                journaling_content_display_title.text = it.jornalingContentTitle
                journaling_content_display_text.text = it.journalingContentText
            }, {
                // do nothing
            })
    }
}
