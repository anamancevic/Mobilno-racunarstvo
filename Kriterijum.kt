package com.dam.e_biblioteka

enum class Kriterijum{
    NASLOV,
    AUTOR,
    GODINA_IZDAVANJA,
    ISBN,
    SVE


}
inline fun <reified T : Enum<T>> T.toInt(): Int {
    return this.ordinal
}