package com.example.viewpager2demo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.diewland.pager.MarqueePager
import kotlinx.android.synthetic.main.activity_main.*

const val JOB_DELAY = 1_000L
const val PAGE_DURATION = 500L
const val LAST_PAGE_DELAY = 1_000L

class MainActivity : AppCompatActivity() {

    private lateinit var imagesArray: Array<String>
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

    private lateinit var marqueePager: MarqueePager
    private fun playMarquee() {
        marqueePager.play(JOB_DELAY, PAGE_DURATION, LAST_PAGE_DELAY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        marqueePager = MarqueePager(slidingViewPager)

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

        // start animation
        playMarquee()
    }

    private fun setUpAnimateButtons() {
        findViewById<Button>(R.id.btn1).setOnClickListener { playMarquee() }
        findViewById<Button>(R.id.btn2).setOnClickListener { marqueePager.stop() }
    }

    override fun onDestroy() {
        super.onDestroy()
        slidingViewPager.unregisterOnPageChangeCallback(slidingCallback)
        marqueePager.stop()
    }

}
