package com.adbc.sdk.greenp.test

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.adbc.sdk.greenp.test.databinding.ActivityMainBinding
import com.adbc.sdk.greenp.v3.GreenpReward
import com.adbc.sdk.greenp.v3.OfferwallBuilder
import java.util.zip.CRC32
import java.util.zip.Checksum

class MainActivity: FragmentActivity(), View.OnClickListener {

    private val appUserId = "someUser13"
    private val appUniqKey = "GreenpOfferwall" // 매체고유키
    private val appCode = "ZBhFaS5kxE"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showOfferwall.setOnClickListener(this)
        binding.showPopup.setOnClickListener(this)
        binding.req320x50.setOnClickListener(this)
        binding.reqMini.setOnClickListener(this)
        binding.reqFragment.setOnClickListener(this)

        initOfferwall()
    }

    override fun onClick(view: View) {

        val builder = GreenpReward.getOfferwallBuilder() ?: return

        builder.setAppUniqKey(appUniqKey)
        builder.setUseGreenpFontStyle(true) // 그린피 폰트 사용여부 ( default : false )

        when (view.id) {

            binding.showOfferwall.id ->

                GreenpReward.getOfferwallBuilder().showOfferwall(this@MainActivity)

            binding.showPopup.id ->
                GreenpReward.getOfferwallBuilder().requestBanner(this@MainActivity,
                    OfferwallBuilder.BANNER_POPUP) { result, msg, banner ->

                    if (result) {

                        banner.showPopupBanner()

                    } else {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

            binding.req320x50.id ->

                GreenpReward.getOfferwallBuilder().requestBanner(this@MainActivity,
                    OfferwallBuilder.BANNER_320x50) { result, msg, banner ->

                    if (result) {

                        if (binding.bannerWrapper.childCount > 0) {
                            binding.bannerWrapper.removeAllViews()
                        }
                        binding.bannerWrapper.addView(banner.view)

                    } else {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

            binding.reqMini.id -> GreenpReward.getOfferwallBuilder().requestBanner(this@MainActivity,
                OfferwallBuilder.BANNER_MINI) { result, msg, banner ->

                    if (result) {

                        if (binding.container.childCount > 0) {
                            binding.container.removeAllViews()
                        }
                        binding.container.addView(banner.view)

                    } else {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

            binding.reqFragment.id -> GreenpReward.getOfferwallBuilder().requestBanner(this@MainActivity,
                OfferwallBuilder.BANNER_FRAGMENT) { result, msg, banner ->

                    if (result) {

                        if (binding.container.childCount > 0) {
                            binding.container.removeAllViews()
                        }

                        val fragment = banner.fragment ?: return@requestBanner

                        supportFragmentManager.commit {
                            replace(R.id.container, fragment)
                            setReorderingAllowed(true)
                            addToBackStack("")
                        }

                    } else {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initOfferwall() {
        /**greenp_v3-debug.aar
         * @params
         * - Context context
         * - String appCode ( 발급받은 매체 코드 )
         * - String userId ( 매체사 유저 아이디 )
         * - OnAdbcRewardListener initListener
         */
        GreenpReward.init(applicationContext, appCode, appUserId) { result, msg ->
            if (result) {
                Toast.makeText(baseContext, "SDK가 초기화 되었습니다.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(baseContext, "SDK가 초기화 되지 않았습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** 암호화 된 유저 ID 생성 예제  */
    private fun encId(): String {

        //byte[] bytes = (appUserId + ("adid")).getBytes();
        val bytes = appUserId.toByteArray()
        val crc: Checksum = CRC32()
        crc.update(bytes, 0, bytes.size)
        return String.format("%08x", crc.value)
    }
}