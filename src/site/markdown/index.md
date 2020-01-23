# Overview

The **owlapi-simplex** project provides some utilities to make accessing 
OWL ontologies easier. The standard way for working with OWL ontologies is the 
[http://owlcs.github.io/owlapi/](OWL API). Using this API often requires a lot
a boiler plate code. 

**owlapi-simplex** provides a code generator which can generate most of this
boiler plate code automatically for a specific OWL ontology. The project also
includes some utilities in the `owlapi-simplex-utils` which can be used 
independently from the code generators.

For invoking the code generators a CLI interface and a Maven plugin are provided.

The project especially the Maven plugin, are not yet available on Maven Central.
The project will be published to Maven Central soon. 