package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * A value associated with occurrences of {@link ElkObject}s in the ontology,
 * specifically of the type {@link ElkClassExpression}, {@link ElkIndividual},
 * {@link ElkObjectProperty}, and {@link ElkDataProperty}. For example, the sub
 * class of an {@link ElkSubClassOfAxiom} has negative occurrence, the super
 * class of an {@link ElkSubClassOfAxiom} has a positive occurrence, the member
 * of an {@link ElkEquivalentClassesAxiom} has a dual occurrence, and the entity
 * of an {@link ElkDeclarationAxiom} has a neutral occurrence. Most
 * {@link ElkClassExpression} and {@link ElkObjectPropertyExpression}
 * constructors preserve the polarity, e.g., the polarity of an occurrence of a
 * {@link ElkObjectIntersectionOf} is the same as for their conjuncts. One of
 * the exception is {@link ElkObjectComplementOf}, for which the polarity of the
 * sub expression {@link ElkObjectComplementOf#getClassExpression()} is
 * {@code complementary} of the polarity of the whole expression. For each of
 * the four types of the polarity the complementary polarity can be returned
 * using {@link ElkPolarity#getComplementary()}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public enum ElkPolarity {

	POSITIVE(), NEGATIVE(POSITIVE), DUAL(), NEUTRAL();

	private ElkPolarity complementary_;

	private ElkPolarity(ElkPolarity complementary) {
		this.complementary_ = complementary;
		complementary.complementary_ = this;
	}

	private ElkPolarity() {
		this.complementary_ = this;
	}

	public ElkPolarity getComplementary() {
		return this.complementary_;
	}

}
