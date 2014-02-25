package karman.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.Headers
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.bertramlabs.plugins.karman.CloudFileACL
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

    void "Getting file presigned URL"() {
        when:
        def URL = file.getURL(new Date() + 1)

        then:
        URL
        URL.toString() == 'http://some.presigned.url'
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.generatePresignedUrl(DIRECTORY_NAME, FILE_NAME, _) >> new URL('http://some.presigned.url')
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

    void "Setting meta attribute"() {
        when:
        file.setMetaAttribute(Headers.CACHE_CONTROL, 'someValue')

        then:
        file.object.objectMetadata.cacheControl == 'someValue'

        when:
        file.setMetaAttribute(Headers.CONTENT_DISPOSITION, 'someValue')

        then:
        file.object.objectMetadata.contentDisposition == 'someValue'

        when:
        file.setMetaAttribute(Headers.CONTENT_ENCODING, 'someValue')

        then:
        file.object.objectMetadata.contentEncoding == 'someValue'

        when:
        file.setMetaAttribute(Headers.CONTENT_LENGTH, 1000)

        then:
        file.object.objectMetadata.contentLength == 1000

        when:
        file.setMetaAttribute(Headers.CONTENT_MD5, 'someValue')

        then:
        file.object.objectMetadata.contentMD5 == 'someValue'

        when:
        file.setMetaAttribute(Headers.CONTENT_TYPE, 'someValue')

        then:
        file.object.objectMetadata.contentType == 'someValue'

        when:
        file.setMetaAttribute(Headers.EXPIRES, new Date())

        then:
        file.object.objectMetadata.httpExpiresDate

        when:
        file.setMetaAttribute('some-user-data', 'someValue')

        then:
        file.object.objectMetadata.userMetadata['some-user-data'] == 'someValue'
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

    void "Saving file with canned ACL"() {
        given:
        file.text = "Setting some value to this file"

        when:
        file.save(CloudFileACL.PublicRead)

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
