package com.knbrgns.isparkappclone.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.knbrgns.isparkappclone.databinding.DialogErrorBinding
import androidx.core.graphics.drawable.toDrawable

class ErrorDialog(
    private val context: Context,
    private val binding: DialogErrorBinding,
    private val errorTitle: String,
    private val errorMessage: String,
    private val onConfirm: () -> Unit,
    private val isCancelable: Boolean
) : BaseDialog {

    init {
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            tvErrorTitle.text = errorTitle
            tvErrorMessage.text = errorMessage
        }
    }

    override fun show(): AlertDialog {

        val createdDialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .setCancelable(isCancelable)
            .create()

        binding.btnOkay.setOnClickListener {
            onConfirm()
            createdDialog.dismiss()
        }
        createdDialog.show()
        return createdDialog
    }

    class Builder(private val context: Context) : BaseDialog.BaseBuilder<ErrorDialog> {

        private var errorTitle: String = ""
        private var errorMessage: String = ""
        private var onConfirm: () -> Unit = {}
        private var isCancelable: Boolean = true

        fun setErrorTitle(errorTitle: String) = apply { this.errorTitle = errorTitle }
        fun setErrorMessage(errorMessage: String) = apply { this.errorMessage = errorMessage }
        fun setOnConfirm(onConfirm: () -> Unit) = apply { this.onConfirm = onConfirm }
        fun setCancelable(isCancelable: Boolean) = apply { this.isCancelable = isCancelable }

        override fun build(): ErrorDialog {
            val binding = DialogErrorBinding.inflate(LayoutInflater.from(context))
            return ErrorDialog(context, binding, errorTitle, errorMessage, onConfirm, isCancelable)
        }
    }
}
