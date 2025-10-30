package ru.pachan.main_kotlin.util.sql

import com.fasterxml.jackson.annotation.JsonProperty

enum class OrderDirection {

    @JsonProperty("DESC")
    DESC,

    @JsonProperty("ASC")
    ASC

}