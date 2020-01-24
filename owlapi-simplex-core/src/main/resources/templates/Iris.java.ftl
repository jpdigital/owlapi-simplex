${license}
/**
This file was automatically generated by the OWL API Generator.
*/

package ${package};

import org.semanticweb.owlapi.model.IRI;

/**
 * Constants for all IRIs starting with ${baseIri}.
 */
public final class ${className} {

    private ${className}() {
        // This class only provides constants, therefore no instances can be 
        // created.
    }
    
    <#list iris as iri>
    public static final IRI ${iri.constantName} = IRI.create("${iri.value}");
    </#list>
}

