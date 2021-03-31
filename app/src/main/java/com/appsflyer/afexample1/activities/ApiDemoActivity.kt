package com.appsflyer.afexample1.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.Purchase
import com.appsflyer.*
import com.appsflyer.CreateOneLinkHttpTask.ResponseListener
import com.appsflyer.afexample1.R
import com.appsflyer.afexample1.databinding.ActivityApiDemoBinding
import com.appsflyer.afexample1.network.PushRequest
import com.appsflyer.afexample1.network.PushType
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.share.CrossPromotionHelper
import com.appsflyer.share.ShareInviteHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import java.net.URI


class ApiDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApiDemoBinding
    private var _opt = false
    private var _anonymizeUser = false

    companion object {
        private const val TAG = "AppsFlyer_API_Demo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiDemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setOnClicks()
        registerListeners()
    }

    private fun registerListeners() {
        AppsFlyerLib.getInstance().registerValidatorListener(this, object :
            AppsFlyerInAppPurchaseValidatorListener {
            override fun onValidateInApp() {
                Log.d(TAG, "Purchase validated successfully")
                showDialog(
                    "validateAndLogInAppPurchase", "Purchase validated successfully",
                    "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#core-apis-53-inapp-purchase-validation"
                )

            }

            override fun onValidateInAppFailure(error: String) {
                Log.d(TAG, "onValidateInAppFailure called: $error")
                showDialog(
                    "validateAndLogInAppPurchase",
                    "onValidateInAppFailure called: $error\n(As expected in this MockUp)",
                    "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#core-apis-53-inapp-purchase-validation"
                )
            }
        })

    }


    private fun setOnClicks() {

        binding.apiBTNCrossPromo.setOnClickListener()
        {
            crossPromo()
        }

        binding.apiBTNInviteUser.setOnClickListener()
        {
            userInvite()
        }


        binding.apiBTNInApp.setOnClickListener()
        {

            logEvent()
        }
        binding.apiBTNLogSession.setOnClickListener()
        {

            logSession()
        }
        binding.apiBTNOpt.setOnClickListener()
        {

            optUser()
        }
        binding.apiBTNAnonymizeUser.setOnClickListener()
        {

            anonymizeUser()
        }
        binding.apiBTNAfId.setOnClickListener()
        {

            showGetAppsFlyerUID()
        }
        binding.apiBTNPerformOAOA.setOnClickListener()
        {

            performOnAppAttribution()
        }
        binding.apiBTNInAppValidation.setOnClickListener()
        {
            val purchase = Purchase("{aaa:\"123\"}", "bbb:\"abcd\"")
            handlePurchase(purchase)
        }
        binding.apiBTNPushNotificationData.setOnClickListener()
        {
            testPushNotificationData(
                PushType.PushNotificationData,
                "https://support.appsflyer.com/hc/en-us/articles/207032126#additional-apis-recording-push-notifications"
            )

        }
        binding.apiBTNPushDeepLink.setOnClickListener()
        {
            testPushNotificationData(
                PushType.DeepLinkPath,
                "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#core-apis-65-configure-push-notification-deep-link-resolution"
            )

        }

    }

    private fun userInvite() {
        val linkGenerator = ShareInviteHelper.generateInviteUrl(this)
        linkGenerator.channel = "Gmail"
        linkGenerator.addParameter("af_cost_value", "2.5")
        linkGenerator.addParameter("af_cost_currency", "USD")
        // optional - set a brand domain to the user invite link
        // optional - set a brand domain to the user invite link
        //linkGenerator.brandDomain = "brand.domain.com"
        val listener: ResponseListener = object : ResponseListener {
            override fun onResponse(s: String) {
                Log.d("Invite Link", s)
                // write logic to let user share the invite link
                showDialog(
                    "User Invite",
                    "Link generated successfully!\n$s",
                    "https://support.appsflyer.com/hc/en-us/articles/115004480866"
                )
            }

            override fun onResponseError(s: String) {
                // handle response error
                showDialog(
                    "User Invite",
                    "Link generate failed!\n$s",
                    "https://support.appsflyer.com/hc/en-us/articles/115004480866"
                )

            }
        }
        linkGenerator.generateLink(this, listener)
    }

    private fun testPushNotificationData(type: PushType, url: String) {
        MaterialAlertDialogBuilder(this@ApiDemoActivity).setTitle(title)
            .setMessage(getText(R.string.sendPush))
            .setNeutralButton(getText(R.string.dismiss), null)
            .setNegativeButton(
                getText(R.string.send)
            ) { _, _ -> sendPushNotification(type) }.setPositiveButton(
                getText(R.string.openDoc)
            ) { _, _ -> openURL(url) }
            .show()

    }


    private fun performOnAppAttribution() {
        showDialog(
            "performOnAppAttribution",
            "re-trigger onAppOpenAttribution with a specific link (URI or URL), without recording a new re-engagement.",
            "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-performonappattribution"
        )
        AppsFlyerLib.getInstance()
            .performOnAppAttribution(this, URI.create("https://paz-sample.onelink.me/4ln7/d6aee21"))

    }

    private fun showGetAppsFlyerUID() {
        val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        showDialog(
            "getAppsFlyerUID", "AppsFlyer UID is $appsFlyerId",
            "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#api-reference-getappsflyeruid"
        )
    }

    private fun anonymizeUser() {
        _anonymizeUser = !_anonymizeUser
        AppsFlyerLib.getInstance().anonymizeUser(_anonymizeUser)
        binding.apiBTNAnonymizeUser.text =
            (if (_anonymizeUser) getString(R.string.anonymizeUserDisable) else getString(R.string.anonymizeUserEnable))
        val msg = "The User Is Now " + if (_anonymizeUser) "Anonymize" else "Not Anonymize"
        showDialog(
            "Anonymize user data", msg,
            "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#additional-apis-anonymize-user-data"
        )
    }

    private fun optUser() {
        _opt = !_opt
        applicationContext
        AppsFlyerLib.getInstance().stop(_opt, applicationContext)
        binding.apiBTNOpt.text =
            (if (_opt) getString(R.string.optIn) else getString(R.string.optOut))
        Log.d(TAG, "optUser:  " + AppsFlyerLib.getInstance().isStopped)
        val msg = "The User Is Now OPT-" + if (AppsFlyerLib.getInstance().isStopped) "Out" else "In"
        showDialog(
            "Stop (opt out)", msg,
            "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#additional-apis-optout"
        )
    }

    private fun logSession() {
        AppsFlyerLib.getInstance().logSession(this)
        showDialog(
            "logSession", "A Session Logged Successfully",
            "https://support.appsflyer.com/hc/en-us/articles/207032126-Android-SDK-integration-for-developers#additional-apis-background-sessions-for-utility-apps"
        )
    }

    private fun logEvent() {
        val eventValue = HashMap<String, Any>()
        eventValue[AFInAppEventParameterName.REVENUE] = 120
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
                            "logEvent", "In App Event Logged Failed\nError: $s\nError Code: $i",
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

    // Purchase object is returned by Google API in onPurchasesUpdated() callback
    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "Purchase successful!")
        val additionalEventValues = HashMap<String, String>()
        additionalEventValues["some_parameter"] = "some_value"
        val price = "10"
        val currency = "USD"
        AppsFlyerLib.getInstance().validateAndLogInAppPurchase(
            this,
            getString(R.string.publicKey),
            purchase.signature,
            purchase.originalJson,
            price,
            currency,
            additionalEventValues
        )
    }

    private fun sendPushNotification(type: PushType) {
        Thread {

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        "pttt",
                        "token: ${task.result}"
                    )
                    if (type == PushType.PushNotificationData)
                        PushRequest.sendPushToThisDevice(this, task.result.toString())
                    else
                        PushRequest.sendAddPushNotificationDeepLinkPath(
                            this,
                            task.result.toString()
                        )


                }
            }
        }.start()
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