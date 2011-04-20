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

import java.util.Set;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.Pair;

public class Concept {
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	//fields
	protected final Set<ElkClassExpression> classExpressions;
	protected final Set<Concept> toldSuperConcepts;
	protected final Set<Conjunction> conjunctions;
	protected final Set<Existential> existentials;
	protected final Set<Universal> universals;

	//methods
	public Concept() {
		classExpressions = new ArraySet<ElkClassExpression> ();
		toldSuperConcepts = new ArraySet<Concept> ();
		conjunctions = new ArraySet<Conjunction> ();
		existentials = new ArraySet<Existential> ();
		universals = new ArraySet<Universal> ();
	}
	
	public Concept(ElkClassExpression ce) {
		this();
		classExpressions.add(ce);
	}

	public Set<ElkClassExpression> getClassExpressions() {
		return classExpressions;
	}

	public Set<Concept> getToldSuperConcepts() {
		return toldSuperConcepts;
	}

	public Set<Conjunction> getConjunctions() {
		return conjunctions;
	}

	public Set<Existential> getExistentials() {
		return existentials;
	}

	public Set<Universal> getUniversals() {
		return universals;
	}

	public boolean containsAtomicClass() {
		for (ElkClassExpression c : classExpressions)
			if (c instanceof ElkClass)
				return true;
		return false;
	}
	
	public Set<ElkClass> getAtomicClasses() {
		Set<ElkClass> result = new ArraySet<ElkClass> ();
		for (ElkClassExpression c : classExpressions)
			if (c instanceof ElkClass)
				result.add((ElkClass) c);
		return result;
	}
}

class Conjunction extends Pair<Set<Concept>, Concept>{
	public Conjunction(Set<Concept> premises, Concept conclusion) {
		super(premises, conclusion);
	}
	
	public Set<Concept> getPremises() {
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

class Existential extends Quantifier {
	public Existential(Role role, Concept concept) {
		super(role, concept);
	}
}

class Universal extends Quantifier {
	public Universal(Role role, Concept concept) {
		super(role, concept);
	}
}