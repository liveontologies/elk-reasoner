/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.AbstractElkEntityVisitor;

/**
 * A simple class to load class taxonomy using a prepared parser. To be used
 * mostly for testing.
 * 
 * TODO Seriously consider adding a simple impl of Taxonomy/InstanceTaxonomy for
 * loading purposes TODO Support SameIndividual axioms
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class TaxonomyLoader {

	public static InstanceTaxonomy<ElkClass, ElkNamedIndividual> load(
			Owl2Parser parser) throws IOException, Owl2ParseException {
		final ConcurrentTaxonomy taxonomy = new ConcurrentTaxonomy();
		TaxonomyInserter listener = new TaxonomyInserter(taxonomy);

		parser.accept(listener);
		// add owl:Thing if needed
		ElkClass thing = PredefinedElkClass.OWL_THING;
		// unless it's inconsistent
		if (taxonomy.unsatisfiableClasses.contains(thing)) {
			return taxonomy;
		}

		NonBottomClassNode topNode = taxonomy.getCreateClassNode(Collections
				.singleton(thing));

		listener.createNodes = true;
		// process the remaining axioms, the order is important
		process(listener, listener.classDeclarations);
		process(listener, listener.subClassOfAxioms);
		process(listener, listener.individualDeclarations);

		TaxonomyNode<ElkClass> botNode = taxonomy
				.getNode(PredefinedElkClass.OWL_NOTHING);

		for (TaxonomyNode<ElkClass> node : taxonomy.getNodes()) {
			if (node == topNode || node == botNode)
				continue;

			NonBottomClassNode nbNode = (NonBottomClassNode) node;

			if (node.getDirectSuperNodes().isEmpty()) {
				nbNode.addDirectSuperNode(topNode);
				topNode.addDirectSubNode(nbNode);
			}
		}

		return taxonomy;
	}

	private static void process(TaxonomyInserter inserter, List<ElkAxiom> axioms) {
		for (ElkAxiom decl : axioms) {
			decl.accept(inserter);
		}
	}

	static class TaxonomyInserter extends AbstractElkAxiomVisitor<Void>
			implements Owl2ParserAxiomProcessor {

		boolean createNodes = false;
		final ConcurrentTaxonomy taxonomy;
		final List<ElkAxiom> subClassOfAxioms = new ArrayList<ElkAxiom>();
		final List<ElkAxiom> classDeclarations = new ArrayList<ElkAxiom>();
		final List<ElkAxiom> individualDeclarations = new ArrayList<ElkAxiom>();

		TaxonomyInserter(final ConcurrentTaxonomy taxonomy) {
			this.taxonomy = taxonomy;
		}

		@Override
		public Void visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
			// a new node
			Set<ElkClass> classes = new HashSet<ElkClass>();
			boolean nothing = false;

			for (ElkClassExpression ce : elkEquivalentClassesAxiom
					.getClassExpressions()) {
				if (ce instanceof ElkClass) {
					ElkClass clazz = (ElkClass) ce;

					if (clazz.getIri().equals(
							PredefinedElkIri.OWL_NOTHING.get())) {
						nothing = true;
					} else {
						classes.add(clazz);
					}
				}
			}

			if (!nothing) {
				taxonomy.getCreateClassNode(classes);
			} else {
				taxonomy.unsatisfiableClasses.addAll(classes);
			}

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
				TaxonomyNode<ElkClass> subNode = taxonomy.getNode(subClass);
				TaxonomyNode<ElkClass> superNode = taxonomy.getNode(superClass);
				NonBottomClassNode subNonBot = null;
				NonBottomClassNode superNonBot = null;

				if ((subNode == null || superNode == null) && !createNodes) {
					// wait, maybe we'll create these nodes later
					subClassOfAxioms.add(elkSubClassOfAxiom);
				} else {
					subNonBot = (NonBottomClassNode) (subNode == null ? taxonomy
							.getCreateClassNode(Collections.singleton(subClass))
							: subNode);
					superNonBot = (NonBottomClassNode) (superNode == null ? taxonomy
							.getCreateClassNode(Collections
									.singleton(superClass)) : superNode);

					subNonBot.addDirectSuperNode(superNonBot);
					superNonBot.addDirectSubNode(subNonBot);
				}
			}

			return null;
		}

		@Override
		public Void visit(final ElkDeclarationAxiom elkDeclarationAxiom) {
			ElkEntity entity = elkDeclarationAxiom.getEntity();
			// support two sorts of declarations: classes and named
			// individuals
			entity.accept(new AbstractElkEntityVisitor<Object>() {

				@Override
				public Object visit(ElkClass elkClass) {
					if (createNodes) {
						if (taxonomy.getNode(elkClass) == null) {
							taxonomy.getCreateClassNode(Collections
									.singleton(elkClass));
						}
					} else {
						classDeclarations.add(elkDeclarationAxiom);
					}

					return null;
				}

				@Override
				public Object visit(ElkNamedIndividual elkNamedIndividual) {
					if (createNodes) {
						if (taxonomy.getInstanceNode(elkNamedIndividual) == null) {
							NonBottomClassNode top = (NonBottomClassNode) taxonomy
									.getTopNode();
							IndividualNode indNode = (IndividualNode) taxonomy
									.getCreateIndividualNode(Collections
											.singleton(elkNamedIndividual));
							indNode.addDirectTypeNode(top);
							top.addDirectInstanceNode(indNode);
						}
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

				TaxonomyNode<ElkClass> typeNode = taxonomy.getNode(type);

				if (typeNode == null && !createNodes) {
					// wait
					subClassOfAxioms.add(elkClassAssertionAxiom);
				} else {
					IndividualNode indNode = (IndividualNode) taxonomy
							.getInstanceNode(individual);

					if (indNode == null) {
						indNode = taxonomy.getCreateIndividualNode(Collections
								.singleton(individual));
					}

					if (typeNode instanceof NonBottomClassNode) {
						((NonBottomClassNode) typeNode)
								.addDirectInstanceNode(indNode);
						indNode.addDirectTypeNode((NonBottomClassNode) typeNode);
					} else {
						// TODO Shouldn't happen, log it?
					}
				}
			}

			return null;
		}

		@Override
		public void visit(ElkAxiom elkAxiom) {
			elkAxiom.accept(this);
		}
	}
}