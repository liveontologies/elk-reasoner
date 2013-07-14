package org.semanticweb.elk.reasoner;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

/**
 * A {@link ElkClassExpressionVisitor} that tests satisfiability of the visited
 * {@link ElkClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ElkClassExpressionSatisfiabilityCheckingVisitor implements
		ElkClassExpressionVisitor<SatisfiabilityCheckingOutcome> {

	/**
	 * The {@link Reasoner} used for reasoning operations
	 */
	private final Reasoner reasoner_;

	public ElkClassExpressionSatisfiabilityCheckingVisitor(Reasoner reasoner) {
		this.reasoner_ = reasoner;
	}

	static SatisfiabilityCheckingException defaultReturn(
			ElkClassExpression classExpression) {
		throw new ElkUnsupportedReasoningTaskException(
				"ELK cannot check satisfiability for "
						+ OwlFunctionalStylePrinter.toString(classExpression));
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(ElkClass elkClass) {
		try {
			Node<ElkClass> classNode = reasoner_.getClassNode(elkClass);
			return new SatisfiabilityCheckingResult(!classNode.getMembers()
					.contains(PredefinedElkClass.OWL_NOTHING));
		} catch (ElkException e) {
			return new SatisfiabilityCheckingException(e);
		}
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		return defaultReturn(elkDataAllValuesFrom);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		return defaultReturn(elkDataExactCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		return defaultReturn(elkDataExactCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(ElkDataHasValue elkDataHasValue) {
		return defaultReturn(elkDataHasValue);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		return defaultReturn(elkDataMaxCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		return defaultReturn(elkDataMaxCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		return defaultReturn(elkDataMinCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		return defaultReturn(elkDataMinCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		return defaultReturn(elkDataSomeValuesFrom);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		return defaultReturn(elkObjectAllValuesFrom);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		return defaultReturn(elkObjectComplementOf);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		return defaultReturn(elkObjectExactCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		return defaultReturn(elkObjectExactCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(ElkObjectHasSelf elkObjectHasSelf) {
		return defaultReturn(elkObjectHasSelf);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectHasValue elkObjectHasValue) {
		return defaultReturn(elkObjectHasValue);
	}

	/*
	 * This deals with a particular case when elkObjectIntersectionOf is a
	 * conjunction of at most one positive ElkClass and some number of negative
	 * ElkClasses; the query is then converted into a series of subsumption
	 * tests
	 */
	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// the positive ElkClass of the conjunction
		ElkClass positiveConjunct = null;
		// the list of negative ElkClasses of the conjunction
		List<ElkClass> negativeConjuncts = new ArrayList<ElkClass>();
		for (ElkClassExpression conjunct : elkObjectIntersectionOf
				.getClassExpressions()) {
			if (conjunct instanceof ElkClass) {
				if (positiveConjunct == null) {
					positiveConjunct = (ElkClass) conjunct;
					continue;
				}
			} else if (conjunct instanceof ElkObjectComplementOf) {
				ElkClassExpression complement = ((ElkObjectComplementOf) conjunct)
						.getClassExpression();
				if (complement instanceof ElkClass) {
					negativeConjuncts.add((ElkClass) complement);
					continue;
				}
			}
			// if the conjunct was neither of the above, we do not support
			// satisfiability checking
			return defaultReturn(elkObjectIntersectionOf);
		}
		if (positiveConjunct == null)
			positiveConjunct = PredefinedElkClass.OWL_THING;
		try {
			/*
			 * the conjunction is satisfiable if the ontology is consistent and
			 * the positive conjunct does NOT entail any of the negative
			 * conjuncts
			 */
			if (reasoner_.isInconsistent())
				return new SatisfiabilityCheckingResult(false);
			Set<? extends Node<ElkClass>> superClasses = reasoner_
					.getSuperClasses(positiveConjunct, false);
			for (ElkClass complement : negativeConjuncts) {
				Node<ElkClass> testNode = reasoner_.getClassNode(complement);
				if (superClasses.contains(testNode))
					return new SatisfiabilityCheckingResult(false);
			}
			return new SatisfiabilityCheckingResult(true);
		} catch (ElkException e) {
			return new SatisfiabilityCheckingException(e);
		}
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		return defaultReturn(elkObjectMaxCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		return defaultReturn(elkObjectMaxCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		return defaultReturn(elkObjectMinCardinality);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		return defaultReturn(elkObjectMinCardinalityQualified);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(ElkObjectOneOf elkObjectOneOf) {
		return defaultReturn(elkObjectOneOf);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return defaultReturn(elkObjectSomeValuesFrom);
	}

	@Override
	public SatisfiabilityCheckingOutcome visit(ElkObjectUnionOf elkObjectUnionOf) {
		return defaultReturn(elkObjectUnionOf);
	}

}
