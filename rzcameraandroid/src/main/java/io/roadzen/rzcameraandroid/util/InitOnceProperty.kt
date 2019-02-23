package io.roadzen.rzcameraandroid.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class InitOnceProperty<T>(
    private val shouldSetNewValue: Boolean,
    private val defaultValue: T
) : ReadWriteProperty<Any, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return if (value == EMPTY) {
            defaultValue
        } else {
            value as T
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (shouldSetNewValue) this.value = EMPTY

        if (this.value != EMPTY) {
            throw IllegalStateException("Value is initialized")
        }
        this.value = value

    }
}

inline fun <reified T> initOnce(shouldSetNewValue: Boolean, defaultValue: T) =
    InitOnceProperty(shouldSetNewValue, defaultValue)