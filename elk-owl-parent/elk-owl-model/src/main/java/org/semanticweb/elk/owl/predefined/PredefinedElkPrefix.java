/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;

public class PredefinedElkPrefix {
	
	public static final ElkPrefix OWL = new ElkPrefix("owl:", 
   		 new ElkFullIri("http://www.w3.org/2002/07/owl#"));
	
	public static final ElkPrefix RDF = new ElkPrefix("rdf:", 
   		 new ElkFullIri("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
	
	public static final ElkPrefix RDFS = new ElkPrefix("rdfs:", 
        		 new ElkFullIri("http://www.w3.org/2000/01/rdf-schema#"));
	
	public static final ElkPrefix XSD = new ElkPrefix("xsd:",
        		 new ElkFullIri("http://www.w3.org/2001/XMLSchema#"));
	
}
