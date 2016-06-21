/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.taxonomy.MockInstanceTaxonomy.MutableTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Peter Skocovsky
 */
public class MockObjectPropertyTaxonomyLoader {

	public static Taxonomy<ElkObjectProperty> load(
			final PredefinedElkObjectPropertyFactory factory,
			final Owl2Parser parser) throws Owl2ParseException {

		final MockInstanceTaxonomy<ElkObjectProperty, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkObjectProperty, ElkNamedIndividual>(
				factory.getOwlTopObjectProperty(),
				factory.getOwlBottomObjectProperty(),
				ElkObjectPropertyKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);

		final Collector collector = new Collector();

		parser.accept(collector);

		final TaxonomyInserter inserter = new TaxonomyInserter(taxonomy);

		process(inserter, collector.equivalentPropertiesAxioms);
		process(inserter, collector.declarationAxioms);
		process(inserter, collector.subPropertiesAxioms);

		return taxonomy;
	}

	private static <O> void process(final ElkAxiomVisitor<O> visitor,
			final Collection<? extends ElkAxiom> axioms) {
		for (final ElkAxiom axiom : axioms) {
			axiom.accept(visitor);
		}
	}

	private static class Collector extends DummyElkAxiomVisitor<Void>
			implements Owl2ParserAxiomProcessor {

		public final Collection<ElkEquivalentObjectPropertiesAxiom> equivalentPropertiesAxioms = new ArrayList<ElkEquivalentObjectPropertiesAxiom>();
		public final Collection<ElkSubObjectPropertyOfAxiom> subPropertiesAxioms = new ArrayList<ElkSubObjectPropertyOfAxiom>();
		public final Collection<ElkDeclarationAxiom> declarationAxioms = new ArrayList<ElkDeclarationAxiom>();

		@Override
		public Void visit(
				final ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
			equivalentPropertiesAxioms.add(elkEquivalentObjectProperties);
			return null;
		}

		@Override
		public Void visit(final ElkDeclarationAxiom elkDeclarationAxiom) {
			declarationAxioms.add(elkDeclarationAxiom);
			return null;
		}

		@Override
		public Void visit(
				final ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
			subPropertiesAxioms.add(elkSubObjectPropertyOfAxiom);
			return null;
		}

		@Override
		public void visit(final ElkAxiom elkAxiom) throws Owl2ParseException {
			elkAxiom.accept(this);
		}

		@Override
		public void visit(final ElkPrefix elkPrefix) throws Owl2ParseException {
			// Empty.
		}

		@Override
		public void finish() throws Owl2ParseException {
			// Empty.
		}

	}

	private static class TaxonomyInserter extends DummyElkAxiomVisitor<Void> {

		final MockInstanceTaxonomy<ElkObjectProperty, ElkNamedIndividual> taxonomy_;

		public TaxonomyInserter(
				final MockInstanceTaxonomy<ElkObjectProperty, ElkNamedIndividual> taxonomy) {
			this.taxonomy_ = taxonomy;
		}

		@Override
		public Void visit(
				final ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {

			final Set<ElkObjectProperty> props = new HashSet<ElkObjectProperty>();

			for (final ElkObjectPropertyExpression prop : elkEquivalentObjectProperties
					.getObjectPropertyExpressions()) {
				if (prop instanceof ElkObjectProperty) {
					props.add((ElkObjectProperty) prop);
				}
			}

			taxonomy_.getCreateTypeNode(props);

			return null;
		}

		@Override
		public Void visit(final ElkDeclarationAxiom elkDeclarationAxiom) {

			final ElkEntity entity = elkDeclarationAxiom.getEntity();

			if (entity instanceof ElkObjectProperty) {
				taxonomy_.getCreateTypeNode(
						Collections.singleton((ElkObjectProperty) entity));
			}

			return null;
		}

		@Override
		public Void visit(
				final ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {

			final ElkSubObjectPropertyExpression subExpression = elkSubObjectPropertyOfAxiom
					.getSubObjectPropertyExpression();
			final ElkSubObjectPropertyExpression superExpression = elkSubObjectPropertyOfAxiom
					.getSuperObjectPropertyExpression();

			if (subExpression instanceof ElkObjectProperty
					&& superExpression instanceof ElkObjectProperty) {
				final ElkObjectProperty subProperty = (ElkObjectProperty) subExpression;
				final ElkObjectProperty superProperty = (ElkObjectProperty) superExpression;

				final MutableTypeNode<ElkObjectProperty, ElkNamedIndividual> subNode = taxonomy_
						.getCreateTypeNode(Collections.singleton(subProperty));
				final MutableTypeNode<ElkObjectProperty, ElkNamedIndividual> superNode = taxonomy_
						.getCreateTypeNode(
								Collections.singleton(superProperty));

				if (!subNode.equals(superNode)) {
					subNode.addDirectParent(superNode);
				}
			}

			return null;
		}

	}

}
