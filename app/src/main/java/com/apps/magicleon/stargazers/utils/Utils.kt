package com.apps.magicleon.stargazers.utils

class Utils {
    companion object {
        private const val baseGithubApiURL = "https://api.github.com/"

        fun buildStargazersRequestURLString(username: String, repository: String, page: Int): String {

            val builder = StringBuilder(baseGithubApiURL)

            return builder
                .append("repos/")
                .append("$username/")
                .append("$repository/")
                .append("stargazers")
                .append("?page=$page")
                .toString()
        }
    }
}