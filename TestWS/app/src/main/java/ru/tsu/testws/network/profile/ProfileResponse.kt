package ru.tsu.testws.network.profile

data class ProfileResponse(
    val userId: String,
    val name: String,
    val aboutMyself: String?,
    val avatar: String?,
    val topics: List<TopicResponse>
)
