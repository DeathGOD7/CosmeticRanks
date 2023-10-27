import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	id("java")
	id("maven-publish")
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.deathgod7.cosmeticranks"
version = "1.0.0"
description = "Custom ranks, prestige, and many more... elevate your player experience!"

repositories {
	mavenLocal()
	mavenCentral()

	// ---------- [ Paper MC ] ----------
	maven(url = "https://repo.papermc.io/repository/maven-public/")

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

}

dependencies {
	// ---------- [ PaperMC ] ----------
	compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

	// ---------- [ LuckPerms ] ----------
	compileOnly("net.luckperms:api:5.4")

	// ---------- [ SE7ENLib ] ----------
	implementation("com.github.deathgod7:SE7ENLib:master-SNAPSHOT")

	// ---------- [ RedLibs ] ----------
	implementation("com.github.Redempt:RedLib:6.5.8")

	// ---------- [ Triumph GUI ] ----------
//	implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT")
	implementation("dev.triumphteam:triumph-gui:3.1.6")

	// ---------- [ Adventure ] ----------
	implementation("net.kyori:adventure-platform-bukkit:4.3.1")
	implementation("net.kyori:adventure-text-minimessage:4.14.0")

	// ---------- [ Test - JUnit ] ----------
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<ShadowJar> {
	minimize()
	mergeServiceFiles()
	archiveFileName.set("${project.name}-${project.version}-all.jar")

	relocate("dev.triumphteam.gui", "com.github.deathgod7.cosmeticranks.gui")
}


tasks {
	build {
		dependsOn(shadowJar)
	}
}

val targetJavaVersion = 8
val sourceCompatibility = JavaVersion.VERSION_1_8
val targetCompatibility = JavaVersion.VERSION_1_8

java {
	val javaVersion = JavaVersion.toVersion(targetJavaVersion)
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
	if (JavaVersion.current() < javaVersion) {
		toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
	}
}

if (hasProperty("buildScan")) {
	extensions.findByName("buildScan")?.withGroovyBuilder {
		setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
		setProperty("termsOfServiceAgree", "yes")
	}
}

// for publishing in jitpack
publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "com.github.deathgod7"
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
	expand (
			"version" to version
	)
}
