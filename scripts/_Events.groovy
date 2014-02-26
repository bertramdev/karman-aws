
eventKarmanConfig = { configHolder ->
	def s3StorageProvider = classLoader.loadClass('com.bertramlabs.plugins.karman.aws.S3StorageProvider')
	
	configHolder.providerTypes += [
		s3: s3StorageProvider,
		aws: s3StorageProvider
	]
}
