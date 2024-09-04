package com.vcp.hessen.kurhessen.core.util

interface Callback<T> {
    fun call(value : T)
}