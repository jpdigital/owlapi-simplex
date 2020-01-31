# Overview

The **owlapi-simplex** project provides some utilities to make accessing 
OWL ontologies easier. The standard way for working with OWL ontologies is the 
[http://owlcs.github.io/owlapi/](OWL API). Using this API often requires a lot
a boiler plate code. 

**owlapi-simplex** provides a code generator which can generate some of this
boiler plate code automatically for a specific OWL ontology. The project also
includes some utilities in the `owlapi-simplex-utils` module which can be used 
independently from the code generators.

For invoking the code generators a CLI interface and a Maven plugin are provided.
Instructions for using the the [Maven plugin](./owlapi-simplex-maven-plugin/index.html) 
and the [CLI interface](./owlapi-simplex-cli/index.html) can be found
on the pages of their Maven modules.

The code generators can generate the following code:

* Classes with constants for all entity types in an OWL ontology:
** Classes
** ObjectProperties
** DataProperties
** Individuals
** AnnotationProperties
* Repository classes for each OWL class with methods for
** Retrieving all instances of class
** Retrieving all instances with a specific value for a data property
* A loader class which loads the ontology

More information about the generated code can be found on the 
[Generated code](./generated-code.html) page.

As indidicate by the version the owlapi-simplex project is currently in its
early stages. It is usable but may not work with all ontologies or produce 
unexpected results. Before opening an issue to suggest a feature 
please look at the [Roadmap](./roadmap.html) if the feature is already in
there. 

If you have an OWL ontology for which owlapi-simplex does not work or produces
unexpected results please on issue and attach an ontology which allows it
to reproduce the issue.