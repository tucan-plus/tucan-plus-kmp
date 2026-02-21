package de.selfmade4u.tucanpluskmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform