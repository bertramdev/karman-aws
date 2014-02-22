grails.project.work.dir = 'target'

grails.project.dependency.resolution = {
    inherits 'global'
    log "warn"
    legacyResolve false
    repositories {
        grailsCentral()
        grailsPlugins()
        mavenCentral()
    }

    dependencies {
        compile 'net.java.dev.jets3t:jets3t:0.9.0'
        build 'org.apache.httpcomponents:httpcore:4.2'
        build 'org.apache.httpcomponents:httpclient:4.2'
        runtime 'org.apache.httpcomponents:httpcore:4.2'
        runtime 'org.apache.httpcomponents:httpclient:4.2'
    }

    plugins {
        // runtime ":karman:0.1.1"
        build(":tomcat:$grailsVersion",
              ":release:2.2.1") {
            export = false
        }
    }
}

grails.plugin.location."karman" = "../karman"