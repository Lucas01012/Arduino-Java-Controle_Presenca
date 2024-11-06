plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("com.fazecast:jSerialComm:2.9.2")
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation ("org.apache.poi:poi:5.2.3")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
    implementation ("org.jdatepicker:jdatepicker:1.3.4")
}

tasks.test {
    useJUnitPlatform()
}