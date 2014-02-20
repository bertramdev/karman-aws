/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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