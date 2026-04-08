import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
	id("arkwright-java")
	id("net.fabricmc.fabric-loom")
}

// Build Configuration Flags
val usesKotlin = findProperty("app.arkwright.kotlin") == "true"
val usesSplitSources = findProperty("app.arkwright.split-sources") == "true"
val addDatagens = findProperty("app.arkwright.datagens") == "true"

if (usesKotlin) {
	apply(plugin = "arkwright-kotlin")
}

val props = project.extensions.create("props", NonNullPropertyDelegate::class, project.extra)

val projectVersion: String by props
val projectGroup: String by props
val projectId: String by props

version = projectVersion
group = projectGroup

base.archivesName = projectId

loom {
	accessWidenerPath = file("src/main/resources/$projectId.classTweaker").takeIf(File::exists)

	if (usesSplitSources) {
		splitEnvironmentSourceSets()

		mods {
			create(projectId) {
				sourceSet("main")
				sourceSet("client")
			}
		}
	}

	runs {
		named("client") {
			property("fabric-tag-conventions-v2.missingTagTranslationWarning", "VERBOSE")
		}
	}
}

if (addDatagens) {
	fabricApi {
		configureDataGeneration {
			client = true
			modId = "$projectId-data"
			createSourceSet = true
		}
	}
}

repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth Maven"
				url = uri("https://api.modrinth.com/maven")
			}
		}

		filter {
			includeGroup("maven.modrinth")
		}
	}

	exclusiveContent {
		forRepository {
			mavenLocal()
		}

		filter {
			includeGroupAndSubgroups("app.arkwright")
		}
	}
}

val versionMinecraft: String by props
val versionFabricLoader: String by props

val versionFabricApi = findProperty("version.fabric.api")
val versionFabricKotlin = findProperty("version.fabric.kotlin")

dependencies {
	minecraft("com.mojang:minecraft:$versionMinecraft")

	implementation("net.fabricmc:fabric-loader:$versionFabricLoader")

	versionFabricApi?.let { implementation("net.fabricmc.fabric-api:fabric-api:$it") }
	versionFabricKotlin?.let { implementation("net.fabricmc:fabric-language-kotlin:$it") }
}

fun createDepEntries(providers: ProviderFactory): Map<String, String> {
	val dependencies = mutableMapOf<String, String>()

	val versions = providers.gradlePropertiesPrefixedBy("version.")
	val declaredDependencies = providers.gradlePropertiesPrefixedBy("deps.").get()
		.mapKeys { it.key.substringAfter("version.").replace(".", "_") }

	dependencies.putAll(declaredDependencies)
	dependencies.putIfAbsent("minecraft", "~$versionMinecraft")
	dependencies.putIfAbsent("java", ">=${java.targetCompatibility.majorVersion}")

	if (usesKotlin) {
		dependencies.putIfAbsent("kotlin", ">=2.3.20")
	}

	for (entry in versions.get()) {
		dependencies.putIfAbsent(entry.key.substringAfter("version.").replace(".", "_"), ">=${entry.value}")
	}

	return dependencies
}

tasks {
	jar {
		version = "$projectVersion+$versionMinecraft"
	}

	tasks.withType<ProcessResources> {
		inputs.properties(
			"project" to mapOf(
				"version" to projectVersion
			),
			"deps" to createDepEntries(providers)
		)

		filesMatching("fabric.mod.json") {
			expand(inputs.properties)
		}
	}
}

if (usesKotlin && (usesSplitSources || addDatagens)) {
	extensions.configure<KotlinJvmProjectExtension>("kotlin") {
		target.compilations.apply {
			val main by getting

			if (usesSplitSources) {
				named("client") { associateWith(main) }
			}

			if (addDatagens) {
				named("datagen") { associateWith(main) }
			}
		}
	}
}
