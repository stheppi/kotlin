package com.programming.kotlin.chapter12.hello.api

import com.fasterxml.jackson.databind.annotation.JsonDeserialize


@JsonDeserialize
data class GreetingMessage(val message: String)