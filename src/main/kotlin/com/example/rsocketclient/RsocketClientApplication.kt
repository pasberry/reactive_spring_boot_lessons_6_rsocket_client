package com.example.rsocketclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@SpringBootApplication
class RsocketClientApplication {

    @Bean
    fun rSocketRequester(builder: RSocketRequester.Builder) = builder
            .connectTcp("localhost", 7000)
            .block()

}

fun main(args: Array<String>) {
    runApplication<RsocketClientApplication>(*args)

}

@Component
class Consumer(val requester: RSocketRequester) {

    @EventListener(ApplicationStartedEvent::class)
    fun consume():Unit {
        this.requester
                .route("greetings.{timeInSeconds}" , 2)
                .data(GreetingRequest("This is the way"))
                .retrieveFlux(GreetingResponse::class.java)
                .doOnNext { println(it.message) }
                .subscribe { println(it)}

    }

}


data class GreetingRequest(val name:String)
data class GreetingResponse(val message:String)
