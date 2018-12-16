package com.apps.magicleon.stargazers

import com.apps.magicleon.stargazers.utils.ValidationUtils
import org.junit.Test

class ValidationUtilsUnitTest{
    @Test
    fun isFieldValidTest_positive(){
        val inputs: Array<String> = arrayOf("linux","foo","bar")

        for (input in inputs) {
            assert(ValidationUtils.isSearchFieldValueValid(input))
        }
    }

    @Test
    fun isFieldValidTest_negative(){
        val inputs: Array<String?> = arrayOf("",null)

        for (input in inputs) {
            assert(!ValidationUtils.isSearchFieldValueValid(input))
        }
    }
}