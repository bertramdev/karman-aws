package com.bertramlabs.plugins.karman
import org.jets3t.service.*
import org.jets3t.service.security.AWSCredentials
import org.jets3t.service.impl.rest.httpclient.RestS3Service

class S3StorageProvider extends StorageProvider {
	String name = "Amazon S3"
	String accessKey
	String secret

	def getS3Service() {
		new RestS3Service(this.getAWSCredentials());
	}

	def getAWSCredentials() {
		new AWSCredentials(accessKey, secret);
	}

	Directory getDirectory(String name) {
		new S3Directory(name: name, provider: this)
	}

	

	def getDirectories() {
		def buckets = s3Service.listAllBuckets()
		buckets.collect { bucket -> directoryFromS3Bucket(bucket)}
	}

	private S3Directory directoryFromS3Bucket(bucket) {
		new S3Directory(name: bucket.name, provider: this)
	}

}