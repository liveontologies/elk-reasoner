/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkAnnotationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAnnotationPropertyRangeAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyRangeAxiomVisitor;

/**
 * Implementation of {@link ElkAnnotationPropertyRangeAxiom}.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 */
public class ElkAnnotationPropertyRangeAxiomImpl extends
		ElkPropertyRangeAxiomImpl<ElkAnnotationProperty, ElkIri> implements
		ElkAnnotationPropertyRangeAxiom {

	ElkAnnotationPropertyRangeAxiomImpl(ElkAnnotationProperty property,
			ElkIri range) {
		super(property, range);
	}

	@Override
	public <O> O accept(ElkAnnotationPropertyRangeAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkAnnotationAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyRangeAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkPropertyRangeAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationPropertyRangeAxiomVisitor<O>) visitor);
	}

}
