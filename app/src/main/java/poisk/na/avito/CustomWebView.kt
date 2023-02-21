package poisk.na.avito

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView


class CustomWebView: WebView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var ic: InputConnection? = null
    private var imm: InputMethodManager? =null

    private var inputConnection: InputConnection? = null
    public var jsCode: String = ""
    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        val newInputConnection = super.onCreateInputConnection(outAttrs) ?: return inputConnection
        inputConnection = newInputConnection
        return newInputConnection
    }

    fun sendTextInput(text: String) {
        if (imm==null)
            imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: throw Exception("could not get system's InputMethodManager")
        requestFocus()
        imm!!.showSoftInput(this, 0)
        if (ic == null)
            ic = onCreateInputConnection(EditorInfo())
                ?: throw Exception("could not create InputConnection")
        ic!!.commitText(text, text.length)
        imm!!.hideSoftInputFromWindow(windowToken, 0)
    }
}

