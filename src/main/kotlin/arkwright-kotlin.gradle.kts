import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	kotlin("jvm")
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
		languageVersion = KotlinVersion.KOTLIN_2_3
		apiVersion = KotlinVersion.KOTLIN_2_3

		freeCompilerArgs.add("-Xcontext-parameters")
	}
}
