package com.yasushicohi.myjournalingapp

import android.content.Context
import android.transition.*
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.coutdown_timer_button_view.view.*
import java.util.concurrent.TimeUnit

class CountDownTimerButtonView :ConstraintLayout{

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    companion object {
        var remainedTime = 0
        var isSettingMinutes = false
    }

    var compositeDisposable = CompositeDisposable()



    init {
        // 初期化処理
        View.inflate(context, R.layout.coutdown_timer_button_view, this)

        count_down_timer_button.setOnClickListener {
            if(timer_start_button.isChecked) return@setOnClickListener
            val explode :Transition = Explode()
            TransitionManager.beginDelayedTransition(this,explode)

            if(isSettingMinutes){
                minutes_setting.visibility = GONE
                val setSeconds = minutes_setting.value * 60
                remainedTime = 0
                count_down_timer_button.text = String.format(" %02d : %02d  ▼ ", (setSeconds / 60), (setSeconds % 60))
                isSettingMinutes = false
                return@setOnClickListener
            }
            minutes_setting.visibility = VISIBLE
            count_down_timer_button.text = "時間を設定してください　▲　"
            isSettingMinutes = true
        }

        minutes_setting.minValue = 1
        minutes_setting.maxValue = 30
        minutes_setting.value = 10


        timer_start_button.setOnCheckedChangeListener{ _, isChecked ->
            if(isSettingMinutes) {
                timer_start_button.isChecked = !isChecked
                return@setOnCheckedChangeListener
            }

            if(isChecked){
                startCountDown()
                return@setOnCheckedChangeListener
            }
            compositeDisposable.clear()
        }
    }



    fun startCountDown(){

        val settingTime = if (remainedTime == 0) minutes_setting.value * 60 else remainedTime

        val observable : Observable<Long> =  Observable.interval(1, TimeUnit.SECONDS)
        // d は必要　"The result of subscribe is not used"
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Function<Long, Long> {
                return@Function settingTime - 1 - it //itはどんどんカウントを増していく
            })
            .subscribe{
                remainedTime = it.toInt()
                count_down_timer_button.text = String.format(" %02d : %02d  ▼ ", (it / 60), (it % 60))
                //TODO 0になったら、トーストを出して、停止する。
            }.let {
                compositeDisposable.add(it)
            }
    }

}
