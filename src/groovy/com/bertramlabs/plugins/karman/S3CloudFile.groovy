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

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.*

class S3CloudFile extends CloudFile {

    S3Directory parent
	S3Object object
    S3ObjectSummary summary // Only set when object is retrieved by listFiles

    private Boolean loaded = false
	private Boolean metaDataLoaded = false
    private Boolean existsFlag = null

    /**
     * Content length metadata
     * @return
     */
    Long getContentLength() {
        if (!metaDataLoaded) {
            loadObjectMetaData()
        }
        s3Object.objectMetadata.contentLength
    }
    void setContentLength(int contentLength) {
        s3Object.objectMetadata.contentLength = contentLength
    }

    /**
     * Content type metadata
     * @return
     */
    String getContentType() {
        if (!metaDataLoaded) {
            loadObjectMetaData()
        }
        s3Object.objectMetadata.contentType
    }
    void setContentType(String contentType) {
        s3Object.objectMetadata.contentType = contentType
    }

    /**
     * Bytes setter/getter
     * @param bytes
     */
    byte[] getBytes() {
        def result = inputStream?.bytes
        inputStream.close()
        return result
    }
    void setBytes(bytes) {
        s3Object.objectContent = new S3ObjectInputStream(new ByteArrayInputStream(bytes), null)
        setContentLength(bytes.length)
    }

    /**
     * Input stream getter
     * @return
     */
    InputStream getInputStream() {
        if (!object) {
            loadObject()
        }
		s3Object.objectContent
	}

    /**
     * Text setter/getter
     * @param encoding
     * @return
     */
	String getText(String encoding = null) {
		def result
		if (encoding) {
			result = inputStream?.getText(encoding)
		} else {
			result = inputStream?.text
		}
		inputStream?.close()
		return result
	}
    void setText(String text) {
		setBytes(text.bytes)
	}

    /**
     * Get URL or pre-signed URL if expirationDate is set
     * @param expirationDate
     * @return
     */
    String getURL(Date expirationDate = null) {
        if (expirationDate) {
            s3Client.generatePresignedUrl(parent.name, name, expirationDate)
        } else {
            "${s3Client.endpoint}/${parent.name}/${name}"
        }
    }

    /**
     * Check if file exists
     * @return
     */
	Boolean exists() {
		if (existsFlag != null) {
			return existsFlag
		}
        if (!name) {
            return false
        }
        //try {
            ObjectListing objectListing = s3Client.listObjects(parent.name, name)
            if (objectListing.objectSummaries) {
                summary = objectListing.objectSummaries.first()
                existsFlag = true
            } else {
                existsFlag = false
            }
        //} catch (AmazonS3Exception exception) {
            //log.warn(exception)
        //} catch (AmazonClientException exception) {
            //log.warn(exception)
        //}
		return existsFlag
	}

    /**
     * Save file
     * @return
     */
	def save(CannedAccessControlList cannedAccessControlList = null) {
		/*if (exists()) {
			delete()
		}*/
        if (cannedAccessControlList) {
            s3Object.objectMetadata.setHeader('x-amz-acl', cannedAccessControlList)
        }
		s3Client.putObject(parent.name, name, inputStream, object.objectMetadata)
		object = null
        summary = null
		existsFlag = true
	}

    /**
     * Delete file
     * @return
     */
	def delete() {
        s3Client.deleteObject(parent.name, name)
		existsFlag = false
	}

    void setMetaAttribute(key, value) {
        s3Object.objectMetadata[key] = value
    }
    
    void getMetaAttribute(key) {
        s3Object.objectMetadata[key]
    }

    void getMetaAttributes() {
        s3Object.objectMetadata
    }

    void removeMetaAttribute(key) {
        s3Object.objectMetadata.remove(key)
    }

    // PRIVATE

    private AmazonS3Client getS3Client() {
        parent.provider.s3Client
    }

    private S3Object getS3Object() {
        if (!object) {
            object = new S3Object(bucketName: parent.name, key: name)
            loaded = false
        }
        object
    }

    private void loadObject() {
        object = s3Client.getObject(parent.name, name)
        loaded = true
        metaDataLoaded = false
    }

    private void loadObjectMetaData() {
        s3Object.objectMetadata = s3Client.getObjectMetadata(parent.name, name)
        metaDataLoaded = true
    }

}