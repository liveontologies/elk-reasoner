/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.classification;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Class taxonomy that is suitable for concurrent processing.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class ConcurrentClassTaxonomy extends ClassTaxonomy {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ConcurrentClassTaxonomy.class);

	// map from class IRIs to class nodes
	protected final ConcurrentMap<String, ClassNode> nodeLookup;

	ConcurrentClassTaxonomy() {
		this.nodeLookup = new ConcurrentHashMap<String, ClassNode>();
	}

	public Set<ClassNode> getNodes() {
		return Collections.unmodifiableSet(new HashSet<ClassNode>(nodeLookup
				.values()));
	}

	/**
	 * Obtain a ClassNode object for a given ElkClass, null if none assigned
	 * 
	 * @param elkClass
	 * @return ClassNode object for elkClass, possibly still incomplete
	 */
	public ClassNode getNode(ElkClass elkClass) {
		ClassNode result = nodeLookup.get(elkClass.getIri());
		if (result == null)
			LOGGER_.error("No taxonomy node for class " + elkClass.getIri());
		return result;
	}

	public int structuralHashCode() {
		return HashGenerator.combineMultisetHash(true, getNodes());
	}
}