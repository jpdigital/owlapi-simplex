# owlapi-simplex-cli

A CLI interface for the code generators of 
[owlapi-simplex](https://jpdigital.github.io/owlapi-simplex).

The CLI interface generates the classes with constants, repositories and the
loader class for the provided ontology in a directory. The generated 
loader class expects the paths to the ontology documents to load 
as parameter.

## Usage

```
Usage: OwlApiGeneratorCli [-hV] [--[no-]annotation-props-iris] [--[no-]
                          class-iris] [--[no-]data-props-iris] [--[no-]
                          individual-iris] [--[no-]loader] [--[no-]
                          obj-props-iris] [--[no-]repositories] <outputDirPath>
                          [<ontologyPaths>...]
Generates an ontology specific API for an OWL ontology
      <outputDirPath>        The output directory. The generated sources will
                               be placed here.
      [<ontologyPaths>...]   The ontology(ies) for which the API is generated.
                               The files are loaded in the order they are
                               provided. The last ontology is used as source
                               and must import all other ontologies.
  -h, --help                 Show this help message and exit.
      --[no-]annotation-props-iris
                             Generate IRI constants for annotation properties?
      --[no-]class-iris      Generate IRI constants for classses?
      --[no-]data-props-iris Generate IRI constants for data properties?
      --[no-]individual-iris Generate IRI constants for individuals?
      --[no-]loader          Generate OntologyLoader?
      --[no-]obj-props-iris  Generate IRI constants for object properties?
      --[no-]repositories    Generate repostories?
  -V, --version              Print version information and exit.
```
