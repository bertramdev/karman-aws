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
        mavenRepo 'http://dl.bintray.com/karman/karman'

    }

    dependencies {
        // build 'org.apache.httpcomponents:httpcore:4.2'
        // build 'org.apache.httpcomponents:httpclient:4.2'
        // runtime 'org.apache.httpcomponents:httpcore:4.2'
        // runtime 'org.apache.httpcomponents:httpclient:4.2'
        // runtime 'com.amazonaws:aws-java-sdk:1.7.1'
        compile('com.bertramlabs.plugins:karman-aws-groovy:0.4.0') {
            excludes 'karman-core'
        }
    }

    plugins {
        build ':tomcat:7.0.50.1'
        build(':release:3.0.1') {
            export = false
        }

        runtime ':aws-sdk:1.7.1'
        runtime ':karman:0.5.1'

        test ':code-coverage:1.2.7'
    }
}
//grails.plugin.location."karman" = "../karman"