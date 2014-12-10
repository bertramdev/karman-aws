import com.bertramlabs.plugins.karman.KarmanConfigHolder
import com.bertramlabs.plugins.karman.aws.S3StorageProvider

class KarmanAwsGrailsPlugin {
    def version         = "0.6.1"
    def grailsVersion   = "2.0 > *"
    def title           = "Karman AWS Plugin"
    def author          = "David Estes"
    def authorEmail     = "destes@bcap.com"
    def description     = 'Karman AWS provides an Amazon S3 Interface to the Karman API'
    def documentation   = "http://github.com/bertramdev/karman-aws"
    def license         = "APACHE"
    def organization    = [name: "Bertram Labs", url: "http://www.bertramlabs.com/"]
    def issueManagement = [ system: "GITHUB", url: "http://github.com/bertramdev/karman-aws/issues" ]
    def scm             = [ url: "http://github.com/bertramdev/karman-aws" ]
    def pluginExcludes  = [
    ]
    def developers      = [ [name: 'Brian Wheeler'], [name: 'Benoit Hediard'] ]


    def doWithApplicationContext = { applicationContext ->
        def config = application.config.grails.plugins.karman
    }
}
