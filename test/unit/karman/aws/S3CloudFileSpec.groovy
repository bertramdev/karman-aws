package karman.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.bertramlabs.plugins.karman.S3CloudFile
import com.bertramlabs.plugins.karman.S3Directory
import com.bertramlabs.plugins.karman.S3StorageProvider
import grails.plugin.awssdk.AmazonWebService
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class S3CloudFileSpec extends Specification {

    static DIRECTORY_NAME = 'bucket-1'
    static FILE_NAME = 'file-1'

    S3StorageProvider provider
    S3Directory directory
    S3CloudFile file

    def setup() {
        provider = new S3StorageProvider()
        provider.amazonWebService = Mock(AmazonWebService)
        directory = new S3Directory(name: DIRECTORY_NAME, provider: provider)
        file = new S3CloudFile(name: FILE_NAME,parent: directory)
    }

    void "Testing if file exists"() {
        when:
        def valid = file.exists()

        then:
        valid
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.listObjects(DIRECTORY_NAME, FILE_NAME) >> {
                [objectSummaries: [new S3ObjectSummary(key: FILE_NAME, bucketName: DIRECTORY_NAME)]] as ObjectListing
            }
            client
        }
    }

    void "Getting file text"() {
        when:
        def text = file.text

        then:
        text == 'Some text'
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.getObject(DIRECTORY_NAME, FILE_NAME) >> {
                new S3Object(
                        bucketName: directory.name,
                        key: file.name,
                        objectContent: new S3ObjectInputStream(new ByteArrayInputStream('Some text'.bytes), null)
                )
            }
            client
        }
    }

    void "Getting file UTF-8 text"() {
        when:
        def text = file.getText('UTF-8')

        then:
        text == 'Texte en français'
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.getObject(DIRECTORY_NAME, FILE_NAME) >> {
                new S3Object(
                        bucketName: directory.name,
                        key: file.name,
                        objectContent: new S3ObjectInputStream(new ByteArrayInputStream('Texte en français'.bytes), null)
                )
            }
            client
        }
    }

    void "Getting file bytes"() {
        when:
        def bytes = file.bytes

        then:
        bytes == 'Some text'.bytes
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.getObject(DIRECTORY_NAME, FILE_NAME) >> {
                new S3Object(
                        bucketName: directory.name,
                        key: file.name,
                        objectContent: new S3ObjectInputStream(new ByteArrayInputStream('Some text'.bytes), null)
                )
            }
            client
        }
    }

    void "Getting file input stream"() {
        when:
        def inputStream = file.inputStream

        then:
        inputStream
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.getObject(DIRECTORY_NAME, FILE_NAME) >> {
                new S3Object(
                        bucketName: directory.name,
                        key: file.name,
                        objectContent: new S3ObjectInputStream(new ByteArrayInputStream('Some text'.bytes), null)
                )
            }
            client
        }
    }

    void "Saving file"() {
        given:
        file.text = "Setting some value to this file"

        when:
        file.save()

        then:
        !file.summary
        !file.object
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.putObject(DIRECTORY_NAME, FILE_NAME, _, _)
            client
        }
    }

    void "Deleting file"() {
        when:
        file.delete()

        then:
        file.existsFlag == false
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.deleteObject(DIRECTORY_NAME, FILE_NAME)
            client
        }
    }

}
