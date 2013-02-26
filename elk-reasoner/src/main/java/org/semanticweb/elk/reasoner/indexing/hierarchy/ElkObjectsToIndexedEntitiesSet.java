/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkObjectsToIndexedEntitiesSet<E extends ElkEntity, I extends IndexedClassEntity> extends AbstractSet<I> {

	private final Collection<E> elkEntities_;
	
	private final IndexObjectConverter converter_;
	
	public ElkObjectsToIndexedEntitiesSet(Collection<E> elkEntities, IndexObjectConverter converter) {
		elkEntities_ = elkEntities;
		converter_ = converter;
	}
	
	@Override
	public Iterator<I> iterator() {
		return new Iterator<I>() {

			final Iterator<E> iter_ = elkEntities_.iterator();
			I curr_ = null;
			
			final ElkEntityVisitor<I> visitor_ = new ElkEntityVisitor<I>() {

				@Override
				public I visit(
						ElkAnnotationProperty elkAnnotationProperty) {
					return null;
				}

				@SuppressWarnings("unchecked")
				@Override
				public I visit(ElkClass elkClass) {
					return (I) converter_.visit(elkClass);
				}

				@Override
				public I visit(ElkDataProperty elkDataProperty) {
					return null;
				}

				@Override
				public I visit(ElkDatatype elkDatatype) {
					return null;
				}

				@SuppressWarnings("unchecked")
				@Override
				public I visit(ElkNamedIndividual elkNamedIndividual) {
					return (I) elkNamedIndividual.accept(converter_);
				}

				@Override
				public I visit(
						ElkObjectProperty elkObjectProperty) {
					return null;
				}


				
			};

			@Override
			public boolean hasNext() {
				if (curr_ != null) {
					return true;
				} else {
					while (curr_ == null && iter_.hasNext()) {
						ElkEntity elkEntity = iter_.next();
						I indexedEntity = elkEntity.accept(visitor_);

						if (indexedEntity.occurs()) {
							curr_ = indexedEntity;
						}
					}
				}

				return curr_ != null;
			}

			@Override
			public I next() {
				I tmp = curr_;

				curr_ = null;

				return tmp;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public int size() {
		// an upper bound
		return elkEntities_.size();
	}
}
