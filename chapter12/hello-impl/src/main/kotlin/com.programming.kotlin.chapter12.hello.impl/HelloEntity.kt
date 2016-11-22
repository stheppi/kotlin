package com.programming.kotlin.chapter12.hello.impl

import akka.Done
import com.lightbend.lagom.javadsl.persistence.PersistentEntity
import com.programming.kotlin.chapter12.hello.impl.HelloCommand.Hello
import com.programming.kotlin.chapter12.hello.impl.HelloCommand.UseGreetingMessage
import com.programming.kotlin.chapter12.hello.impl.HelloEvent.GreetingMessageChanged
import java.time.LocalDateTime
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

class HelloEntity : PersistentEntity<HelloCommand, HelloEvent, HelloState>() {

  /**
   * An entity can define different behaviours for different states, but it will
   * always start with an initial behaviour. This entity only has one behaviour.
   */
  override fun initialBehavior(snapshotState: Optional<HelloState>): PersistentEntity<HelloCommand, HelloEvent, HelloState>.Behavior {

    /*
     * Behaviour is defined using a behaviour builder. The behaviour builder
     * starts with a state, if this entity supports snapshotting (an
     * optimisation that allows the state itself to be persisted to combine many
     * events into one), then the passed in snapshotState may have a value that
     * can be used.
     *
     * Otherwise, the default state is to use the Hello greeting.
     */
    val b = newBehaviorBuilder(snapshotState.orElse(HelloState("Hello", LocalDateTime.now().toString())))

    /*
    * Command handler for the UseGreetingMessage command.
    */
    b.setCommandHandler<Done, UseGreetingMessage>(
        UseGreetingMessage::class.java,
        BiFunction<UseGreetingMessage,
            PersistentEntity<HelloCommand, HelloEvent, HelloState>.CommandContext<Done>,
            PersistentEntity<HelloCommand, HelloEvent, HelloState>.Persist<GreetingMessageChanged>> { cmd, ctx ->

          // In response to this command, we want to first persist it as a
          // GreetingMessageChanged event
          ctx.thenPersist<GreetingMessageChanged>(HelloEvent.GreetingMessageChanged(cmd.message),
              Consumer<GreetingMessageChanged> { evt: GreetingMessageChanged -> ctx.reply(Done.getInstance()) })
              as PersistentEntity<HelloCommand, HelloEvent, HelloState>.Persist<GreetingMessageChanged>
        }
    )

    /*
     * Event handler for the GreetingMessageChanged event.
     */
    b.setEventHandler<GreetingMessageChanged>(GreetingMessageChanged::class.java,
        // We simply update the current state to use the greeting message from
        // the event.
        Function<GreetingMessageChanged, HelloState> { evt -> HelloState(evt.message, LocalDateTime.now().toString()) })

    /*
     * Command handler for the Hello command.
     */
    b.setReadOnlyCommandHandler<String, Hello>(Hello::class.java,
        // Get the greeting from the current state, and prepend it to the name
        // that we're sending
        // a greeting to, and reply with that message.
        BiConsumer<Hello, PersistentEntity<HelloCommand, HelloEvent, HelloState>.ReadOnlyCommandContext<String>>{ cmd, ctx -> ctx.reply(state().message + ", " + cmd.name + "!") })

    /*
     * We've defined all our behaviour, so build and return it.
     */
    return b.build()
  }
}
