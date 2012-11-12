/*
 * #%L
 * ELK Utilities Collections
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
package org.semanticweb.elk.owl.managers;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * A {@link ElkObjectRecycler} that reuses {@link ElkEntity}s. If
 * {@link ElkObjectRecycler#recycle(ElkObject)} is called on two structurally
 * equivalent {@link ElkEntity}s (i.e., with the same {@link ElkIri}s), the same
 * object will be obtained in the output, unless one of the output
 * {@link ElkEntity}s has been garbage collected (i.e., is not used anymore). It
 * does not recycle other types of {@link ElkObject}s, and simply returns them
 * as the output.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkEntityRecycler implements ElkObjectRecycler {

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

	@Override
	public ElkObject recycle(ElkObject object) {
		if (object instanceof ElkEntity)
			return getCanonicalElkEntity((ElkEntity) object);
		else
			return object;
	}

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

		while ((w = (WeakWrapper<? extends ElkEntity>) referenceQueue.poll()) != null) {
			cache.remove(w);
		}
	}
}
