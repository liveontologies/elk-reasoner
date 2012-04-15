/*
 * #%L
 * ELK OWL Model Implementation
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


/**
 * Represents an abbreaviated IRI. This class holds enough information to
 * be able to get the IRI both in the full and the abbreviated form.
 * 
 * @author Frantisek Simancik
 *
 */
public class ElkAbbreviatedIri extends ElkIri {
	
	protected final ElkPrefix prefix;
	protected final String localName;
	
	public ElkAbbreviatedIri(ElkPrefix prefix, String localName) {
		super (prefix.getIri().asString() + localName);
		this.prefix = prefix;
		this.localName = localName;
	}

	public ElkPrefix getPrefix() {
		return prefix;
	}

	public String getLocalName() {
		return localName;
	}
	
	@Override
	public String asString() {
		return prefix.getIri().asString() + localName;
	}

	@Override
	public int compareTo(ElkIri arg) {
		if (arg instanceof ElkAbbreviatedIri && this.prefix == ((ElkAbbreviatedIri) arg).prefix)
			return this.localName.compareTo(((ElkAbbreviatedIri) arg).localName);
		
		return super.compareTo(arg);
	}
	
}
