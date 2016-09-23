/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.query.BaseSatisfiabilityTestOutput;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.query.SatisfiabilityTestOutput;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

/**
 * @author Peter Skocovsky
 * @see #load(InputStream)
 */
public class ExpectedTestOutputLoader {

	/**
	 * Loads an expected output of a class query test.
	 * <p>
	 * The expected output should be stored in an ontology that is loaded from
	 * the supplied argument. There should be only one complex class that will
	 * be recognized as the query class. Classes equivalent to the query class
	 * should be in single equivalence axiom with the query class. Direct
	 * super(sub)-classes of the query class should be partitioned into sets of
	 * equivalent classes, each of which should be expressed by a single
	 * equivalence axiom if there is more than one class in the set. Whether
	 * such a set contains super- or sub-classes of the query class should be
	 * expressed by a single subclass axiom of the query class with one of the
	 * related classes. Analogously for individuals: grouped by same individuals
	 * axioms and related by class assertion axioms.
	 * 
	 * @param expectedOutput
	 *            contains the ontology that encode the expected output
	 * @return an object providing the leaded exected test output
	 */
	public static ExpectedTestOutputLoader load(
			final InputStream expectedOutput) {

		final OWLOntologyManager manager = OWLManager
				.createOWLOntologyManager();
		try {

			final OWLOntology expectedOnt = manager
					.loadOntologyFromOntologyDocument(expectedOutput);

			final Set<OWLClassExpression> complex = new HashSet<OWLClassExpression>();
			final Map<OWLClassExpression, OWLClassNode> equivalent = new HashMap<OWLClassExpression, OWLClassNode>();
			final Multimap<OWLClassExpression, OWLClass> superClasses = new HashSetMultimap<OWLClassExpression, OWLClass>();
			final Multimap<OWLClassExpression, OWLClass> subClasses = new HashSetMultimap<OWLClassExpression, OWLClass>();
			final Map<OWLIndividual, OWLNamedIndividualNode> same = new HashMap<OWLIndividual, OWLNamedIndividualNode>();
			final Multimap<OWLClassExpression, OWLNamedIndividual> instances = new HashSetMultimap<OWLClassExpression, OWLNamedIndividual>();

			for (final OWLAxiom axiom : expectedOnt.getAxioms()) {
				axiom.accept(new OWLAxiomVisitorAdapter() {

					@Override
					public void visit(final OWLEquivalentClassesAxiom axiom) {
						final Set<OWLClass> owlClasses = new HashSet<OWLClass>();
						for (final OWLClassExpression ce : axiom
								.getClassExpressions()) {
							if (ce instanceof OWLClass) {
								owlClasses.add((OWLClass) ce);
							} else {
								complex.add(ce);
							}
						}
						final OWLClassNode node = new OWLClassNode(owlClasses);
						for (final OWLClassExpression ce : axiom
								.getClassExpressions()) {
							equivalent.put(ce, node);
						}
					}

					@Override
					public void visit(final OWLSubClassOfAxiom axiom) {
						if (axiom.getSubClass() instanceof OWLClass) {
							subClasses.add(axiom.getSuperClass(),
									(OWLClass) axiom.getSubClass());
						} else {
							complex.add(axiom.getSubClass());
						}
						if (axiom.getSuperClass() instanceof OWLClass) {
							superClasses.add(axiom.getSubClass(),
									(OWLClass) axiom.getSuperClass());
						} else {
							complex.add(axiom.getSuperClass());
						}
					}

					@Override
					public void visit(final OWLSameIndividualAxiom axiom) {
						final Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
						for (final OWLIndividual i : axiom.getIndividuals()) {
							if (i instanceof OWLNamedIndividual) {
								individuals.add((OWLNamedIndividual) i);
							}
						}
						final OWLNamedIndividualNode node = new OWLNamedIndividualNode(
								individuals);
						for (final OWLIndividual i : axiom.getIndividuals()) {
							same.put(i, node);
						}
					}

					@Override
					public void visit(final OWLClassAssertionAxiom axiom) {
						if (axiom
								.getIndividual() instanceof OWLNamedIndividual) {
							instances.add(axiom.getClassExpression(),
									(OWLNamedIndividual) axiom.getIndividual());
						}
						if (!(axiom.getClassExpression() instanceof OWLClass)) {
							complex.add(axiom.getClassExpression());
						}
					}

				});
			}

			if (complex.size() != 1) {
				throw new IllegalArgumentException(
						"There must be exactly 1 complex class in the expected result!");
			}
			final OWLClassExpression complexClass = complex.iterator().next();

			return new ExpectedTestOutputLoader(complexClass, equivalent,
					superClasses.get(complexClass),
					subClasses.get(complexClass), same,
					instances.get(complexClass));

		} catch (final OWLOntologyCreationException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private final OWLClassExpression queryClass_;
	private final Map<OWLClassExpression, OWLClassNode> equivalent_;
	private final Collection<OWLClass> superClasses_;
	private final Collection<OWLClass> subClasses_;
	private final Map<OWLIndividual, OWLNamedIndividualNode> same_;
	private final Collection<OWLNamedIndividual> instances_;

	private ExpectedTestOutputLoader(final OWLClassExpression queryClass,
			final Map<OWLClassExpression, OWLClassNode> equivalent,
			final Collection<OWLClass> superClasses,
			final Collection<OWLClass> subClasses,
			final Map<OWLIndividual, OWLNamedIndividualNode> same,
			final Collection<OWLNamedIndividual> instances) {
		this.queryClass_ = queryClass;
		this.equivalent_ = equivalent;
		this.superClasses_ = superClasses;
		this.subClasses_ = subClasses;
		this.same_ = same;
		this.instances_ = instances;
	}

	public OWLClassExpression getQueryClass() {
		return queryClass_;
	}

	public SatisfiabilityTestOutput getSatisfiabilityTestOutput() {
		final OWLClassNode node = equivalent_.get(queryClass_);
		// If the query class is equivalent to bottom, node is NOT null!
		return new BaseSatisfiabilityTestOutput(
				node == null || !node.isBottomNode());
	}

	public EquivalentEntitiesTestOutput<OWLClass> getEquivalentEntitiesTestOutput() {
		final OWLClassNode node = equivalent_.get(queryClass_);
		return new OwlApiEquivalentEntitiesTestOutput(
				node == null ? new OWLClassNode() : node);
	}

	public RelatedEntitiesTestOutput<OWLClass> getSuperEntitiesTestOutput() {

		final Collection<OWLClassNode> superNodes = Operations.map(
				superClasses_,
				new Operations.Transformation<OWLClass, OWLClassNode>() {
					@Override
					public OWLClassNode transform(final OWLClass cls) {
						final OWLClassNode result = equivalent_.get(cls);
						if (result != null) {
							return result;
						}
						// else
						return new OWLClassNode(cls);
					}
				});

		return new OwlApiRelatedEntitiesTestOutput<OWLClass>(
				new OWLClassNodeSet(new HashSet<Node<OWLClass>>(superNodes)));
	}

	public RelatedEntitiesTestOutput<OWLClass> getSubEntitiesTestOutput() {

		final Collection<OWLClassNode> subNodes = Operations.map(subClasses_,
				new Operations.Transformation<OWLClass, OWLClassNode>() {
					@Override
					public OWLClassNode transform(final OWLClass cls) {
						final OWLClassNode result = equivalent_.get(cls);
						if (result != null) {
							return result;
						}
						// else
						return new OWLClassNode(cls);
					}
				});

		return new OwlApiRelatedEntitiesTestOutput<OWLClass>(
				new OWLClassNodeSet(new HashSet<Node<OWLClass>>(subNodes)));
	}

	public RelatedEntitiesTestOutput<OWLNamedIndividual> getInstancesTestOutput() {

		final Collection<OWLNamedIndividualNode> instances = Operations.map(
				instances_,
				new Operations.Transformation<OWLNamedIndividual, OWLNamedIndividualNode>() {
					@Override
					public OWLNamedIndividualNode transform(
							final OWLNamedIndividual ind) {
						final OWLNamedIndividualNode result = same_.get(ind);
						if (result != null) {
							return result;
						}
						// else
						return new OWLNamedIndividualNode(ind);
					}
				});

		return new OwlApiRelatedEntitiesTestOutput<OWLNamedIndividual>(
				new OWLNamedIndividualNodeSet(
						new HashSet<Node<OWLNamedIndividual>>(instances)));
	}

}
