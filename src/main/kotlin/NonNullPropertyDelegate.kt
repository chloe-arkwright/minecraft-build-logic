import kotlin.reflect.KProperty
import kotlin.reflect.cast
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.MutablePropertyDelegate

open class NonNullPropertyDelegate(val properties: ExtraPropertiesExtension) : MutablePropertyDelegate {
	override fun <T> setValue(receiver: Any?, property: KProperty<*>, value: T) {
		val name = property.name.replace(Regex("([A-Z])")) { ".${it.value.lowercase()}" }

		properties[name] = value
	}

	override fun <T> getValue(receiver: Any?, property: KProperty<*>): T {
		val name = property.name.replace(Regex("([A-Z])")) { ".${it.value.lowercase()}" }

		return properties[name].cast()
	}

	private inline fun <reified T : Any> Any?.cast(): T {
		return T::class.cast(this)
	}
}
