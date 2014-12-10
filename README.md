Karman AWS
=============

Karman AWS is a S3 implementation of the Karman Cloud Service / Storage Interface. It allows one to interact with Amazon S3 via the standard Karman API interfaces


Usage / Documentation
---------------------

To instantiate an S3 provider simply do:

```groovy
import com.bertramlabs.plugins.karman.*

// To use credentials from Config.groovy or credential provider chain
def provider = StorageProvider.create(provider: 's3')
// Or
def provider = StorageProvider.create(
    provider: 's3',
    accessKey: ACCESS_KEY,
    secretKey: SECRET_KEY,
    region: 'eu-west-1' //optional
)

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