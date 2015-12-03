grails.project.work.dir = 'target'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'
    legacyResolve false
    repositories {
        mavenLocal()
        grailsCentral()
        grailsPlugins()
        mavenCentral()
    }

    dependencies {
        // build 'org.apache.httpcomponents:httpcore:4.2'
        // build 'org.apache.httpcomponents:httpclient:4.2'
        // runtime 'org.apache.httpcomponents:httpcore:4.2'
        // runtime 'org.apache.httpcomponents:httpclient:4.2'
        runtime 'com.amazonaws:aws-java-sdk:1.10.33'
        compile('com.bertramlabs.plugins:karman-aws:0.9.9') {
            excludes 'karman-core'
        }
    }

    plugins {
        build ':tomcat:7.0.52.1'
        build(':release:3.1.2') {
            export = false
        }
        runtime ':karman:0.9.9'

        test ':code-coverage:1.2.7'
    }
}
//grails.plugin.location."karman" = "../karman"
