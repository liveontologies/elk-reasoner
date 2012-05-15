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
import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;

/**
 * A simple class to load class taxonomy using a prepared parser. To be used
 * mostly for testing.
 * 
 * WARNING: currently this class can only load taxonomies dumped by
 * {@link ClassTaxonomyPrinter}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class ClassTaxonomyLoader {

	public static ClassTaxonomy load(Owl2Parser parser) throws IOException,
			Owl2ParseException {
		final ConcurrentClassTaxonomy taxonomy = new ConcurrentClassTaxonomy();
		TaxonomyInserter listener = new TaxonomyInserter(taxonomy);

		parser.parseOntology(listener);
		listener.createNodes = true;
		// process the remaining axioms
		for (ElkAxiom remaining : listener.nonProcessedAxioms) {
			remaining.accept(listener);
		}
		// add owl:Thing if needed
		ElkClass thing = PredefinedElkClass.OWL_THING;
		// unless it's inconsistent
		if (taxonomy.getNode(thing) == taxonomy)
			return taxonomy;

		NonBottomNode topNode = taxonomy
				.getCreate(Collections.singleton(thing));
		ClassNode botNode = taxonomy.getNode(PredefinedElkClass.OWL_NOTHING);

		for (ClassNode node : taxonomy.getNodes()) {
			if (node == topNode || node == botNode)
				continue;

			NonBottomNode nbNode = (NonBottomNode) node;

			if (node.getDirectSuperNodes().isEmpty()) {
				nbNode.addDirectSuperNode(topNode);
				topNode.addDirectSubNode(nbNode);
			}
		}

		return taxonomy;
	}

	static class TaxonomyInserter extends AbstractElkAxiomVisitor<Void>
			implements ElkAxiomProcessor {

		boolean createNodes = false;
		final ConcurrentClassTaxonomy taxonomy;
		final List<ElkSubClassOfAxiom> nonProcessedAxioms = new ArrayList<ElkSubClassOfAxiom>();

		TaxonomyInserter(final ConcurrentClassTaxonomy taxonomy) {
			this.taxonomy = taxonomy;
		}

		@Override
		protected Void defaultLogicalVisit(ElkAxiom axiom) {
			return null;
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

					if (clazz.getIri().equals(PredefinedElkIri.OWL_NOTHING)) {
						nothing = true;
					} else {
						classes.add(clazz);
					}
				}
			}

			if (!nothing) {
				taxonomy.getCreate(classes);
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
				ClassNode subNode = taxonomy.getNode(subClass);
				ClassNode superNode = taxonomy.getNode(superClass);
				NonBottomNode subNonBot = null;
				NonBottomNode superNonBot = null;

				if ((subNode == null || superNode == null) && !createNodes) {
					// wait, maybe we'll create these nodes later
					nonProcessedAxioms.add(elkSubClassOfAxiom);
				} else {
					subNonBot = (NonBottomNode) (subNode == null ? taxonomy
							.getCreate(Collections.singleton(subClass))
							: subNode);
					superNonBot = (NonBottomNode) (superNode == null ? taxonomy
							.getCreate(Collections.singleton(superClass))
							: superNode);

					subNonBot.addDirectSuperNode(superNonBot);
					superNonBot.addDirectSubNode(subNonBot);
				}
			}

			return null;
		}

		@Override
		public void process(ElkAxiom elkAxiom) {
			elkAxiom.accept(this);
		}
	}
}