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
package org.semanticweb.elk.reasoner.query;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.impl.SimpleNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

public class ElkExpectedTestOutputLoader {

	/**
	 * Loads an expected output of a class query test.
	 * <p>
	 * The expected output should be stored in an ontology that is loaded from
	 * the supplied argument. The complex classes in this ontology will be
	 * recognized as query classes. Classes equivalent to each query class
	 * should be in single equivalence axiom with the query class. Direct
	 * super(sub)-classes of each query class should be partitioned into sets of
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
	public static ElkExpectedTestOutputLoader load(
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

			return new ElkExpectedTestOutputLoader(complex, equivalent,
					superClasses, subClasses, same, instances);

		} catch (final Owl2ParseException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private final Set<ElkClassExpression> queryClasses_;
	private final Map<ElkClassExpression, Map<ElkIri, ElkClass>> equivalent_;
	private final Multimap<ElkClassExpression, ElkClass> superClasses_;
	private final Multimap<ElkClassExpression, ElkClass> subClasses_;
	private final Map<ElkIndividual, Map<ElkIri, ElkNamedIndividual>> same_;
	private final Multimap<ElkClassExpression, ElkNamedIndividual> instances_;

	private ElkExpectedTestOutputLoader(
			final Set<ElkClassExpression> queryClasses,
			final Map<ElkClassExpression, Map<ElkIri, ElkClass>> equivalent,
			final Multimap<ElkClassExpression, ElkClass> superClasses,
			final Multimap<ElkClassExpression, ElkClass> subClasses,
			final Map<ElkIndividual, Map<ElkIri, ElkNamedIndividual>> same,
			final Multimap<ElkClassExpression, ElkNamedIndividual> instances) {
		this.queryClasses_ = queryClasses;
		this.equivalent_ = equivalent;
		this.superClasses_ = superClasses;
		this.subClasses_ = subClasses;
		this.same_ = same;
		this.instances_ = instances;
	}

	public Collection<QueryTestManifest<ElkClassExpression, EmptyTestOutput>> getNoOutputManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, EmptyTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {
			result.add(
					new QueryTestManifest<ElkClassExpression, EmptyTestOutput>(
							name, input, queryClass, null));
		}

		return result;
	}

	public Collection<QueryTestManifest<ElkClassExpression, SatisfiabilityTestOutput>> getClassExpressionSatisfiabilityTestManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, SatisfiabilityTestOutput>> result = new ArrayList<QueryTestManifest<ElkClassExpression, SatisfiabilityTestOutput>>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {
			final Map<ElkIri, ElkClass> node = equivalent_.get(queryClass);
			// If the query class is equivalent to bottom, node is NOT null!
			result.add(
					new QueryTestManifest<ElkClassExpression, SatisfiabilityTestOutput>(
							name + " isSatisfiable", input, queryClass,
							new SatisfiabilityTestOutput(
									node == null || !node.containsKey(
											PredefinedElkIris.OWL_NOTHING),
									true)));
		}

		return result;
	}

	public Collection<QueryTestManifest<ElkClassExpression, ElkEquivalentClassesTestOutput>> getEquivalentClassesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, ElkEquivalentClassesTestOutput>> result = new ArrayList<QueryTestManifest<ElkClassExpression, ElkEquivalentClassesTestOutput>>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {
			final Map<ElkIri, ElkClass> node = equivalent_.get(queryClass);
			result.add(
					new QueryTestManifest<ElkClassExpression, ElkEquivalentClassesTestOutput>(
							name + " getEquivalentClasses", input, queryClass,
							new ElkEquivalentClassesTestOutput(
									node == null ? Collections.emptyMap()
											: node,
									true)));
		}

		return result;
	}

	public Collection<QueryTestManifest<ElkClassExpression, ElkDirectSuperClassesTestOutput>> getDirectSuperClassesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, ElkDirectSuperClassesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {

			final Collection<Node<ElkClass>> superNodes = Operations.map(
					superClasses_.get(queryClass),
					new Operations.Transformation<ElkClass, Node<ElkClass>>() {
						@Override
						public Node<ElkClass> transform(final ElkClass cls) {
							final Map<ElkIri, ElkClass> clsEquivalent = equivalent_
									.get(cls);
							if (clsEquivalent != null) {
								Collection<ElkClass> values = clsEquivalent
										.values();
								return new SimpleNode<>(values, values.size(),
										ElkClassKeyProvider.INSTANCE);
							}
							// else
							return new SimpleNode<>(Collections.singleton(cls),
									1, ElkClassKeyProvider.INSTANCE);
						}
					});

			result.add(
					new QueryTestManifest<ElkClassExpression, ElkDirectSuperClassesTestOutput>(
							name + " getDirectSuperClasses", input, queryClass,
							new ElkDirectSuperClassesTestOutput(queryClass,
									superNodes, true)));
		}

		return result;
	}

	public Collection<QueryTestManifest<ElkClassExpression, ElkDirectSubClassesTestOutput>> getDirectSubClassesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, ElkDirectSubClassesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {

			final Collection<Node<ElkClass>> subNodes = Operations.map(
					subClasses_.get(queryClass),
					new Operations.Transformation<ElkClass, Node<ElkClass>>() {
						@Override
						public Node<ElkClass> transform(final ElkClass cls) {
							final Map<ElkIri, ElkClass> clsEquivalent = equivalent_
									.get(cls);
							if (clsEquivalent != null) {
								Collection<ElkClass> values = clsEquivalent
										.values();
								return new SimpleNode<>(values, values.size(),
										ElkClassKeyProvider.INSTANCE);
							}
							// else
							return new SimpleNode<>(Collections.singleton(cls),
									1, ElkClassKeyProvider.INSTANCE);
						}
					});

			result.add(
					new QueryTestManifest<ElkClassExpression, ElkDirectSubClassesTestOutput>(
							name + " getDirectSubClasses", input, queryClass,

							new ElkDirectSubClassesTestOutput(queryClass, subNodes,
									true)));
		}

		return result;
	}

	public Collection<QueryTestManifest<ElkClassExpression, ElkDirectInstancesTestOutput>> getInstancesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<ElkClassExpression, ElkDirectInstancesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final ElkClassExpression queryClass : queryClasses_) {

			final Collection<Node<ElkNamedIndividual>> instances = Operations
					.map(instances_.get(queryClass),
							new Operations.Transformation<ElkNamedIndividual, Node<ElkNamedIndividual>>() {
								@Override
								public Node<ElkNamedIndividual> transform(
										final ElkNamedIndividual ind) {
									final Map<ElkIri, ElkNamedIndividual> indSame = same_
											.get(ind);
									if (indSame != null) {
										Collection<ElkNamedIndividual> values = indSame
												.values();
										return new SimpleNode<>(values,
												values.size(),
												ElkIndividualKeyProvider.INSTANCE);
									}
									// else
									return new SimpleNode<>(
											Collections.singleton(ind), 1,
											ElkIndividualKeyProvider.INSTANCE);
								}
							});

			result.add(
					new QueryTestManifest<ElkClassExpression, ElkDirectInstancesTestOutput>(
							name + " getDirectInstances", input, queryClass,
							new ElkDirectInstancesTestOutput(queryClass, instances,
									true)));
		}

		return result;
	}

	public Collection<EntailmentQueryTestManifest<Collection<ElkAxiom>>> getEntailmentManifests(
			final String name, final URL input) {

		final ElkObject.Factory elkFactory = new ElkObjectBaseFactory();

		final List<ElkAxiom> query = new ArrayList<ElkAxiom>();
		final Map<ElkAxiom, QueryResult> output = new HashMap<>();

		for (final ElkClassExpression queryClass : queryClasses_) {

			// Equivalent to queryClass.

			final Map<ElkIri, ElkClass> equivalent = equivalent_
					.get(queryClass);
			if (equivalent != null && !equivalent.isEmpty()) {

				final List<ElkClassExpression> equivalentClasses = new ArrayList<>(
						1 + equivalent.size());
				equivalentClasses.add(queryClass);
				equivalentClasses.addAll(equivalent.values());

				final ElkAxiom axiom = elkFactory
						.getEquivalentClassesAxiom(equivalentClasses);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, true));

			}

			// Superclasses of queryClass.

			final Collection<ElkClass> superClasses = superClasses_
					.get(queryClass);
			if (superClasses != null && !superClasses.isEmpty()) {
				final ElkClass superClass = superClasses.iterator().next();

				ElkAxiom axiom = elkFactory.getSubClassOfAxiom(queryClass,
						superClass);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, true));

				axiom = elkFactory.getSubClassOfAxiom(superClass, queryClass);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, false));

				if (equivalent != null && !equivalent.isEmpty()) {
					final List<ElkClassExpression> equivalentClasses = new ArrayList<ElkClassExpression>(
							2 + equivalent.size());
					equivalentClasses.add(queryClass);
					equivalentClasses.add(superClass);
					equivalentClasses.addAll(equivalent.values());
					axiom = elkFactory
							.getEquivalentClassesAxiom(equivalentClasses);
					query.add(axiom);
					output.put(axiom, new TestQueryResult(axiom, false));
				}

			}

			// Subclasses of queryClass.

			final Collection<ElkClass> subClasses = subClasses_.get(queryClass);
			if (subClasses != null && !subClasses.isEmpty()) {
				final ElkClass subClass = subClasses.iterator().next();

				ElkAxiom axiom = elkFactory.getSubClassOfAxiom(subClass,
						queryClass);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, true));

				axiom = elkFactory.getSubClassOfAxiom(queryClass, subClass);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, false));

				if (equivalent != null && !equivalent.isEmpty()) {
					final List<ElkClassExpression> equivalentClasses = new ArrayList<ElkClassExpression>(
							2 + equivalent.size());
					equivalentClasses.add(queryClass);
					equivalentClasses.add(subClass);
					equivalentClasses.addAll(equivalent.values());
					axiom = elkFactory
							.getEquivalentClassesAxiom(equivalentClasses);
					query.add(axiom);
					output.put(axiom, new TestQueryResult(axiom, false));
				}

			}

			// Instances of queryClass.

			final Collection<ElkNamedIndividual> instances = instances_
					.get(queryClass);
			if (instances != null && !instances.isEmpty()) {
				final ElkNamedIndividual instance = instances.iterator().next();

				ElkAxiom axiom = elkFactory.getClassAssertionAxiom(queryClass,
						instance);
				query.add(axiom);
				output.put(axiom, new TestQueryResult(axiom, true));

				// Find individual that is not an instance.
				ElkNamedIndividual notInstance = null;
				for (final ElkClassExpression type : instances_.keySet()) {
					for (final ElkNamedIndividual individual : instances_
							.get(type)) {
						if (!instances.contains(individual)) {
							notInstance = individual;
							break;
						}
					}
					if (notInstance != null) {
						break;
					}
				}
				if (notInstance != null) {

					axiom = elkFactory.getClassAssertionAxiom(queryClass,
							notInstance);
					query.add(axiom);
					output.put(axiom, new TestQueryResult(axiom, false));

				}

			}

		}

		if (query.isEmpty()) {
			return Collections.emptySet();
		}
		// else

		try {
			return Collections.singleton(
					new EntailmentQueryTestManifest<Collection<ElkAxiom>>(name,
							input, query,
							new ElkEntailmentQueryTestOutput(output)));
		} catch (ElkQueryException e) {
			throw new RuntimeException(e);
		}
	}

}
