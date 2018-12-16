package com.apps.magicleon.stargazers.views

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.apps.magicleon.stargazers.R
import com.apps.magicleon.stargazers.utils.ValidationUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.run {
            et_username.setText(savedInstanceState.getString(getString(R.string.USERNAME_KEY)))
            et_repository.setText(savedInstanceState.getString(getString(R.string.REPOSITORY_KEY)))
        }

        search_button.setOnClickListener {

            var valid = true

            if (!isFieldValid(et_username)) {
                et_username.error = getString(R.string.empty_field_error_message)
                et_username.requestFocus()
                valid = false
            } else if (!isFieldValid(et_repository)) {
                et_repository.error = getString(R.string.empty_field_error_message)
                et_repository.requestFocus()
                valid = false
            }


            if (valid) {
                val intent = Intent(this, StargazersActivity::class.java)
                intent.putExtra(getString(R.string.USERNAME_KEY), et_username.text.toString())
                intent.putExtra(getString(R.string.REPOSITORY_KEY), et_repository.text.toString())

                startActivity(intent)
            }
        }
    }

    private fun isFieldValid(field: EditText): Boolean {
        val stringValue = field.text.toString()
        return ValidationUtils.isSearchFieldValueValid(stringValue)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putString(getString(R.string.USERNAME_KEY), et_username.text.toString())
        outState?.putString(getString(R.string.REPOSITORY_KEY), et_repository.text.toString())
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
