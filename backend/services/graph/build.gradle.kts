import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.protobuf")
}

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

dependencies {

    implementation(kotlin("reflect"))
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    // Protobuf & gRPC
    implementation("com.google.protobuf:protobuf-kotlin:3.25.3")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-protobuf:1.63.0")

    implementation("org.springframework.kafka:spring-kafka:4.0.2")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

// Protobuf Code Generation Configuration
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.63.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

// Point to the root 'proto' directory
sourceSets {
    main {
        proto {
            srcDir("../../../proto")
        }
    }
}