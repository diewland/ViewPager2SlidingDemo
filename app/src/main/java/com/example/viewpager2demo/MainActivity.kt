package com.example.viewpager2demo

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val ANI_PERIOD = 20_000L // TODO calc from item size
const val JOB_DELAY = 1_000L
const val JOB_PERIOD = JOB_DELAY + ANI_PERIOD

class MainActivity : AppCompatActivity() {

    private lateinit var imagesArray: Array<String>
    private var currentPage = 0
    private lateinit var slidingImageDots: Array<ImageView?>
    private var slidingDotsCount = 0

    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.non_active_dot
                    )
                )
            }

            slidingImageDots[position]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.active_dot
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpSlidingViewPager()
        setUpAnimateButtons()
    }

    private fun setUpSlidingViewPager() {
        imagesArray = resources.getStringArray(R.array.image_urls_array)

        val landingImagesAdapter = SlidingImagesAdapter(this, imagesArray.size)
        slidingViewPager.apply {
            adapter = landingImagesAdapter
            registerOnPageChangeCallback(slidingCallback)
        }

        slidingDotsCount = imagesArray.size

        slidingImageDots = arrayOfNulls(slidingDotsCount)

        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(this)
            slidingImageDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.non_active_dot
                )
            )
            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(8, 0, 8, 0)
            slider_dots.addView(slidingImageDots[i], params)
        }

        slidingImageDots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.active_dot
            )
        )

        val handler = Handler()
        val update = Runnable {
            if (currentPage == imagesArray.size) {
                currentPage = 0
            }

            //The second parameter ensures smooth scrolling
            //slidingViewPager.setCurrentItem(currentPage++, true)
            playAnimation()
        }

        Timer().schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(update)
            }
        }, JOB_DELAY, JOB_PERIOD)
    }

    private fun setUpAnimateButtons() {
        findViewById<Button>(R.id.btn1).setOnClickListener { goLast() }
        findViewById<Button>(R.id.btn2).setOnClickListener { goFirst() }
    }
    private fun playAnimation() {
        if (slidingViewPager.currentItem > 0)
            goFirst()
        else
            goLast()
    }
    private fun goFirst() { goTo(slidingViewPager, 0) }
    private fun goLast() { goTo(slidingViewPager, imagesArray.size-1) }

    // https://stackoverflow.com/a/59235979/466693
    private fun goTo(pager: ViewPager2, item: Int) {

        val pagePxWidth = pager.width
        val currentItem = pager.currentItem
        val interpolator = AccelerateDecelerateInterpolator()
        val duration:Long = ANI_PERIOD

        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0
        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            pager.fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) { pager.beginFakeDrag() }
            override fun onAnimationEnd(animation: Animator?) { pager.endFakeDrag() }
            override fun onAnimationCancel(animation: Animator?) { /* Ignored */ }
            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */ }
        })
        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        slidingViewPager.unregisterOnPageChangeCallback(slidingCallback)
    }

}
