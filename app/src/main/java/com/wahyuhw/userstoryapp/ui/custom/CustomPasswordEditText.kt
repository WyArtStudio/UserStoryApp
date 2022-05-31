package com.wahyuhw.userstoryapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.wahyuhw.userstoryapp.R

class CustomPasswordEditText: AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage: Drawable
    private lateinit var editTextBackground: Drawable
    private var txtColor: Int = 0
    private var txtHintColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setTextColor(txtColor)
        setHintTextColor(txtHintColor)
        textSize = 12f
        setPadding(20)
        background = editTextBackground
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() = setButtonDrawables(endOfTheText = clearButtonImage)

    private fun hideClearButton() = setButtonDrawables()

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null) {
        setCompoundDrawables(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    private fun init() {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.background_dark) as Drawable
        txtColor = ContextCompat.getColor(context, R.color.black)
        txtHintColor = ContextCompat.getColor(context, R.color.colorHint)
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text.toString().isNotEmpty()) {
                    showClearButton()
                    error = if (text!!.length < 6) {
                        resources.getString(R.string.error_password_length)
                    } else {
                        null
                    }
                } else {
                    hideClearButton()
                }
            }

            override fun afterTextChanged(text: Editable?) {}
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else {
                return false
            }
        }
        return false
    }
}