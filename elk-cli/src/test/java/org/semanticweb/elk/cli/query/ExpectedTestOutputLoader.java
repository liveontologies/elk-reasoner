/*
 * #%L
 * ELK Command Line Interface
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
package org.semanticweb.elk.cli.query;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.query.BaseSatisfiabilityTestOutput;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.query.SatisfiabilityTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

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

		final Set<ElkClassExpression> complex = new HashSet<ElkClassExpression>();
		final Map<ElkClassExpression, Map<ElkIri, ElkClass>> equivalent = new HashMap<ElkClassExpression, Map<ElkIri, ElkClass>>();
		final Multimap<ElkClassExpression, ElkClass> superClasses = new HashSetMultimap<ElkClassExpression, ElkClass>();
		final Multimap<ElkClassExpression, ElkClass> subClasses = new HashSetMultimap<ElkClassExpression, ElkClass>();
		final Map<ElkIndividual, Map<ElkIri, ElkNamedIndividual>> same = new HashMap<ElkIndividual, Map<ElkIri, ElkNamedIndividual>>();
		final Multimap<ElkClassExpression, ElkNamedIndividual> instances = new HashSetMultimap<ElkClassExpression, ElkNamedIndividual>();

		final ElkAxiomVisitor<Void> visitor = new DummyElkAxiomVisitor<Void>() {

			@Override
			public Void visit(
					final ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
				final Map<ElkIri, ElkClass> classes = new HashMap<ElkIri, ElkClass>();
				for (final ElkClassExpression ce : elkEquivalentClassesAxiom
						.getClassExpressions()) {
					if (ce instanceof ElkClass) {
						final ElkClass cls = (ElkClass) ce;
						classes.put(cls.getIri(), cls);
					} else {
						complex.add(ce);
					}
				}
				for (final ElkClassExpression ce : elkEquivalentClassesAxiom
						.getClassExpressions()) {
					equivalent.put(ce, classes);
				}
				return null;
			}

			@Override
			public Void visit(final ElkSubClassOfAxiom elkSubClassOfAxiom) {
				if (elkSubClassOfAxiom
						.getSubClassExpression() instanceof ElkClass) {
					subClasses.add(elkSubClassOfAxiom.getSuperClassExpression(),
							(ElkClass) elkSubClassOfAxiom
									.getSubClassExpression());
				} else {
					complex.add(elkSubClassOfAxiom.getSubClassExpression());
				}
				if (elkSubClassOfAxiom
						.getSuperClassExpression() instanceof ElkClass) {
					superClasses.add(elkSubClassOfAxiom.getSubClassExpression(),
							(ElkClass) elkSubClassOfAxiom
									.getSuperClassExpression());
				} else {
					complex.add(elkSubClassOfAxiom.getSuperClassExpression());
				}
				return null;
			}

			@Override
			public Void visit(
					final ElkSameIndividualAxiom elkSameIndividualAxiom) {
				final Map<ElkIri, ElkNamedIndividual> individuals = new HashMap<ElkIri, ElkNamedIndividual>();
				for (final ElkIndividual i : elkSameIndividualAxiom
						.getIndividuals()) {
					if (i instanceof ElkNamedIndividual) {
						final ElkNamedIndividual ni = (ElkNamedIndividual) i;
						individuals.put(ni.getIri(), ni);
					}
				}
				for (final ElkIndividual i : elkSameIndividualAxiom
						.getIndividuals()) {
					same.put(i, individuals);
				}
				return null;
			}

			@Override
			public Void visit(
					final ElkClassAssertionAxiom elkClassAssertionAxiom) {
				if (elkClassAssertionAxiom
						.getIndividual() instanceof ElkNamedIndividual) {
					instances.add(elkClassAssertionAxiom.getClassExpression(),
							(ElkNamedIndividual) elkClassAssertionAxiom
									.getIndividual());
				}
				if (!(elkClassAssertionAxiom
						.getClassExpression() instanceof ElkClass)) {
					complex.add(elkClassAssertionAxiom.getClassExpression());
				}
				return null;
			}

		};

		final Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(expectedOutput);
		try {

			parser.accept(new Owl2ParserAxiomProcessor() {

				@Override
				public void visit(final ElkPrefix elkPrefix)
						throws Owl2ParseException {
					// Empty.
				}

				@Override
				public void visit(final ElkAxiom elkAxiom)
						throws Owl2ParseException {
					elkAxiom.accept(visitor);
				}

				@Override
				public void finish() throws Owl2ParseException {
					// Empty.
				}

			});

			if (complex.size() != 1) {
				throw new IllegalArgumentException(
						"There must be exactly 1 complex class in the expected result!");
			}
			final ElkClassExpression complexClass = complex.iterator().next();

			return new ExpectedTestOutputLoader(complexClass, equivalent,
					superClasses.get(complexClass),
					subClasses.get(complexClass), same,
					instances.get(complexClass));

		} catch (final Owl2ParseException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private final ElkClassExpression queryClass_;
	private final Map<ElkClassExpression, Map<ElkIri, ElkClass>> equivalent_;
	private final Collection<ElkClass> superClasses_;
	private final Collection<ElkClass> subClasses_;
	private final Map<ElkIndividual, Map<ElkIri, ElkNamedIndividual>> same_;
	private final Collection<ElkNamedIndividual> instances_;

	private ExpectedTestOutputLoader(final ElkClassExpression queryClass,
			final Map<ElkClassExpression, Map<ElkIri, ElkClass>> equivalent,
			final Collection<ElkClass> superClasses,
			final Collection<ElkClass> subClasses,
			final Map<ElkIndividual, Map<ElkIri, ElkNamedIndividual>> same,
			final Collection<ElkNamedIndividual> instances) {
		this.queryClass_ = queryClass;
		this.equivalent_ = equivalent;
		this.superClasses_ = superClasses;
		this.subClasses_ = subClasses;
		this.same_ = same;
		this.instances_ = instances;
	}

	public ElkClassExpression getQueryClass() {
		return queryClass_;
	}

	public SatisfiabilityTestOutput getSatisfiabilityTestOutput() {
		final Map<ElkIri, ElkClass> node = equivalent_.get(queryClass_);
		// If the query class is equivalent to bottom, node is NOT null!
		return new BaseSatisfiabilityTestOutput(node == null
				|| !node.containsKey(PredefinedElkIris.OWL_NOTHING));
	}

	public EquivalentEntitiesTestOutput<ElkClass> getEquivalentEntitiesTestOutput() {
		final Map<ElkIri, ElkClass> node = equivalent_.get(queryClass_);
		return new CliEquivalentEntitiesTestOutput(node == null
				? Collections.<ElkClass> emptySet() : node.values());
	}

	public RelatedEntitiesTestOutput<ElkClass> getSuperEntitiesTestOutput() {

		final Collection<Collection<ElkClass>> superNodes = Operations.map(
				superClasses_,
				new Operations.Transformation<ElkClass, Collection<ElkClass>>() {
					@Override
					public Collection<ElkClass> transform(final ElkClass cls) {
						final Map<ElkIri, ElkClass> result = equivalent_
								.get(cls);
						if (result != null) {
							return result.values();
						}
						// else
						return Collections.singleton(cls);
					}
				});

		return new CliRelatedEntitiesTestOutput<ElkClass>(superNodes,
				ElkClassKeyProvider.INSTANCE);
	}

	public RelatedEntitiesTestOutput<ElkClass> getSubEntitiesTestOutput() {

		final Collection<Collection<ElkClass>> subNodes = Operations.map(
				subClasses_,
				new Operations.Transformation<ElkClass, Collection<ElkClass>>() {
					@Override
					public Collection<ElkClass> transform(final ElkClass cls) {
						final Map<ElkIri, ElkClass> result = equivalent_
								.get(cls);
						if (result != null) {
							return result.values();
						}
						// else
						return Collections.singleton(cls);
					}
				});

		return new CliRelatedEntitiesTestOutput<ElkClass>(subNodes,
				ElkClassKeyProvider.INSTANCE);
	}

	public RelatedEntitiesTestOutput<ElkNamedIndividual> getInstancesTestOutput() {

		final Collection<Collection<ElkNamedIndividual>> instances = Operations
				.map(instances_,
						new Operations.Transformation<ElkNamedIndividual, Collection<ElkNamedIndividual>>() {
							@Override
							public Collection<ElkNamedIndividual> transform(
									final ElkNamedIndividual ind) {
								final Map<ElkIri, ElkNamedIndividual> result = same_
										.get(ind);
								if (result != null) {
									return result.values();
								}
								// else
								return Collections.singleton(ind);
							}
						});

		return new CliRelatedEntitiesTestOutput<ElkNamedIndividual>(instances,
				ElkIndividualKeyProvider.INSTANCE);
	}

}
