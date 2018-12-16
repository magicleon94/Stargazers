package com.apps.magicleon.stargazers.utils

class Utils {
    private val _baseGithubApiURL = "https://api.github.com/"

    fun buildStargazersRequestURLString(username: String, repository: String, page: Int): String {

        val builder = StringBuilder(_baseGithubApiURL)

        return builder
            .append("repos/")
            .append("$username/")
            .append("$repository/")
            .append("stargazers")
            .append("?page=$page")
            .toString()
    }
}