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
import org.apache.commons.io.IOUtils
import groovy.util.logging.Log4j
import java.io.ByteArrayInputStream;

@Log4j
class S3CloudFile extends CloudFile {
	S3Directory parent
	S3Object object
	def detailsOnly = true
	Boolean existsFlag = null

	S3Object getS3Object(metaOnly=false) {
		if(!this.exists()) {
			object = object ?: new S3Object(parent.s3Bucket, name)
			return object
		}
		if(!object || (metaOnly == false && detailsOnly == true)) {
			try {
				if(metaOnly) {
					object = provider.s3Service.getObjectDetails(parent.s3Bucket, name)
					detailsOnly = true
				} else {
					object = provider.s3Service.getObject(parent.s3Bucket, name)
					detailsOnly = false
				}
			} catch(S3ServiceException ex) {
				log.warn("Karman-AWS Error Fetching S3Object",ex)
			}
				
		}
		return object
	}

	InputStream getInputStream() {
		s3Object?.dataInputStream
	}

	String getText(String encoding=null) {
		def result = null
		if(encoding) {
			result = inputStream?.getText(encoding)
		} else {
			result = inputStream?.text
		}
		s3Object?.closeDataInputStream()
		return result
	}

	byte[] getBytes() {
		def result = inputStream?.bytes
		s3Object?.closeDataInputStream()
		return result
	}

	void setText(String text) {
		setBytes(text.bytes)
	}

	void setBytes(bytes) {
		InputStream is = new ByteArrayInputStream(bytes);
		s3Object?.setDataInputStream(is)
		s3Object?.setContentLength(bytes.length)

	}

	Long getContentLength() {
		getS3Object(true)?.contentLength
	}

	String getContentType() {
		getS3Object(true)?.contentType
	}

	void setContentType(String contentType) {
		s3Object?.setContentType(contentType)
	}

	Boolean exists() {
		if(existsFlag != null) {
			return existsFlag
		}
		try {
			provider.s3Service.getObjectDetails(parent.s3Bucket, name)
			existsFlag = true
		} catch(S3ServiceException ex) {
			println "Service Exception!"
			if(ex.responseCode == 404) {
				existsFlag = false
			}
		} 
		
		return existsFlag
	}

	def save() {
		def myObject = s3Object
		if(exists()) {
			delete()
		}
		provider.s3Service.putObject(parent.s3Bucket, s3Object);
		object = null
		existsFlag = true
	}

	def delete() {
		provider.s3Service.deleteObject(parent.s3Bucket, name)
		existsFlag = false
	}

}