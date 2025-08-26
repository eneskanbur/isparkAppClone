package com.knbrgns.isparkappclone.view.dialog

import androidx.appcompat.app.AlertDialog

interface BaseDialog {

    fun show(): AlertDialog

    interface BaseBuilder<T: BaseDialog> {
        fun build(): T
    }

}