package com.dicoding.picodiploma.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide.init
import com.dicoding.picodiploma.storyapp.R

class PasswordEditText : AppCompatEditText {
    private lateinit var iconPassword: Drawable
    private var passLength: Int = 0
    private val minPassLength: Int = 8


    init {
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iconPassword = ContextCompat.getDrawable(context, R.drawable.ic_password_show) as Drawable
        showIconPasswordInput()
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passLength = s.length
                if (s.isNotEmpty() && passLength < minPassLength) {
                    error = context.getString(R.string.password_invalid)
                }
            }

            override fun afterTextChanged(ed: Editable?) {

            }
        })

    }

    private fun showIconPasswordInput() {
        setButtonDrawables(startOfTheText = iconPassword)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length ?: 0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        maxLines = 1
        hint = resources.getString(R.string.password_hint)
    }
}