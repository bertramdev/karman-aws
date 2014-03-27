package com.bertramlabs.plugins.karman.aws

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3ObjectSummary
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class S3DirectorySpec extends Specification {

    static DIRECTORY_NAME = 'bucket-1'

    S3StorageProvider provider
    S3Directory directory

    def setup() {
        provider = new S3StorageProvider(accessKey: 'ACCESS_KEY',
                secretKey: 'SECRET_KEY',
                region: 'eu-west-1')

        directory = new S3Directory(name: DIRECTORY_NAME, provider: provider)
    }

    void "Testing if directory exists"() {
        when:
        def valid = directory.exists()

        then:
        valid
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.doesBucketExist(DIRECTORY_NAME) >> {
                true
            }
            client
        }

        when:
        valid = directory.exists()

        then:
        !valid
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.doesBucketExist(DIRECTORY_NAME) >> {
                false
            }
            client
        }
    }

    void "Listing directory files"() {
        when:
        def files = directory.listFiles()

        then:
        files
        files.size() == 2
        files.first().name == 'key-1'
        files.first().provider == provider
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.listObjects(_) >> {
               [objectSummaries: [
                       new S3ObjectSummary(key: 'key-1', bucketName: DIRECTORY_NAME),
                       new S3ObjectSummary(key: 'key-2', bucketName: DIRECTORY_NAME)
               ]] as ObjectListing
            }
            client
        }
    }

    void "Getting file"() {
        when:
        def file = directory.getFile('file-1')

        then:
        file.name == 'file-1'
        file.provider == provider
        0 * provider.amazonWebService.getS3(_)

        when:
        file = directory['file-1']

        then:
        file.name == 'file-1'
        file.provider == provider
        0 * provider.amazonWebService.getS3(_)
    }

    void "Saving directory"() {
        given:
        String bucketName = 'new-bucket'
        def newBucket = new S3Directory(name: bucketName, provider: provider)

        when:
        newBucket.save()

        then:
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.createBucket(bucketName) >> {
                new Bucket(bucketName)
            }
            client
        }
    }

    void "Saving directory in a given region"() {
        given:
        String bucketName = 'new-bucket'
        String regionName = 'eu-west-1'
        def newBucket = new S3Directory(name: bucketName, provider: provider, region: regionName)

        when:
        newBucket.save()

        then:
        1 * provider.amazonWebService.getS3(_) >> {
            def client = Mock(AmazonS3Client)
            1 * client.createBucket(bucketName, regionName) >> {
                new Bucket(bucketName)
            }
            client
        }
    }

}