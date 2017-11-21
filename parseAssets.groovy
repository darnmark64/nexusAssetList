import groovy.json.JsonSlurper

CliBuilder cli = new CliBuilder(
    usage: 'groovy parseAssets.groovy -f assetFile.json -t textFile.txt')
cli.with {
  f longOpt: 'file', args: 1, required: true, 'Repository asset file in JSON format is required'
  t longOpt: 'textFile', args: 1, required: true, 'Text file name is required'
}
def options = cli.parse(args)
if (!options) {
  return
}

def file = new File(options.f)
assert file.exists()

def jsonFile = new JsonSlurper().parseText(file.getText('UTF-8'))
assert jsonFile.result: 'result is missing'

def result = new JsonSlurper().parseText(jsonFile.result)
def assets = result.assets
assert assets instanceof List
print "${assets.size()} asset arrays"

def names = []
for (List list : assets) {
	if (list.size() > 0) {
		for (String name : list) {
			names.add(name)
		}
	}
}

File textFile = new File(options.t)
textFile.withWriter{ out ->
  names.sort().each {out.println it}
}



