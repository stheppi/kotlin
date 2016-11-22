package com.programming.kotlin.chapter12.hello.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lightbend.lagom.serialization.Jsonable

interface HelloEvent : Jsonable {
  @JsonDeserialize
  data class GreetingMessageChanged(val message: String) : HelloEvent
}