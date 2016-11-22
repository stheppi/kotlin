package com.programming.kotlin.chapter12.hello.impl

import akka.Done
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lightbend.lagom.javadsl.persistence.PersistentEntity
import com.lightbend.lagom.serialization.CompressedJsonable
import com.lightbend.lagom.serialization.Jsonable
import java.util.*

interface HelloCommand : Jsonable {
  @JsonDeserialize
  data class UseGreetingMessage(val message: String) : HelloCommand, CompressedJsonable, PersistentEntity.ReplyType<Done>

  @JsonDeserialize
  data class Hello(val name: String, val organization: Optional<String>) : HelloCommand, PersistentEntity.ReplyType<String>
}