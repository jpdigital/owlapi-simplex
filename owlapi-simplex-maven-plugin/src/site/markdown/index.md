# owlapi-simplex-maven-plugin

A Maven plugin which generates a easy to use API for an ontology stored as 
resource(s) in a Maven project.

The use it add the plugin to your build section:

```
...
<build>
    <plugins>
        ...
        <plugin>
            <groupId>de.jpdigital</groupId>
            <artifactId>owlapi-simplex-maven-plugin</artifactId>
            <version>0.1.0-SNAPSHOT</version>
            <configuration>
                <owlFiles>
                    <param>file1.owl</param>
                    <param>file2.owl</param>
                    ...
                </owlFiles>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>gen-api</goal>
                    </goals>
                    <phase>generate-sources</phase>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
...
```

Replace the version with the most current one available. Also replace the
paths in the `<owlFiles>` element with the correct paths to the resources.
The generated sources are automatically added to the sources to compile.

The generated sources use several classes provided by the 
[owlapi-simplex-utils](https://jpdigital.github.io/owlapi-simplex/owlapi-simplex-utils).
Therefore you have the add the *owlapi-simplex-utils* artifact to your 
dependencies:

```
...
<dependencies>
    ...
    <dependency>
        <groupId>de.jpdigital</groupId>
        <artifactId>owlapi-simplex-utils</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
    ...
</dependencies>
...
```

Replace the version with the most current one available.

The generated sources can be customized. Per default everything 
(constants for all entity types, repositories, loader class) is generated. This 
is equivalent to the following configuration:

```
...
<build>
    <plugins>
        ...
        <plugin>
            <groupId>de.jpdigital</groupId>
            <artifactId>owlapi-simplex-maven-plugin</artifactId>
            <version>0.1.0-SNAPSHOT</version>
            <configuration>
                <owlFiles>
                    <param>file1.owl</param>
                    <param>file2.owl</param>
                    ...
                </owlFiles>
                <generateIriConstantsForClasses>true<generateIriConstantsForClasses>
                <generateIriConstantsForObjectProperties>true</generateIriConstantsForObjectProperties>
                <generateIriConstantsForDataProperties>true</generateIriConstantsForDataProperties>
                <generateIriConstantsForIndividuals>true</generateIriConstantsForIndividuals>
                <generateIriConstantsForAnnotationProperties>true</generateIriConstantsForAnnotationProperties>
                <generateRepositories>false</generateRepositories>
                <generateOntologyLoader>false</generateOntologyLoader>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>gen-api</goal>
                    </goals>
                    <phase>generate-sources</phase>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
...
```

