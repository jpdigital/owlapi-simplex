${license}
/**
This file was automatically generated by the OWL API Generator.
*/

package ${package};

public class ${className} extends ${baseClass}{
    
    <#if (baseClass == "de.jpdigital.owlapisimplex.OwlApiSimplexUtilsFromResourcesBuilder")>
    public ${className}() {
        super(Arrays.asList({
        <#list ontologySources as source>
        "${source}"<#sep>,</#sep>
        </#list>
        }));
    }
    <#else>
    public ${className}(final List<Path> paths) {
        super(paths);
    }
    </#if>

    @Override
    protected void validate(final List<OWLOntology> ontologies)
        throws OwlApiSimplexException {
        final List<IRI> ontologyIris = Arrays.asList({
            <#list ontologyIris as iri>
            "${iri}"<#sep>,</#sep>
            </#list>
        });
        for (final IRI iri : ontologyIris) {
            final boolean available = ontologies
                .stream()
                .anyMatch(
                    ontology -> ontology
                                    .getOntologyID()
                                    .getOntologyIRI()
                                    .equals(iri)
                ); 
            if (!available) {
                throw new OwlApiSimplexException(
                    String.format(
                        "Ontology %s has not been loaded.", iri
                    );
                );
            }
        }
    }
}
