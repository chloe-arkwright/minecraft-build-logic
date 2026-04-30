plugins {
	idea
}

// https://gradle.org/release-checksums/
// https://docs.gradle.org/9.5.0/release-notes.html
tasks.named<Wrapper>("wrapper") {
	gradleVersion = "9.5.0"
	distributionSha256Sum = "a3c4ba4aca8f0075688b9c5b18939fd28e8cb4357c227da5c1d9f38343791439"
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
