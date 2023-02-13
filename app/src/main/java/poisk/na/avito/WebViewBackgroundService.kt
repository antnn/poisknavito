package poisk.na.avito

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.*
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout



class WebShotService : Service() {
    private var webView: WebView? = null
    private var winManager: WindowManager? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        winManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        webView = WebView(this)
        webView!!.isVerticalScrollBarEnabled = false
        val typeOverlay = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            typeOverlay,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0

        webView!!.webViewClient = client

        val frame = FrameLayout(this)
        frame.addView(webView)
        winManager!!.addView(frame, params)
        //webView!!.settings.setSupportMultipleWindows(true);
        //webView!!.settings.javaScriptCanOpenWindowsAutomatically = true;
        webView!!.loadUrl("https://www.ptuniv.edu.in/")
        return START_STICKY
    }

    private val client: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            val p = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val wMetrics = winManager!!.currentWindowMetrics
                val rect = wMetrics.bounds
                p.x = rect.width()
                p.y = rect.height()
            } else {
                winManager!!.defaultDisplay.getSize(p)
            }
            webView!!.measure(
                View.MeasureSpec.makeMeasureSpec(
                    if (p.x < p.y) p.y else p.x,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    if (p.x < p.y) p.x else p.y,
                    View.MeasureSpec.EXACTLY
                )
            )

            webView!!.layout(0, 0, webView!!.measuredWidth, webView!!.measuredHeight)
            //webView!!.postDelayed(capture, 1000)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}