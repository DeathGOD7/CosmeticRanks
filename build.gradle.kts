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
	maven (url = "https://jitpack.io/")

	// ---------- [ CodeMC ] ----------
	maven(url = "https://repo.codemc.org/repository/maven-public/")

	// ---------- [ Apache Maven ] ----------
	maven(url ="https://repo.maven.apache.org/maven2/")

}

dependencies {
	// ---------- [ PaperMC ] ----------
	compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

	// ---------- [ SE7ENLib ] ----------
	implementation("com.github.DeathGOD7:SE7ENLib:master-SNAPSHOT")

	// ---------- [ Test - JUnit ] ----------
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<ShadowJar> {
	minimize()
	mergeServiceFiles()
	archiveFileName.set("${project.name}-${project.version}-shadow.jar")
}


tasks {
	build {
		dependsOn(shadowJar)
	}
}

val targetJavaVersion = 8
tasks.withType<JavaCompile> {
	val sourceCompatibility = JavaVersion.VERSION_1_8
	val targetCompatibility = JavaVersion.VERSION_1_8
}

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
