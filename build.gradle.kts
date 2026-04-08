import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	kotlin("jvm") version "2.3.20"
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
	mavenCentral()

	exclusiveContent {
		forRepository {
			maven {
				name = "FabricMC's Maven"
				url = uri("https://maven.fabricmc.net/")
			}
		}
		filter {
			includeGroupAndSubgroups("net.fabricmc")
		}
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
		languageVersion = KotlinVersion.KOTLIN_2_3
		apiVersion = KotlinVersion.KOTLIN_2_3
	}
}

dependencies {
	// https://maven.fabricmc.net/net/fabricmc/fabric-loom/
	implementation("net.fabricmc:fabric-loom:1.16.0-alpha.16")
	// https://plugins.gradle.org/plugin/me.modmuss50.mod-publish-plugin
	implementation("me.modmuss50.mod-publish-plugin:me.modmuss50.mod-publish-plugin.gradle.plugin:1.1.0")
	// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
}
