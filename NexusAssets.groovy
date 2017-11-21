import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

def request = new JsonSlurper().parseText(args)
assert request.repoName: 'repoName parameter is required'
assert request.repoName instanceof List: 'repoName must be a JSON array of repo names'
assert request.startDate: 'startDate parameter is required, format: yyyy-mm-dd'
assert request.contentType: 'contentType parameter is required, format: application/java-archive or application/x-tgz'

def urls = []
for (String repoName : request.repoName) {
	log.info("Gathering Asset list for repository: ${repoName} as of startDate: ${request.startDate}")

	def repo = repository.repositoryManager.get(repoName)
	StorageFacet storageFacet = repo.facet(StorageFacet)
	def tx = storageFacet.txSupplier().get()

	tx.begin()

	Iterable<Asset> assets = tx.findAssets(Query.builder().where('content_type').eq(request.contentType).and('last_updated > ').param(request.startDate).build(), [repo])
	def names = assets.collect { "${it.name()}" }

	tx.commit()

	log.info("${names.size()} assets added to ${repoName} since ${request.startDate}")
	urls.add(names);
}

def result = JsonOutput.toJson([
    assets  : urls
])
return result

