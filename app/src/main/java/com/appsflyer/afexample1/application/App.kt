package com.appsflyer.afexample1.application

import android.app.Application
import android.os.Looper
import android.util.Log
import com.appsflyer.AFLogger
import com.appsflyer.AppsFlyerLib
import com.appsflyer.afexample1.R
import java.math.BigDecimal


class App : Application() {


    override fun onCreate() {
        super.onCreate()

        val id = AppsFlyerLib.getInstance().getAppsFlyerUID(null)
        Log.d("AppsF", "id: $id")
        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setloglevel
        AppsFlyerLib.getInstance().setLogLevel(AFLogger.LogLevel.DEBUG)

        AppsFlyerLib.getInstance().setDebugLog(true)

        AppsFlyerLib.getInstance().init(resources.getString(R.string.devKey), null, this)

        optionalAppsFlyerInitializationAPISamples()


       // AppsFlyerLib.getInstance().start(this)
        //setCUID()


    }

    private fun optionalAppsFlyerInitializationAPISamples() {
        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setmintimebetweensessions
        AppsFlyerLib.getInstance().setMinTimeBetweenSessions(10)

        //https://support.appsflyer.com/hc/en-us/articles/207032126#api-reference-appendparameterstodeeplinkingurl
        AppsFlyerLib.getInstance().appendParametersToDeepLinkingURL(
            "paz://re",
            mapOf("pid" to "my domain", "is_retargeting" to "true")
        )

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setsharingfilter
        AppsFlyerLib.getInstance().setResolveDeepLinkURLs(
            "clickdomain.com",
            "myclickdomain.com",
            "anotherclickdomain.com"
        )

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setappinviteonelink
        AppsFlyerLib.getInstance().setAppInviteOneLink("4ln7")

        //https://support.appsflyer.com/hc/en-us/articles/207032126#additional-apis-set-customer-user-id
        AppsFlyerLib.getInstance().setCustomerUserId("paz test")
        //AppsFlyerLib.getInstance().waitForCustomerUserId(true)

        //https://support.appsflyer.com/hc/en-us/articles/207032126#api-reference-addpushnotificationdeeplinkpath
        AppsFlyerLib.getInstance().addPushNotificationDeepLinkPath("deeply", "nested", "deep_link")

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setpreinstallattribution
        //AppsFlyerLib.getInstance().setPreinstallAttribution("af","sample app"," AppsFlyer")

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setoutofstore
        AppsFlyerLib.getInstance().setOutOfStore("AppsFlyer_Site")

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setsharingfilter
        AppsFlyerLib.getInstance().setSharingFilter(
            "facebook_int",
            "googleadwords_int",
            "snapchat_int",
            "doubleclick_int"
        )

        //https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-setpartnerdata
        setPartnerData()
        setAdditionalData()
    }

    private fun setAdditionalData() {
        val data = HashMap<String, Any>()
        data["name"] = "AppsFlyer"
        data["ts"] = System.currentTimeMillis()
        data["aaa"] = "bbb"
        AppsFlyerLib.getInstance().setAdditionalData(data)
    }

    private fun setPartnerData() {
        val partnerDataA: MutableMap<String, Any> = HashMap()
        partnerDataA["encoded"] =
            "TG9yZW0gSXBzdW0gaXMgc2ltcGx5IGR1bW15IHRleHQgb2YgdGhlIHByaW50aW5nIGFuZCB0eXBlc2V0dGluZyBpbmR1c3RyeS4g"
        AppsFlyerLib.getInstance().setPartnerData("partnerA_int", partnerDataA)

        val partnerDataB: MutableMap<String, Any> = HashMap()
        partnerDataB["thePartnerId"] = "abcd"
        partnerDataB["user-type"] = "new"
        val metaData: MutableMap<String, Any> = HashMap()
        metaData["item-id"] = 123
        metaData["isLegacy"] = false
        metaData["type"] = "custom"
        partnerDataB["meta-data"] = metaData
        AppsFlyerLib.getInstance().setPartnerData("partnerB_int", partnerDataB)
    }

    private fun setCUID() {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val id =
                (AppsFlyerLib.getInstance().getAppsFlyerUID(this).replace("-", "")).toBigDecimal()
                    .multiply(BigDecimal(5))
            //https://support.appsflyer.com/hc/en-us/articles/207032126#api-reference-setcustomeridandlogsession
            AppsFlyerLib.getInstance().setCustomerIdAndLogSession(id.toString(), this)
        }, 5000)
    }

}

