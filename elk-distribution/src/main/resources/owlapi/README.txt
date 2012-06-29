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

This package contains a Java library for accessing ELK through 
OWL API plus all third party libraries used by ELK, except for the 
OWL API itself. The OWL API library can be obtained here:

    http://owlapi.sourceforge.net/

REQUIREMENTS:

ELK OWL API bindings are tested to work with OWL API ${owlapi.version}. It may work 
with other versions of OWL API.

Java ${java.required.version} or higher.

INSTALLATION:

To use the library make sure that Java finds ${elk-owlapi.base}.jar, and
the third party libraries from the lib directory in the class path.

USAGE:

Usage instructions, including an example program demonstrating how to use ELK
through OWL API for ontology classification, can be found here:

    ${project.parent.url}wiki/ELK_from_OWL_API  
    
Several example programs on how to classify an ontology and how to query the
reasoner with unnamed class expressions are included here in the folder:

	src/main/java/org/semanticweb/elk/owlapi/examples