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

import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkAnnotationVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 *
 */
public class ElkAnnotationImpl implements ElkAnnotation {

	private final ElkAnnotationProperty annProperty_;
	private final ElkAnnotationValue annValue_;

	ElkAnnotationImpl(ElkAnnotationProperty annProperty,
			ElkAnnotationValue annValue) {
		this.annProperty_ = annProperty;
		this.annValue_ = annValue;
	}

	@Override
	public <O> O accept(ElkAnnotationVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkAnnotationVisitor<O>) visitor);
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return annProperty_;
	}

	@Override
	public ElkAnnotationValue getValue() {
		return annValue_;
	}

}
