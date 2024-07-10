package dev.maruffirdaus.stories.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.maruffirdaus.stories.R

class EmailInputEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        post {
            val textInputLayout = parent.parent as TextInputLayout
            textInputLayout.error =
                if ((0 < lengthBefore || 0 < lengthAfter) && !Patterns.EMAIL_ADDRESS.matcher(text.toString())
                        .matches()
                ) {
                    context.getString(R.string.email_error)
                } else {
                    null
                }
        }
    }
}