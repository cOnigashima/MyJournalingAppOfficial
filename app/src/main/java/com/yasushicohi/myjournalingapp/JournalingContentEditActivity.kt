package com.yasushicohi.myjournalingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.yasushicohi.myjournalingapp.JournalingContentsDisplayActivity.Companion.KEY_CONTENT_ID
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_journaling_content_edit.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class JournalingContentEditActivity : AppCompatActivity() {

    companion object {
        const val TEN_MINUTE = 600
    }

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

        findViewById<Button>(R.id.time_count_button).
            setOnClickListener {
                startCountDown()
        }


        content_date.text = creationDate()


        fillInTextView(mContentIdForEdit)

    }


    fun startCountDown(){

        val timerDisplay = findViewById<TextView>(R.id.timer_display)
        var d : Disposable
        // TODO 一回ボタンを押して開始したら押せないようにするか、停止する。

        val observable : Observable<Long>  =  Observable.interval(1, TimeUnit.SECONDS)
        // d は必要　"The result of subscribe is not used"
         d = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Function<Long, Long> {
                return@Function TEN_MINUTE - 1 - it //itはどんどんカウントを増していく
            })

            .subscribe{
                // もうKotlinがわからない
                // itがいわゆるonNextで返される値。

                // 分数で　format　
                //　これformat使う方がええな。
                //timerDisplay.text = ((it/60).toString() + " : " + (it%60).toString())
                // format("" , intかLongじゃないとダメ。多分Long , )
                // はぁ、楽しかった。。。
                //　ここは、onNextでござる。
                timerDisplay.text = String.format(" %02d : %02d ", (it / 60), (it % 60))

                // TimeUnit.SECONDS.toMinutes(
                // onCompleteとかはないから？ってとやろう？？
                //TODO 0になったら、トーストを出して、停止する。
                // 分数を選べるようにする。
            }


//                     Javaの場合。これで一生頑張っていた、JavaだったらこれでOK。
//                     new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        onTick(aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        onFinish();
//                    }
//                });


    }

    fun saveContent(){
        if(mContentIdForEdit != 0) mContentId = mContentIdForEdit.toLong()

        val inputTitleText = findViewById<TextView>(R.id.journaling_content_title).text.toString()
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
