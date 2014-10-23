/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.visitors.ElkAnnotationPropertyVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

/**
 * Implements the {@link ElkAnnotationProperty} interface by wrapping instances
 * of {@link OWLAnnotationProperty}
 * 
 * @author Frantisek Simancik
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkAnnotationPropertyWrap<T extends OWLAnnotationProperty> extends
		ElkEntityWrap<T> implements ElkAnnotationProperty {

	public ElkAnnotationPropertyWrap(T owlAnnotationProperty) {
		super(owlAnnotationProperty);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAnnotationPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

}