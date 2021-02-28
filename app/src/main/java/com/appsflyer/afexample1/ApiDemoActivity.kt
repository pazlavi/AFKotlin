package com.appsflyer.afexample1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import com.appsflyer.afexample1.databinding.ActivityApiDemoBinding
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.share.CrossPromotionHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ApiDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApiDemoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiDemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setOnClicks()
    }


    private fun setOnClicks() {
        binding.apiBTNCrossPromo.setOnClickListener()
        {
            crossPromo();
        }


        binding.apiBTNInApp.setOnClickListener()
        {
            logEvent()
        }

    }

    private fun logEvent() {
        val eventValue = HashMap<String, Any>()
        eventValue[AFInAppEventParameterName.REVENUE] = 1234.56
        eventValue[AFInAppEventParameterName.CONTENT_TYPE] = "Shirt"
        eventValue[AFInAppEventParameterName.CONTENT_ID] = "1234567"
        eventValue[AFInAppEventParameterName.CURRENCY] = "USD"
        AppsFlyerLib.getInstance()
            .logEvent(
                applicationContext,
                AFInAppEventType.PURCHASE,
                eventValue,
                object : AppsFlyerRequestListener {
                    override fun onSuccess() {
                        showDialog(
                            "logEvent", "In App Event Logged Successfully",
                            "https://support.appsflyer.com/hc/en-us/articles/207032126#core-apis-5-recording-inapp-events"
                        )
                    }

                    override fun onError(i: Int, s: String) {
                        showDialog(
                            "logEvent", "In App Event Logged Failed\nError: $s",
                            "https://support.appsflyer.com/hc/en-us/articles/207032126#core-apis-5-recording-inapp-events"
                        )
                    }
                })


    }

    private fun openURL(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun crossPromo() {
        Log.d(
            "AppsFlyer",
            "cuid = " + AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID)
        )
        val appID = "com.paz.maale_milim"
        val campaign = "Cross Promo Campaign"
        val parameters = HashMap<String, String>()
        parameters["pid"] = "willbedup"
        parameters["af_sub1"] = "test"
        parameters["custom_param"] = "jira"
        CrossPromotionHelper.logAndOpenStore(
            this,
            appID,
            campaign,
            parameters
        )
        showDialog(
            "Cross promotion attribution",
            "Record and attribute installs originating from cross-promotion campaigns of your apps.",
            "https://support.appsflyer.com/hc/en-us/articles/207032126#additional-apis-cross-promotion-attribution"
        )
    }

    private fun showDialog(title: String, msg: String, url: String) {
        this@ApiDemoActivity.runOnUiThread {
            MaterialAlertDialogBuilder(this@ApiDemoActivity).setTitle(title)
                .setMessage(msg)
                .setNeutralButton(getText(R.string.dismiss), null)
                .setPositiveButton(
                    getText(R.string.openDoc)
                ) { _, _ -> openURL(url) }
                .show()
        }
    }
}