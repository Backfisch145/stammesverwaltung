package com.vcp.hessen.kurhessen.util

inline fun <T : Any, R> T.tryOrNull(f: T.() -> R): R? {
    return try {
        f()
    } catch (e: Exception) {
        null
    }
}