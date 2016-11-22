package com.programming.kotlin.chapter12.hello.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lightbend.lagom.serialization.CompressedJsonable

@JsonDeserialize
data class HelloState(val message: String, val timestamp: String) : CompressedJsonable