import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.webjars:bootstrap:5.2.2")
	implementation("org.webjars:jquery:3.6.1")
	implementation("com.google.apis:google-api-services-drive:v3-rev20221023-2.0.0")
	implementation("com.google.api-client:google-api-client:2.0.1")
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.13")
	implementation("io.springfox:springfox-swagger-ui:2.9.2")
	implementation("io.springfox:springfox-swagger2:2.9.2")

	compileOnly("org.projectlombok:lombok:1.18.24")
	annotationProcessor("org.projectlombok:lombok:1.18.24")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.codehaus.groovy:groovy-all:2.5.8")
	testImplementation("org.spockframework:spock-core:1.0-groovy-2.4")
	testImplementation("org.objenesis:objenesis:2.2")
	testImplementation("cglib:cglib-nodep:3.2.0")

	testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
