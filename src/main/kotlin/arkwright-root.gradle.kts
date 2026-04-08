plugins {
	idea
}

tasks.named<Wrapper>("wrapper") {
	gradleVersion = "9.4.0"
	distributionSha256Sum = "b21468753cb43c167738ee04f10c706c46459cf8f8ae6ea132dc9ce589a261f2"
	distributionType = Wrapper.DistributionType.ALL
}

fun shouldBeExcluded(file: File): Boolean {
	if (file.isDirectory) {
		val excludedFolderNames = setOf("run", "build", ".kotlin")

		return file.name in excludedFolderNames
	}

	return false
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true

		excludeDirs.addAll(
			rootDir.walkTopDown().filter(::shouldBeExcluded)
		)
	}
}
