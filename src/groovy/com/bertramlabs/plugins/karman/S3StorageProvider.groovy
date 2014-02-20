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