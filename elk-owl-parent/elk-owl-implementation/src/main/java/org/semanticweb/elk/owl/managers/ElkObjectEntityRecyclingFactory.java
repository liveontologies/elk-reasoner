package org.semanticweb.elk.owl.managers;

/*
 * #%L
 * ELK OWL Model Implementation
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectDelegatingFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * An {@link ElkObject.Factory} that reuses already created {@link ElkEntity}s.
 * That is, any two structurally equivalent {@link ElkEntity}s (i.e., with the
 * same {@link ElkIri}s) entities will be the same object. For other types of
 * {@link ElkObject}s this is not the case: every time a new object is created.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
@SuppressWarnings("javadoc")
public class ElkObjectEntityRecyclingFactory
		extends ElkObjectDelegatingFactory {

	public ElkObjectEntityRecyclingFactory(ElkObject.Factory factory) {
		super(factory);
	}

	public ElkObjectEntityRecyclingFactory() {
		this(new ElkObjectBaseFactory());
	}

	// TODO: simplify the implementation to use just one weak wrapper

	@SuppressWarnings("unchecked")
	@Override
	protected <C extends ElkObject> C filter(C candidate) {
		if (candidate instanceof ElkEntity)
			return (C) getCanonicalElkEntity((ElkEntity) candidate);
		else
			return candidate;
	}

	private HashMap<WeakWrapper<? extends ElkEntity>, WeakWrapper<? extends ElkEntity>> cache = new HashMap<WeakWrapper<? extends ElkEntity>, WeakWrapper<? extends ElkEntity>>();

	private ReferenceQueue<ElkEntity> referenceQueue = new ReferenceQueue<ElkEntity>();

	private ElkEntityVisitor<WeakWrapper<? extends ElkEntity>> wrapper = new ElkEntityVisitor<WeakWrapper<? extends ElkEntity>>() {

		@Override
		public WeakWrapper<? extends ElkEntity> visit(ElkClass elkClass) {
			return new WeakElkClassWrapper(elkClass, referenceQueue);
		}

		@Override
		public WeakWrapper<? extends ElkEntity> visit(ElkDatatype elkDatatype) {
			return new WeakElkDatatypeWrapper(elkDatatype, referenceQueue);
		}

		@Override
		public WeakWrapper<? extends ElkEntity> visit(
				ElkObjectProperty elkObjectProperty) {
			return new WeakElkObjectPropertyWrapper(elkObjectProperty,
					referenceQueue);
		}

		@Override
		public WeakWrapper<? extends ElkEntity> visit(
				ElkDataProperty elkDataProperty) {
			return new WeakElkDataPropertyWrapper(elkDataProperty,
					referenceQueue);
		}

		@Override
		public WeakWrapper<? extends ElkEntity> visit(
				ElkNamedIndividual elkNamedIndividual) {
			return new WeakElkNamedIndividualWrapper(elkNamedIndividual,
					referenceQueue);
		}

		@Override
		public WeakWrapper<? extends ElkEntity> visit(
				ElkAnnotationProperty elkAnnotationProperty) {
			return new WeakElkAnnotationPropertyWrapper(elkAnnotationProperty,
					referenceQueue);
		}

	};

	private ElkEntity getCanonicalElkEntity(ElkEntity entity) {
		processQueue();

		if (entity == null)
			return null;

		WeakWrapper<? extends ElkEntity> key = entity.accept(wrapper);
		WeakWrapper<? extends ElkEntity> value = cache.get(key);

		if (value != null) {
			ElkEntity result = value.get();
			if (result != null)
				return result;
		}

		cache.put(key, key);
		return entity;
	}

	private final void processQueue() {
		WeakWrapper<? extends ElkEntity> w = null;

		while ((w = (WeakWrapper<? extends ElkEntity>) referenceQueue
				.poll()) != null) {
			cache.remove(w);
		}
	}

}
