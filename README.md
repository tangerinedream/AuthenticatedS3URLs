AuthenticatedS3URLs
===================

Code to generate pre-authenticated S3 URLs 

Simply generate one or more URL's, provide to anyone you like including non AWS account holders and your content can be easily shared.

This solution leverages the AWS Java API to generate pre-authenticated URLs.  These URLs self contain an STS provided AccessID and public key encoded in the URL itself.  There are configuration options to customize the behavior of the pre-authenticated URL string, including S3 bucket, object, http/s protocol, and time to live after which the URL is no longer valid.

Gradle is used to build and run the application

# Build only
$ gradle build

or

# Build and run
$ gradle run