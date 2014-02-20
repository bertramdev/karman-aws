package com.bertramlabs.plugins.karman
import org.jets3t.service.model.*
import org.jets3t.service.*

class S3Directory extends com.bertramlabs.plugins.karman.Directory {
	String region
	S3Bucket getS3Bucket() {
		new S3Bucket(name)
	}

	Boolean exists() {
		provider?.s3Service?.checkBucketStatus(name) == org.jets3t.service.StorageService.BUCKET_STATUS__MY_BUCKET
	}

	List listFiles(options=[:]) {
		def objects = provider.s3Service.listObjects(s3Bucket,options?.prefix,options?.delimiter)
		objects.collect { object -> cloudFileFromS3Object(object) }
	}

	def save() {

	}

	CloudFile getFile(String name) {
		new S3CloudFile(provider: provider, parent: this, name: name)
	}


	private S3CloudFile cloudFileFromS3Object(object) {
		new S3CloudFile(provider: provider, parent: this, name: object.key, object: object, existsFlag: true)
	}
}