# ${project.parent.parent.name}
![Maven Central Version](https://img.shields.io/maven-central/v/${project.parent.parent.groupId}/${project.parent.parent.artifactId})
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build status](https://ci.appveyor.com/api/projects/status/3sv7r52xqm0ja2mi?svg=true)](https://ci.appveyor.com/project/ykazakov/elk-reasoner)

${project.parent.parent.description}

For a detailed information, see the [project wiki](${elk.wiki}). 

## Features

ELK is an [ontology reasoner](https://en.wikipedia.org/wiki/Semantic_reasoner) implementing a polynomial-time goal-directed [consequence-based procedure](https://doi.org/10.1007/s10817-013-9296-3) for a fragment of [OWL 2 EL](https://www.w3.org/TR/owl2-profiles/#OWL_2_EL).

It is distinguishing features include:

#### Concurrent computation: 
	
ELK can take advantage of *multi-core processors* to speed-up the computation of reasoning results.
	
#### Incremental reasoning: 
	
ELK can update the reasoning results *incrementally* after changes in the ontology by only re-computing the reasoning results that depend on the changed axioms. 
In many cases, reasoning results, such as ontology class hierarchy, can be updated almost in real time
	
#### Generation of explanations:
    
ELK can *explain* logical consequences of ontologies by showing how consequences are derived, step-by-step from the axioms of the ontology:

<img width="868" alt="Explaining why American is a CheesyPizza" src="https://github.com/liveontologies/elk-reasoner/assets/2140361/515e1a68-4fdc-4699-824d-74b4bb3211f6">

## Usage

ELK is provided in several [distribution packages](https://github.com/liveontologies/elk-reasoner/releases) for different use-cases:

### Stand-alone application

A stand-alone command-line Java application can perform selected reasoning tasks with a given ontology. E.g., the following command classifies the [Pizza ontology](https://protege.stanford.edu/ontologies/pizza/pizza.owl) and saves the result in another file:
    
```
java -jar elk.jar -i pizza.owl -c -o pizza-taxonomy.owl
```

### OWL API

A library for the [OWL API](https://owlcs.github.io/owlapi/) implementing the [OWLReasoner interface](https://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/reasoner/OWLReasoner.html).
See an example how to [perform queries using an OWL reasoner](https://github.com/owlcs/owlapi/wiki/DL-Queries-with-a-real-reasoner). Both versions 4.x and 5.x of OWL API are currently supported.
To use the ELK OWL API library, include the following maven dependency to your project:

```
<dependency>
  <groupId>io.github.liveontologies</groupId>
  <artifactId>elk-owlapi</artifactId>
  <version>${releasedVersion.version}</version>
</dependency>
```

### Protégé

We provide a reasoner plug-in for the [Protégé Desktop](https://protege.stanford.edu) ontology editor, which can be installed from within the editor.
See the [general documentation](https://protegeproject.github.io/protege/) about how to work with ontologies and use reasoners in Protégé.

## License

ELK is Copyright (c) ${project.inceptionYear} - ${currentYear} ${project.organization.name}.

All sources of this project are available under the terms of the 
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
(see the file `LICENSE.txt`).