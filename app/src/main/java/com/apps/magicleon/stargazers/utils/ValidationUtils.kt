package com.apps.magicleon.stargazers.utils

class ValidationUtils{
    companion object {
        fun isSearchFieldValueValid(fieldValue: String?): Boolean{
            return !fieldValue.isNullOrEmpty()
        }
    }
}