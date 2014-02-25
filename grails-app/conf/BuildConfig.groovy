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
        build 'org.apache.httpcomponents:httpcore:4.2'
        build 'org.apache.httpcomponents:httpclient:4.2'
        runtime 'org.apache.httpcomponents:httpcore:4.2'
        runtime 'org.apache.httpcomponents:httpclient:4.2'
    }

    plugins {
        build ':tomcat:7.0.50.1'
        build(':release:3.0.1') {
            export = false
        }

        runtime ':aws-sdk:1.7.1'
        runtime ':karman:0.1.2'

        test ':code-coverage:1.2.7'
    }
}
// grails.plugin.location."karman" = "../karman"