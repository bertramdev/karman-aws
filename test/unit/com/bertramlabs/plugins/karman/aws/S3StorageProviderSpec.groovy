package com.bertramlabs.plugins.karman.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
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

    void "Getting s3Client provider"() {
        when:
        def client = provider.s3Client

        then:
        client
        1 * provider.amazonWebService.getS3('') >> {
            Mock(AmazonS3Client)
        }
    }

    void "Getting s3Client provider with region"() {
        given:
        provider.region = 'eu-west-1'

        when:
        def client = provider.s3Client

        then:
        client
        1 * provider.amazonWebService.getS3('eu-west-1') >> {
            Mock(AmazonS3Client)
        }
    }

    void "Getting s3Client provider with credentials and region"() {
        provider = new S3StorageProvider(
                accessKey: 'ACCESS_KEY',
                secretKey: 'SECRET_KEY',
                region: 'eu-west-1'
        )

        when:
        def client = provider.s3Client

        then:
        client
        client instanceof AmazonS3Client
        client.region.toString() == 'eu-west-1'
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
