package com.jackh.okhttps.self.sign.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jackh.okhttps.self.sign.R

/**
 * Project Name：awesome-wandroid
 * Created by hejunqiu on 2019/10/28 17:29
 * Description:
 */

class CustomStateView : FrameLayout {

    private companion object {
        const val STATE_LOADING = 0
        const val STATE_EMPTY = 1
        const val STATE_ERROR = 2
        const val STATE_CONTENT = 3
    }

    private var onRetryBtnClickListener: ((View) -> Unit)? = null

    private lateinit var contentView: View

    private var currentState: Int = STATE_CONTENT

    private var errorTextResId: Int = R.string.network_error
    private var errorText: String? = null

    private var errorImageResId: Int = R.drawable.network_error_icon

    private var emptyTextResId: Int = R.string.empty_text
    private var emptyText: String? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initData(context, attrs, defStyleAttr)
    }

    private fun initData(context: Context, attrs: AttributeSet?, defStyleAttr: Int){
        val typedArray: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomStateView, defStyleAttr, 0)

        for (index in 0 until typedArray.indexCount) {
            when (val attr = typedArray.getIndex(index)) {
                R.styleable.CustomStateView_errorText -> {

                    errorTextResId = typedArray.getResourceId(attr, R.string.network_error)

                    errorText = typedArray.getString(attr)
                }

                R.styleable.CustomStateView_errorImageResId -> {
                    errorImageResId = typedArray.getResourceId(attr, R.drawable.network_error_icon)
                }

                R.styleable.CustomStateView_emptyText -> {
                    emptyTextResId = typedArray.getResourceId(attr, R.string.empty_text)

                    emptyText = typedArray.getString(attr)
                }
            }
        }

        typedArray.recycle()
    }

    private lateinit var retryBtn: Button

    private lateinit var errorContainer: View
    private lateinit var errorTv: TextView
    private lateinit var errorIv: ImageView

    private lateinit var emptyContainer: View
    private lateinit var emptyTv: TextView

    private lateinit var progressView: View

    private fun inflateCustomStateView() {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_state_view,
                this,
                true)

        retryBtn = findViewById(R.id.retry_btn)

        errorContainer = findViewById(R.id.error_container)
        errorTv = findViewById(R.id.error_tv)
        errorIv = findViewById(R.id.error_iv)

        emptyContainer = findViewById(R.id.empty_container)
        emptyTv = findViewById(R.id.empty_tv)


        progressView = findViewById(R.id.progress_view)

        retryBtn.setOnClickListener {
            onRetryBtnClickListener?.invoke(it)
        }

        setNetWorkText(errorText)
        setNetWorkText(errorTextResId)

        setNetWorkIcon(errorImageResId)

        setEmptyText(emptyText)
        setEmptyText(emptyTextResId)
    }

    fun setOnRetryBtnClickListener(onRetryBtnClickListener: (View) -> Unit) {
        this.onRetryBtnClickListener = onRetryBtnClickListener
    }

    fun setNetWorkText(text: String?) {
        text?.let {
            errorTv.text = it
        }
    }
    fun setNetWorkText(@StringRes textResId: Int) {
        errorTv.setText(textResId)
    }

    fun setNetWorkIcon(@DrawableRes resId: Int) {
        errorIv.setImageResource(resId)
    }

    fun setEmptyText(text: String?) {
        text?.let {
            emptyTv.text = it
        }
    }
    fun setEmptyText(@StringRes resId: Int) {
        emptyTv.setText(resId)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            throw RuntimeException("CustomStateView must have one child")
        }
        contentView = getChildAt(0)

        inflateCustomStateView()
    }

    private fun switchView(state: Int) {
        if (currentState == state) {
            return
        }
        currentState = state

        when (currentState) {

            STATE_LOADING -> {
                progressView.visibility = View.VISIBLE
                emptyContainer.visibility = View.INVISIBLE
                errorContainer.visibility = View.INVISIBLE
                contentView.visibility = View.INVISIBLE
            }

            STATE_CONTENT -> {
                progressView.visibility = View.INVISIBLE
                emptyContainer.visibility = View.INVISIBLE
                errorContainer.visibility = View.INVISIBLE
                contentView.visibility = View.VISIBLE
            }

            STATE_EMPTY -> {
                progressView.visibility = View.INVISIBLE
                emptyContainer.visibility = View.VISIBLE
                errorContainer.visibility = View.INVISIBLE
                contentView.visibility = View.INVISIBLE
            }

            STATE_ERROR -> {
                progressView.visibility = View.INVISIBLE
                emptyContainer.visibility = View.INVISIBLE
                errorContainer.visibility = View.VISIBLE
                contentView.visibility = View.INVISIBLE
            }
        }
    }

    fun showContent() {
        switchView(STATE_CONTENT)
    }

    fun showEmpty(){
        switchView(STATE_EMPTY)
    }

    fun showError(){
        switchView(STATE_ERROR)
    }

    fun showLoading(){
        switchView(STATE_LOADING)
    }
}