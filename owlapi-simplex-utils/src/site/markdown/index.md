# owlapi-simplex-utils

This module provides several utilities for working with OWL ontologies. This
module can be used independently from the other module of 
[owlapi-simplex](https://jpdigital.github.io/owlapi-simplex).

Currently **owlapi-simplex-utils** provides three utility classes 

[Instances](./apidocs/index.html?de/jpdigital/owlapisimplex/Instances.html)
: Provides methods for retrieving individuals from the ontology

[ObjectProperties](./apidocs/index.html?de/jpdigital/owlapisimplex/ObjectProperties.html)
: Methods for retrieving the values of object properties

[DataProperties](./apidocs/index.html?de/jpdigital/owlapisimplex/DataProperties.html)
: Methods for retrieving the values of data properties

In addition there are two helper classes which can be used to load an
ontology from OWL files in the class path or from the file system:

[OwlApiSimplexUtilsFromPathsBuilder](./apidocs/index.html?de/jpdigital/owlapisimplex/OwlApiSimplexUtilsFromPathsBuilder.html)
: Loads an ontology from OWL files in the file system

[OwlApiSimplexUtilsFromResourcesBuilder](./apidocs/index.html?de/jpdigital/owlapisimplex/OwlApiSimplexUtilsFromResourcesBuilder.html)
: Loads an ontology from OWL files in the class path (resources).

Both classes will return an instance of the 
[OwlApiSimplexUtils](./apidocs/index.html?de/jpdigital/owlapisimplex/OwlApiSimplexUtils.html)
class. This class provides some utility methods for generating new
instances of the [Instances](./apidocs/index.html?de/jpdigital/owlapisimplex/Instances.html),
[ObjectProperties](./apidocs/index.html?de/jpdigital/owlapisimplex/ObjectProperties.html),
and [DataProperties](./apidocs/index.html?de/jpdigital/owlapisimplex/DataProperties.html)
classes.

More details can be found in the JavaDoc of the classes.