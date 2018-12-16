package com.apps.magicleon.stargazers.data

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.apps.magicleon.stargazers.models.Stargazer
import com.apps.magicleon.stargazers.models.StargazersResponse
import com.google.gson.Gson
import java.nio.charset.Charset

class StargazersRequest(
    method: Int,
    url: String?,
    private val responseListener: Response.Listener<StargazersResponse>,
    errorListener: Response.ErrorListener?
) :
    Request<StargazersResponse>(method, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<StargazersResponse> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )

            val linkHeader = response?.headers?.get("Link")
            val nextPageAvailable = linkHeader?.contains("rel=\"next\"", ignoreCase = true) ?: false

            val stargazersList = Gson().fromJson(json, Array<Stargazer>::class.java).toCollection(ArrayList())

            val stargazersResponse =
                StargazersResponse(nextPageAvailable, stargazersList)

            Response.success(stargazersResponse, HttpHeaderParser.parseCacheHeaders(response))
        } catch (ex: Exception) {
            Response.error(ParseError(ex))
        }
    }

    override fun deliverResponse(response: StargazersResponse?) {
        responseListener.onResponse(response)
    }

}