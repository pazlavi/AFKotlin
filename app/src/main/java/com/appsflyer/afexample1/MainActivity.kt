package com.appsflyer.afexample1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.afexample1.databinding.ActivityMainBinding
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult


class MainActivity : AppCompatActivity(), DeepLinkListener, AppsFlyerConversionListener {
    private var _tag = "AppsFlyer_Sample_App"
    private var _clear = true

    private lateinit var binding: ActivityMainBinding

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(_tag, "onNewIntent called ")
        Log.d(_tag, "onNewIntent extras = ${intent?.extras.let { it.toString() }} ")
        Log.d(_tag, "onNewIntent flags = ${intent?.flags.let { it.toString() }} ")
        setIntent(intent)
    }

    override fun onResume() {
        intent.extras?.let {
            for (key in it.keySet())
                Log.d(_tag, "$key = ${it[key]} ")
        }
        super.onResume()
        _clear = true

    }

    override fun onPause() {
        super.onPause()
        if (_clear) {
            binding.mainLBLConversion.text = getString(R.string.conversionEmpty)
            binding.mainLBLDeepLink.text = getString(R.string.deepLinkEmpty)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        appsFlyerInit()
        setOnClicks()

    }

    private fun setOnClicks() {
        binding.mainBTNApiDemo.setOnClickListener { makeIntent(ApiDemoActivity::class.java) }
    }


    private fun appsFlyerInit() {
        val af = AppsFlyerLib.getInstance()
        af.subscribeForDeepLink(this)
        af.registerConversionListener(this, this)
    }


    override fun onDeepLinking(p0: DeepLinkResult) {
        setDeepLinkView(p0)
        if (p0.error != null) {
            Log.d(_tag, "There was an error getting Deep Link data")
            return
        }
        if (p0.deepLink == null) {
            Log.d(_tag, "DeepLink data came back null")
            return
        }
        if (p0.deepLink.isDeferred == true) {
            // Deferred deep link
            handleDeferredDeepLink(p0.deepLink.deepLinkValue)
        } else {
            handleDirectDeepLink(p0.deepLink.deepLinkValue)

        }


    }

    private fun handleDirectDeepLink(deepLinkValue: String?) {
        if (deepLinkValue == null)
            makeIntent(DirectDeepLinkActivity::class.java)
        else {
            when (deepLinkValue) {
                "sms retargeting" -> makeIntent(DirectDeepLinkActivity::class.java)
                "sms_deferred" -> makeIntent(DirectDeepLinkActivity::class.java)
                "sms_direct" -> makeIntent(DirectDeepLinkActivity::class.java)

            }
        }
    }

    private fun handleDeferredDeepLink(deepLinkValue: String?) {
        if (deepLinkValue == null)
            makeIntent(DeferredDeepLinkActivity::class.java)
        when (deepLinkValue) {
            "sms retargeting" -> makeIntent(DeferredDeepLinkActivity::class.java)
            "sms_deferred" -> makeIntent(DeferredDeepLinkActivity::class.java)
            "sms_direct" -> makeIntent(DeferredDeepLinkActivity::class.java)
            else -> makeIntent(DeferredDeepLinkActivity::class.java)

        }

    }

    private fun <T : Activity> makeIntent(dest: Class<T>) {
        _clear = false
        val i = Intent(this, dest)
        startActivity(i)
    }

    private fun setDeepLinkView(p0: DeepLinkResult) {
        var str = ""
        str = if (p0.error != null) {
            val s = "Error Received: " + p0.status.name
            getString(R.string.onDeepLinking, s)

        } else {
            val s = deepLinkToString(p0.deepLink)
            getString(R.string.onDeepLinking, s)
        }
        this.runOnUiThread {
            binding.mainLBLDeepLink.text = str
        }
        Log.d(_tag, "onDeepLinking: " + p0.deepLink)
    }

    private fun deepLinkToString(deepLink: DeepLink): String {
        val sb = StringBuilder()
        val s = deepLink.toString().substring(1, deepLink.toString().length - 1).split(",")
        for (i in s) {
            sb.append(i.replace(":", " = ").replace("\"", "")).append("\n")
        }
        return sb.toString()

    }

    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {

        val str = mapToString(p0)
        this.runOnUiThread {
            binding.mainLBLConversion.text =
                getString(R.string.onConversionDataSuccess, str)
        }
        Log.d(_tag, "onConversionDataSuccess: " + p0.toString())

    }

    override fun onConversionDataFail(p0: String?) {
        Log.d(_tag, "onConversionDataFail: ")
        this.runOnUiThread {
            binding.mainLBLConversion.text = getString(R.string.onConversionDataFail, p0.toString())
        }
    }

    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        Log.d(_tag, "onAttributionFailure: ")
        this.runOnUiThread {
            binding.mainLBLDeepLink.text = getString(R.string.onAppOpenAttribution, p0.toString())
        }
    }

    override fun onAttributionFailure(p0: String?) {
        Log.d(_tag, "onAttributionFailure: ")
        this.runOnUiThread {
            binding.mainLBLDeepLink.text = getString(R.string.onAttributionFailure, p0.toString())
        }
    }

    private fun mapToString(map: MutableMap<String, Any>?): String {
        return if (map != null) {
            val sb = StringBuilder()
            for (k in map.keys) {
                sb.append(k).append(" = ").append(map[k]).append("\n")
            }
            sb.toString()
        } else {
            "got null map"
        }
    }


}