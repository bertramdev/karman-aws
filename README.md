Karman AWS
=============

Karman AWS is a S3 implementation of the Karman Cloud Service / Storage Interface. It allows one to interact with Amazon S3 via the standard Karman API interfaces


Configuration
-------------

Since the plugin uses [AWS SDK](http://grails.org/plugin/aws-sdk) Grails Plugin, you can add your AWS credentials parameters to your grails-app/conf/Config.groovy :

```groovy
grails.plugin.awssdk.accessKey = {ACCESS_KEY}
grails.plugin.awssdk.secretKey = {SECRET_KEY}
```

If you do not provide credentials in grails-app/conf/Config.groovy, a credentials provider chain will be used that searches for credentials in this order:

- Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY
- Java System Properties - aws.accessKeyId and aws.secretKey
- Instance profile credentials delivered through the Amazon EC2 metadata service (IAM role)


Usage / Documentation
---------------------

To instantiate an S3 provider simply do:

```groovy
import com.bertramlabs.plugins.karman.*
def provider = new S3StorageProvider()

//example getting file contents
def file = provider['mybucket']['example.txt']
return file.text
```


Check the Karman API Documentation for details on how to interace with cloud files:

http://bertramdev.github.io/karman


Contributions
-------------
All contributions are of course welcome as this is an ACTIVE project. Any help with regards to reviewing platform compatibility, adding more tests, and general cleanup is most welcome.
Thanks to several people for suggestions throughout development.