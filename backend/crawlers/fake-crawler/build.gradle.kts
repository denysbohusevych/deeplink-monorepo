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
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")

    implementation("com.google.protobuf:protobuf-kotlin:3.25.3")
    implementation("io.grpc:grpc-protobuf:1.63.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("kotlin")
                // id("java") <-- УДАЛЕНО, так как Java-код генерируется автоматически
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("../../../proto")
        }
    }
}