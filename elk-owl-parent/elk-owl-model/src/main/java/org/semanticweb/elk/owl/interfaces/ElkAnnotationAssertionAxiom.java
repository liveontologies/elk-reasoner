/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkAnnotationAssertionAxiomVisitor;

/**
 * Annotation assertion axiom as defined in <a
 * href="http://www.w3.org/TR/owl2-syntax/#Annotation_Assertion"> Section
 * 10.2.1</a> of the specification
 * 
 * Note that the superclass of AnnotationAssertion is AnnotationAxiom, not
 * AssertionAxiom.
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * 
 */
public interface ElkAnnotationAssertionAxiom extends ElkAnnotationAxiom {

	public ElkAnnotationSubject getSubject();

	public ElkAnnotationProperty getProperty();

	public ElkAnnotationValue getValue();

	/**
	 * Accept an {@link ElkAnnotationAssertionAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkAnnotationAssertionAxiomVisitor<O> visitor);
}
