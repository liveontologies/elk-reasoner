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
package org.semanticweb.elk.owl.iris;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.owl.predefined.PredefinedElkPrefix;

/**
 * @author Frantisek Simancik
 *
 */
public class ElkPrefixDeclarationsImpl implements ElkPrefixDeclarations {
	
	protected final Map<String, ElkPrefix> prefixLookup =
		new HashMap<String, ElkPrefix> ();

	@Override
	public boolean addPrefix(ElkPrefix prefix) {
		if (prefixLookup.containsKey(prefix.getName()))
			return false;
		
		prefixLookup.put(prefix.getName(), prefix);
		return true;
	}

	@Override
	public ElkPrefix getPrefix(String prefixName) {
		return prefixLookup.get(prefixName);
	}
	
	@Override
	public void addOwlDefaultPrefixes() {
         addPrefix(PredefinedElkPrefix.OWL);
         addPrefix(PredefinedElkPrefix.RDF);
         addPrefix(PredefinedElkPrefix.RDFS);
         addPrefix(PredefinedElkPrefix.XSD);
	}

}
