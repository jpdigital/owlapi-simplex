# Generated code

The code generators provided by **owlapi-simplex** can generate the following
code from an OWL ontology:

* Constants for all OWL entities in the ontology
* Repository classes for each OWL class in the ontology
* A loader class which loads the ontology

The generated code uses classes provided by the 
[owlapi-simplex-utils module](./owlapi-simplex-utils/index.html). Therefore you 
must **owlapi-simplex-utils** to your project.

## Constants

For each type of OWL entities (classes, object properties, data properties, 
individuals and annotation properties) classes with constants for their IRIs
are created. The package name(s) for these classes there generated from the
namespace part of the IRIs of the entities. The code generator converts
all characters in namespace part of the IRI which are not allowed in Java 
package names to characters which are allowed. For each type of entity 
a separate class is generated. The name of the classes contains the namoe
of the ontology and the type of entity.

## Repository classes

For each OWL class found in the ontology a repository like class is generated. 
The package name of these classes is generated from the namespace part of the
IRI of the class. The name of the class is the name of the OWL class. Each 
repository provides the following methods:

```
public Stream<OWLNamedIndividual> instances()
```

Retrieves a instances of the class from the ontology them as a stream..

```
public List<OWLNamedIndividual> getInstances()
```

Retrieves a instances of the class from the ontology and returns them as a list.

For each data property of the class a method is generated to filter the 
instances by the value of the data property. The method is available in two 
variants, one returning a stream and one returning a list.

```
public Stream<OWLNamedIndividual> instancesFilteredByDataProperty(String value)
```

```
public List<OWLNamedIndividual> getInstancesFilteredByDataProperty(String value)
```

## Ontology Loader

The ontology loader class can be generated in two variants. Both variants 
are subclasses of the [OwlApiSimplexUtilsBuilder](./owlapi-simplex-utils/apidocs/index.html?de/jpdigital/owlapisimplex/OwlApiSimplexUtilsBuilder.html) 
class. In both variants the 
`build()` method will return an instance of the 
[OwlApiSimplexUtils](./owlapi-simplex-utils/apidocs/index.html?de/jpdigital/owlapisimplex/OwlApiSimplexUtils.html)
class which can be used to create instances of the generated repository classes.

The first variant loads all OWL files of the ontology from the class path. This
variant is for example generated when using the Maven plugin. In this case
the generated loader class will have a parameterless constructor.

The second variant loads the OWL files of the ontology from the files system.
In this case the loader class requires the user to provide the paths to the
OWL files to load:

```
public OntologyLoader(final List<Path> paths)
```

The files must be in the correct order so that the OWL API can resolve all 
imports correctly.

In both cases the generated class will check if all required (sub-) ontologies
are available when the `build()` method is called.