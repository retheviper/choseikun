package com.retheviper.choseikun.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Choseikun

fun main(args: Array<String>) {
    runApplication<Choseikun>(*args)
}