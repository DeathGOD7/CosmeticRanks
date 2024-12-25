import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	id("java")
	id("maven-publish")
	//id("com.github.johnrengelman.shadow") version "8.1.1" // old and unmaintained
	id("com.gradleup.shadow") version "9.0.0-beta4" // new and maintained version of shadow
}

group = "io.github.deathgod7.cosmeticranks"
version = "1.0.0"
description = "Custom ranks, prestige, and many more... elevate your player experience!"

repositories {
	mavenLocal()
	mavenCentral()

	// ---------- [ Paper MC ] ----------
	maven(url = "https://repo.papermc.io/repository/maven-public/")

	// ---------- [ Github Package ] ----------
	maven(url = "https://maven.pkg.github.com/DeathGOD7/SE7ENLib")

	// ---------- [ Sonatype ] ----------
	maven(url = "https://oss.sonatype.org/content/groups/public/")

	// ---------- [ Jitpack ] ----------
	maven(url = "https://jitpack.io/")

	// ---------- [ CodeMC ] ----------
	maven(url = "https://repo.codemc.org/repository/maven-public/")

	// ---------- [ Apache Maven ] ----------
	maven(url = "https://repo.maven.apache.org/maven2/")

	// ---------- [ Redempt / Redlib ] ----------
	maven(url = "https://redempt.dev")

	// ---------- [ Triumph Team ] ----------
	maven(url = "https://repo.triumphteam.dev/snapshots/")

	maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")

}

dependencies {
	// ---------- [ PaperMC ] ----------
	compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

	// ---------- [ LuckPerms ] ----------
	compileOnly("net.luckperms:api:5.4")

	// ---------- [ SE7ENLib ] ----------
	implementation("io.github.deathgod7:SE7ENLib:1.1.1-rc3")

	// ---------- [ Triumph CMD/GUI ] ----------
	implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-ALPHA-10")
	implementation("dev.triumphteam:triumph-gui:3.1.11")

	// ---------- [ Adventure ] ----------
	implementation("net.kyori:adventure-platform-bukkit:4.3.2")
	implementation("net.kyori:adventure-text-minimessage:4.16.0")

	// ---------- [ Yaml ] ----------
	implementation("com.amihaiemil.web:eo-yaml:7.2.0")

	// ---------- [ PlaceholderAPI ] ----------
	compileOnly("me.clip:placeholderapi:2.11.5")

	// ---------- [ Test - JUnit ] ----------
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<ShadowJar> {
	configurations = listOf(project.configurations.runtimeClasspath.get())
	minimize()
	mergeServiceFiles()
	archiveFileName.set("${project.name}-${project.version}-all.jar")

	relocate("dev.triumphteam.gui", "io.github.deathgod7.cosmeticranks.gui")
	relocate("dev.triumphteam.cmd", "io.github.deathgod7.cosmeticranks.cmd")
}


tasks {
	build {
		dependsOn(shadowJar)
	}
}

val sourceCompatibility = JavaVersion.VERSION_1_8
val targetCompatibility = JavaVersion.VERSION_1_8
val targetJavaVersion = 11

java {
	val javaVersion = JavaVersion.toVersion(targetJavaVersion)
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
	if (JavaVersion.current() < javaVersion) {
		toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
	}
}

extensions.findByName("develocity")?.withGroovyBuilder {
	getProperty("buildScan")?.withGroovyBuilder {
		setProperty("termsOfUseUrl", "https://gradle.com/help/legal-terms-of-use")
		setProperty("termsOfUseAgree", "yes")
	}
}

// for publishing in jitpack
publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "io.github.deathgod7"
			artifactId = "CosmeticRanks"
			version = "1.0.0"

			from(components["java"])
		}
	}
}

tasks.withType<JavaCompile>().configureEach {
	if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
		options.release.set(targetJavaVersion)
	}
}

tasks.test {
	useJUnitPlatform()
}


tasks.processResources {

	val placeholders = mapOf(
			"version" to version
	)

	filesMatching("plugin.yml") {
		expand(placeholders)
	}
}
