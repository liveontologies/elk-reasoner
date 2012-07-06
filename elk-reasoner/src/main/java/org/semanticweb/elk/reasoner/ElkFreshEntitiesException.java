/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkEntity;

/**
 * Exception that is thrown when a query that is asked to the reasoner refers to
 * vocabulary symbols that do not occur in the ontology yet.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkFreshEntitiesException extends ElkException {

	private static final long serialVersionUID = -4462031988813386808L;

	protected final Set<ElkEntity> entities;

	public ElkFreshEntitiesException(ElkEntity entity) {
		super();
		entities = new HashSet<ElkEntity>();
		entities.add(entity);
	}

	public ElkFreshEntitiesException(Set<ElkEntity> entities) {
		super();
		this.entities = entities;
	}

	public Set<ElkEntity> getEntities() {
		return entities;
	}

}
