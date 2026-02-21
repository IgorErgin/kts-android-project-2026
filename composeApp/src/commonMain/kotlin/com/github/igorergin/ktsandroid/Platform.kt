package com.github.igorergin.ktsandroid

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform