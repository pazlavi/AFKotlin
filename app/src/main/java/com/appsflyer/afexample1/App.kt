package com.appsflyer.afexample1

import android.app.Application
import android.os.Looper
import com.appsflyer.AppsFlyerLib
import java.math.BigDecimal


class App : Application() {
    private final val LOG_TAG = "AFApplication"


    override fun onCreate() {
        super.onCreate()

        AppsFlyerLib.getInstance().setDebugLog(true)

        AppsFlyerLib.getInstance().init(resources.getString(R.string.devKey), null, this)
        AppsFlyerLib.getInstance().appendParametersToDeepLinkingURL(
            "paz://retargeting",
            mapOf("pid" to "my domain")
        )
        AppsFlyerLib.getInstance().setAppInviteOneLink("waF33")
        AppsFlyerLib.getInstance().setCustomerUserId("paz test")
        //AppsFlyerLib.getInstance().waitForCustomerUserId(true)
        AppsFlyerLib.getInstance().start(this)
        //setCUID()
    }

    private fun setCUID() {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val id =
                (AppsFlyerLib.getInstance().getAppsFlyerUID(this).replace("-", "")).toBigDecimal()
                    .multiply(BigDecimal(5))
            AppsFlyerLib.getInstance().setCustomerIdAndLogSession(id.toString(), this)
        }, 5000)
    }


}

