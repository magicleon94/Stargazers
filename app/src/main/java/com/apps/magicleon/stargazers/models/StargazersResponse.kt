package com.apps.magicleon.stargazers.models

data class StargazersResponse(
    var hasNextPage: Boolean,
    var stargazers: ArrayList<Stargazer>
)