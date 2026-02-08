plugins {
    java
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.umdalecs.javitita.App")
}

dependencies {
    implementation("com.formdev:flatlaf:3.7")
}