/*
 * #%L
 * ELK OWL Model Implementation
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
/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkAnnotationAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAnnotationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkAnnotationAssertionAxiom}
 * 
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 *
 */
public class ElkAnnotationAssertionAxiomImpl implements
		ElkAnnotationAssertionAxiom {

	private final ElkAnnotationSubject annSubject_;
	private final ElkAnnotationProperty annProperty_;
	private final ElkAnnotationValue annValue_;

	ElkAnnotationAssertionAxiomImpl(ElkAnnotationProperty annProperty,
			ElkAnnotationSubject annSubject, ElkAnnotationValue annValue) {
		this.annSubject_ = annSubject;
		this.annProperty_ = annProperty;
		this.annValue_ = annValue;
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return annProperty_;
	}

	@Override
	public ElkAnnotationValue getValue() {
		return annValue_;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ElkAnnotationSubject getSubject() {
		return annSubject_;
	}

	@Override
	public <O> O accept(ElkAnnotationAxiomVisitor<O> visitor) {
		return accept((ElkAnnotationAssertionAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkAnnotationAssertionAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAnnotationAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}
}