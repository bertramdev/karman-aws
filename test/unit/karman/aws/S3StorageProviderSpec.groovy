package karman.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.bertramlabs.plugins.karman.S3StorageProvider
import grails.plugin.awssdk.AmazonWebService
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class S3StorageProviderSpec extends Specification {

    S3StorageProvider provider

    def setup() {
        provider = new S3StorageProvider()
        provider.amazonWebService = Mock(AmazonWebService)
    }

    void "Getting directories"() {
        when:
        def directories = provider.getDirectories()

        then:
        directories
        directories.size() == 2
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.listBuckets() >> {
                [new Bucket('bucket-1'), new Bucket('bucket-2')]
            }
            client
        }
    }

    void "Getting directory"() {
        when:
        def directory = provider.getDirectory('bucket-1')

        then:
        directory
        directory.name == 'bucket-1'
        directory.provider == provider
        0 * provider.amazonWebService.getS3(_)

        when:
        directory = provider['bucket-1']

        then:
        directory
        directory.name == 'bucket-1'
        directory.provider == provider
        0 * provider.amazonWebService.getS3(_)
    }

}
