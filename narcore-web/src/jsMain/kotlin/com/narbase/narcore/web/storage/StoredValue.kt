package com.narbase.narcore.web.storage

import kotlinx.browser.window
import org.w3c.dom.get
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class StoredValue<T>(
    val key: String,
    val valueToString: (T) -> String,
    val stringToValue: (String?) -> T
) : ReadWriteProperty<Any, T> {


    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return stringToValue(window.localStorage[key])
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        console.log(value)
        window.localStorage.setItem(key, valueToString(value))
    }

}

class StoredStringValue(key: String) : StoredValue<String?>(key, { it ?: "" }, { it })

class StoredIntValue(key: String) : StoredValue<Int?>(key, { it.toString() }, { it?.toIntOrNull() })

class StoredBooleanValue(key: String, defaultValue: Boolean) : StoredValue<Boolean>(key, { it.toString() }, {
    it?.toBoolean() ?: defaultValue
})