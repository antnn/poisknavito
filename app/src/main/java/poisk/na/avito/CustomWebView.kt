package poisk.na.avito

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import kotlinx.coroutines.delay
import kotlin.random.Random


class CustomWebView: WebView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var inputConnection: InputConnection? = null
    public var jsCode: String=""
    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        val newInputConnection = super.onCreateInputConnection(outAttrs) ?: return inputConnection
        inputConnection = newInputConnection
        return newInputConnection
    }

    fun sendTextInput(text: String) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val ic = onCreateInputConnection(EditorInfo()) ?:
        throw Exception("could not create InputConnection")
        /*for (i in text.indices) {
            ic.setComposingRegion(i, i + 1)
            ic.commitText(text[i].toString(), 1)
        }*/
        ic.commitText(text, 1)
        //imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
