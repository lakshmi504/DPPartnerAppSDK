package com.dpdelivery.android.utils

class Validation {
    companion object {
        private var EMAIL_PATTERN: String? = null

        fun isValidObject(`object`: Any?): Boolean {
            return `object` != null
        }

        fun isValidString(string: String?): Boolean {
            return string != null && string.trim().isNotEmpty()
        }
    }
}