package org.semanticweb.elk.reasoner.taxonomy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
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
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.DummyElkEntityVisitor;
import org.semanticweb.elk.reasoner.taxonomy.MockInstanceTaxonomy.MutableTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class MockTaxonomyLoader {

	/**
	 * Loads an {@link InstanceTaxonomy} using the given
	 * {@link ElkObject.Factory} and {@link Owl2Parser}. The
	 * {@link ElkObject.Factory} should create equal {@link ElkClass}s and
	 * {@link ElkIndividual}s for equal {@link ElkIri}s (w.r.t. to the
	 * {@link #equals(Object)} method. The {@link Owl2Parser} should use the
	 * same {@link ElkObject.Factory}.
	 * 
	 * 
	 * @param factory
	 *            the factory for creating owl:Thing and owl:Nothing
	 * 
	 * @param parser
	 *            the {@link Owl2Parser} for loading the ontology representing
	 *            the taxonomy; it should use the same {@link ElkObject.Factory}
	 * 
	 * @return the {@link InstanceTaxonomy} constructed from the parsed ontology
	 * 
	 * @throws Owl2ParseException
	 *             if the ontology cannot be parsed by the parser
	 */
	public static InstanceTaxonomy<ElkClass, ElkNamedIndividual> load(
			PredefinedElkClassFactory factory, Owl2Parser parser)
			throws Owl2ParseException {
		final MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				factory.getOwlThing(),
				factory.getOwlNothing(),
				ElkClassKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);
		TaxonomyInserter listener = new TaxonomyInserter(taxonomy);

		parser.accept(listener);

		listener.createNodes = true;
		// process the remaining axioms, the order is important
		process(listener, listener.classDeclarations);
		process(listener, listener.subClassOfAxioms);
		process(listener, listener.individualDeclarations);

		return taxonomy;
	}

	private static void process(TaxonomyInserter inserter, List<ElkAxiom> axioms) {
		for (ElkAxiom decl : axioms) {
			decl.accept(inserter);
		}
	}

	static class TaxonomyInserter extends DummyElkAxiomVisitor<Void> implements
			Owl2ParserAxiomProcessor {

		boolean createNodes = false;
		final MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy;
		final List<ElkAxiom> subClassOfAxioms = new ArrayList<ElkAxiom>();
		final List<ElkAxiom> classDeclarations = new ArrayList<ElkAxiom>();
		final List<ElkAxiom> individualDeclarations = new ArrayList<ElkAxiom>();

		TaxonomyInserter(
				final MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy) {
			this.taxonomy = taxonomy;
		}

		@Override
		protected Void defaultLogicalVisit(ElkAxiom axiom) {
			return null;
		}

		@Override
		protected Void defaultNonLogicalVisit(ElkAxiom axiom) {
			return null;
		}

		@Override
		public Void visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
			// a new node
			Set<ElkClass> classes = new HashSet<ElkClass>();

			for (ElkClassExpression ce : elkEquivalentClassesAxiom
					.getClassExpressions()) {
				if (ce instanceof ElkClass) {
					classes.add((ElkClass) ce);
				}
			}

			taxonomy.getCreateTypeNode(classes);

			return null;
		}
		
		@Override
		public Void visit(ElkSameIndividualAxiom axiom) {
			// a new instance node
			Set<ElkNamedIndividual> individuals = new HashSet<ElkNamedIndividual>();

			for (ElkIndividual ind : axiom.getIndividuals()) {
				if (ind instanceof ElkNamedIndividual) {
					individuals.add((ElkNamedIndividual) ind);
				}
			}
			
			taxonomy.getCreateInstanceNode(individuals);
			
			return null;
		}

		@Override
		public Void visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
			// a subclass relationship between canonical members of two nodes
			ElkClassExpression subCE = elkSubClassOfAxiom
					.getSubClassExpression();
			ElkClassExpression superCE = elkSubClassOfAxiom
					.getSuperClassExpression();

			if (subCE instanceof ElkClass && superCE instanceof ElkClass) {
				ElkClass subClass = (ElkClass) subCE;
				ElkClass superClass = (ElkClass) superCE;
				// check if both nodes are there yet
				MutableTypeNode<ElkClass, ElkNamedIndividual> subNode = taxonomy
						.getNode(subClass);
				MutableTypeNode<ElkClass, ElkNamedIndividual> superNode = taxonomy
						.getNode(superClass);

				if ((subNode == null || superNode == null) && !createNodes) {
					// wait, maybe we'll create these nodes later
					subClassOfAxioms.add(elkSubClassOfAxiom);
				} else {
					subNode = taxonomy.getCreateTypeNode(Collections
							.singleton(subClass));
					superNode = taxonomy.getCreateTypeNode(Collections
							.singleton(superClass));

					if (!subNode.equals(superNode)) {
						subNode.addDirectParent(superNode);
					}
				}
			}

			return null;
		}

		@Override
		public Void visit(final ElkDeclarationAxiom elkDeclarationAxiom) {
			ElkEntity entity = elkDeclarationAxiom.getEntity();
			// support two sorts of declarations: classes and named
			// individuals
			entity.accept(new DummyElkEntityVisitor<Object>() {

				@Override
				public Object visit(ElkClass elkClass) {
					if (createNodes) {
						taxonomy.getCreateTypeNode(Collections
								.singleton(elkClass));
					} else {
						classDeclarations.add(elkDeclarationAxiom);
					}

					return null;
				}

				@Override
				public Object visit(ElkNamedIndividual elkNamedIndividual) {
					if (createNodes) {
						taxonomy.getCreateInstanceNode(
								Collections.singleton(elkNamedIndividual),
								Collections
										.<TypeNode<ElkClass, ElkNamedIndividual>> emptyList());
					} else {
						individualDeclarations.add(elkDeclarationAxiom);
					}

					return null;

				}
			});

			return null;
		}

		@Override
		public Void visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
			if (elkClassAssertionAxiom.getClassExpression() instanceof ElkClass
					&& elkClassAssertionAxiom.getIndividual() instanceof ElkNamedIndividual) {
				ElkClass type = (ElkClass) elkClassAssertionAxiom
						.getClassExpression();
				ElkNamedIndividual individual = (ElkNamedIndividual) elkClassAssertionAxiom
						.getIndividual();
				MutableTypeNode<ElkClass, ElkNamedIndividual> typeNode = taxonomy
						.getNode(type);

				if (typeNode == null && !createNodes) {
					// wait
					subClassOfAxioms.add(elkClassAssertionAxiom);
				} else {
					MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>.MockInstanceNode indNode = taxonomy
							.getInstanceNode(individual);

					typeNode = taxonomy.getCreateTypeNode(Collections
							.singleton(type));

					if (indNode == null) {
						// may need to create the type node
						indNode = taxonomy
								.getCreateInstanceNode(
										Collections.singleton(individual),
										Collections
												.<TypeNode<ElkClass, ElkNamedIndividual>> singleton(typeNode));
					} else {
						// may need to add this instance to its type
						typeNode.addDirectInstance(indNode);
					}
				}
			}

			return null;
		}

		@Override
		public void visit(ElkAxiom elkAxiom) {
			elkAxiom.accept(this);
		}

		@Override
		public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			// ignored
		}

		@Override
		public void finish() throws Owl2ParseException {
			// everything is processed immediately
		}

	}
}