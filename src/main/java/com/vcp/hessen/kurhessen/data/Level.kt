package com.vcp.hessen.kurhessen.data

enum class Level(val langKey : String, val maxAge : Int) {
    WOELFLING("WOELFLING", 10),
    JUNGPFADFINDER("JUNGPFADFINDER", 14),
    PFADFINDER("PFADFINDER", 16),
    ROVER("ROVER", 21),
    ERWACHSEN("ERWACHSEN", 21);
}