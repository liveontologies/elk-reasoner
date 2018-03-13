ELK is an ontology reasoner that aims to support the OWL 2 EL profile.
See ${project.parent.url} for further information.

ELK Reasoner is Copyright (c) ${project.inceptionYear} - ${currentYear}
${project.organization.name}

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

ABOUT:

This package a contains a standalone executable ELK Reasoner with a simple
command-line interface. 

REQUIREMENTS:

Java ${java.required.version} or higher.

QUICK START:

To load the ontology from the file ontology.owl, compute its classification
and store the result in taxonomy.owl, run ELK as follows: 

    java -jar ${elk-cli.filename}.jar -i ontology.owl -c -o taxonomy.owl

Without the output parameter ELK will still compute the result but it will
neither store nor display the result anywhere; this can be used for performance
experiments.

USAGE:

To display all available options run:
  
    java -jar ${elk-cli.filename}.jar -h

You may want to specify further Java parameters for increasing available memory
for classifying larger ontologies, e.g. by setting

    java -Xms256M -Xmx3G -jar ${elk-cli.filename}.jar
    
or    

    java -XX:+AggressiveHeap -jar ${elk-cli.filename}.jar	

If ELK cannot parse your ontology, this is probably because it is in the
RDF/XML syntax. ELK can only parse ontologies in OWL 2 Functional-Style Syntax.
OWL ontologies in other formats can be converted into Functional-Style Syntax
using Protege version 4.1 or higher. To convert a file, open it in Protege and
save using the menu: File > Save as... > OWL Functional Syntax