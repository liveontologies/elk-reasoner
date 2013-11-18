/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.index;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * General interface for storage and retrieval of datatype expressions.
 *
 * @author Pospishnyi Oleksandr
 */
public interface DatatypeIndex {

	/**
	 * Add new {@link IndexedDatatypeExpression} to this index
	 *
	 * @param ide {@link IndexedDatatypeExpression} to add for this index
	 */
	public void addDatatypeExpression(IndexedDatatypeExpression ide);

	/**
	 * Remove already stored {@link IndexedDatatypeExpression} from this
	 * index
	 *
	 * @param ide {@link IndexedDatatypeExpression} to remove for this index
	 * @return true if this index contained the specified
	 * {@link IndexedDatatypeExpression} .
	 */
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide);

	/**
	 * Search this datatype index for all {@link IndexedDatatypeExpression}s
	 * that subsume this {@link IndexedDatatypeExpression}.
	 *
	 * @param ide {@link IndexedDatatypeExpression} for which a search of
	 * relevant {@link IndexedDatatypeExpression}s will be conducted in the
	 * index
	 * @return a collection of relevant {@link IndexedDatatypeExpression}s
	 */
	public Iterable<IndexedDatatypeExpression> getSubsumersFor(IndexedDatatypeExpression ide);

	/**
	 * Add all {@link IndexedDatatypeExpression}s from this
	 * {@link DatatypeIndex} to specified {@link DatatypeIndex}
	 *
	 * @param index {@link DatatypeIndex} to merge into
	 */
	public void appendTo(DatatypeIndex index);
}
