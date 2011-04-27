/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import java.util.List;
import java.util.ArrayList;

import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.util.Pair;


/**
 * Represents all occurrences of an ElkClassExpression in an ontology  
 * 
 * @author Frantisek Simancik
 *
 */

class Concept implements Derivable { 

	protected final ElkClassExpression classExpression;
	protected final List<Concept> toldSuperConcepts = new ArrayList<Concept> ();
	protected final List<Conjunction> conjunctions = new ArrayList<Conjunction> ();
	protected final List<Quantifier> existentials = new ArrayList<Quantifier> ();
	protected final List<Quantifier> universals = new ArrayList<Quantifier> ();
	
	int positiveOccurrenceNo = 0;
	int negativeOccurrenceNo = 0;

	/**
	 * Creates a Concept representing classExpression
	 * 
	 * @param classExpression
	 */
	public Concept(ElkClassExpression classExpression) {
		this.classExpression = classExpression;
	}
	
	public ElkClassExpression getClassExpression() {
		return classExpression;
	}

	public List<Concept> getToldSuperConcepts() {
		return toldSuperConcepts;
	}

	public List<Conjunction> getConjunctions() {
		return conjunctions;
	}

	public List<Quantifier> getExistentials() {
		return existentials;
	}

	public List<Quantifier> getUniversals() {
		return universals;
	}

	private static int nextHashCode_ = 0;
	private final int hash_ = nextHashCode_++;
	
	@Override
	public int hashCode() {
		return hash_;
	}

	public <O> O accept(DerivableVisitor<O> visitor) {
		return visitor.visit(this);
	}
}


class Conjunction extends Pair<List<Concept>, Concept>{
	public Conjunction(List<Concept> premises, Concept conclusion) {
		super(premises, conclusion);
	}
	
	public List<Concept> getPremises() {
		return first;
	}
	
	public Concept getConclusion() {
		return second;
	}
}


class Quantifier extends Pair<Role, Concept> {
	public Quantifier(Role role, Concept concept) {
		super(role, concept);
	}
	
	public Role getRole() {
		return first;
	}
	
	public Concept getConcept() {
		return second;
	}
}