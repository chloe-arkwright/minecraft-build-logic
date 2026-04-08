import gradle.kotlin.dsl.accessors._0cb39c16b209519d61ee18b0fceac003.jar
import gradle.kotlin.dsl.accessors._0cb39c16b209519d61ee18b0fceac003.java
import me.modmuss50.mpp.ReleaseType

plugins {
	id("me.modmuss50.mod-publish-plugin")
}

val props: NonNullPropertyDelegate = the()

val projectVersion: String by props
val projectName: String by props

val versionMinecraft: String by props
val versionFabricApi = findProperty("version.fabric.api")
val versionFabricKotlin = findProperty("version.fabric.kotlin")

publishMods {
	tasks.jar.orNull?.also { jar ->
		version = jar.archiveVersion
		file = jar.archiveFile
		type = if ("alpha" in projectVersion) {
			ReleaseType.BETA
		} else {
			ReleaseType.STABLE
		}
		displayName = "$projectName $projectVersion"
	}
	modLoaders = listOf("fabric")
	changelog = file("changelog.md").readText(Charsets.UTF_8)
	dryRun = providers.gradleProperty("app.arkwright.chloe.release.dryrun").map { it == "true" }.orElse(true)

	project.findProperty("project.modrinth.id")?.let { modrinthProjectId ->
		modrinth {
			accessToken = providers.gradleProperty("app.arkwright.chloe.release.modrinth")
			minecraftVersions.convention(listOf(versionMinecraft))

			projectId.convention(modrinthProjectId as String)

			if (versionFabricApi != null) {
				requires("fabric-api")
			}

			if (versionFabricKotlin != null) {
				requires("fabric-language-kotlin")
			}
		}
	}

	project.findProperty("project.curseforge.id")?.let { curseforgeProjectId ->
		curseforge {
			accessToken = providers.gradleProperty("app.arkwright.chloe.release.curseforge")
			minecraftVersions.convention(listOf(versionMinecraft))
			javaVersions.convention(listOf(java.targetCompatibility))

			projectId.convention(curseforgeProjectId as String)

			if (versionFabricApi != null) {
				requires("fabric-api")
			}

			if (versionFabricKotlin != null) {
				requires("fabric-language-kotlin")
			}
		}
	}
}
