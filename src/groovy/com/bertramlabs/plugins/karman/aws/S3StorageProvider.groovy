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

package com.bertramlabs.plugins.karman.aws

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.bertramlabs.plugins.karman.Directory
import com.bertramlabs.plugins.karman.StorageProvider
import grails.util.Holders

class S3StorageProvider extends StorageProvider {

    static String providerName = "s3"

    String accessKey = ''
    String secretKey = ''
    String region = ''

	Directory getDirectory(String name) {
		new S3Directory(name: name, provider: this)
	}

	List<Directory> getDirectories() {
		List<Bucket> buckets = s3Client.listBuckets()
        buckets.collect { bucket -> directoryFromS3Bucket(bucket)}
	}

    AmazonS3Client getS3Client() {
        AmazonS3Client client
        if (accessKey && secretKey) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey)
            client = new AmazonS3Client(credentials)
            if (region) {
                Region region = RegionUtils.getRegion(region)
                client.region = region
            }
        } else {
            return null
        }
        client
    }

    // PRIVATE

    private S3Directory directoryFromS3Bucket(bucket) {
		new S3Directory(
                name: bucket.name,
                provider: this
        )
	}

}