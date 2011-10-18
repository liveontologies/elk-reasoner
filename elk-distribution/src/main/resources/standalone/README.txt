ELK is an ontology reasoner that aims to support the OWL 2 EL profile.
See ${project.parent.url} for further information.

ELK Reasoner is Copyright (c) ${project.inceptionYear} 
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

Java 1.5 or higher.

USAGE:

Invoking the client without any parameters displays the supported options:
  
    java -jar ${elk-standalone.base}.jar

To load the ontology from the file ontology.fss, compute its classification
and store the result in classification.fss, run ELK as follows: 

    java -jar ${elk-standalone.base}.jar -i ontology.fss -c -o classificaiton.fss
	
You may want to specify further Java parameters for increasing available
memory for classifying larger ontologies, e.g. by setting

    java -XX:+AggressiveHeap -jar ${elk-standalone.base}.jar

or by providing a increased maximum heap size such as -Xmx3000m.	

Currently ELK can only read ontologies in OWL 2 Functional-Style Syntax.
OWL ontologies in other formats can be converted into Functional-Style Syntax
using Protégé version 4.1 or higher. To convert a file, open it in Protege
and save using the menu: File > Save as... > OWL Functional Syntax