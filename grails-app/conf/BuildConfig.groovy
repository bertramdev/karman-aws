grails.project.work.dir = 'target'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'
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
        runtime ':karman:0.1.1'
        build ':tomcat:7.0.50.1'
        runtime ':hibernate:3.6.10.8'

        build(':release:3.0.1') {

            export = false
        }
    }
}
//grails.plugin.location."karman" = "../karman"