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
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertyTaxonomyComputation;
import org.semanticweb.elk.reasoner.saturation.properties.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.saturation.properties.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.saturation.properties.TransitiveReductionOutputExtreme;
import org.semanticweb.elk.reasoner.saturation.properties.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.taxonomy.ElkObjectPropertyKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ReverseObjectPropertyTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ReverseTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Keeps track of state of the object property taxonomy. Currently contains only
 * the taxonomy.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyTaxonomyState {

	private final PredefinedElkObjectPropertyFactory elkFactory_;

	/**
	 * Object property taxonomy is constructed in the opposite order than class
	 * taxonomy, i.e., from super-properties to sub-properties. In order to use
	 * the same taxonomy implementation as for the class taxonomy, property
	 * taxonomy is up side down. It must be reversed before passing it out of
	 * the reasoner!
	 */
	private ReverseObjectPropertyTaxonomy taxonomy_ = null;

	public ObjectPropertyTaxonomyState(
			final PredefinedElkObjectPropertyFactory elkFactory) {
		this.elkFactory_ = elkFactory;
	}

	public Taxonomy<ElkObjectProperty> getTaxonomy() {
		return new ReverseTaxonomy<ElkObjectProperty>(taxonomy_);
	}

	private void initTaxonomy() {
		taxonomy_ = new ReverseObjectPropertyTaxonomy(elkFactory_,
				ElkObjectPropertyKeyProvider.INSTANCE);
	}

	private void clearTaxonomy() {
		taxonomy_ = null;
	}

	/**
	 * Receives the output of the transitive reduction of sub object properties
	 * and constructs the taxonomy from it.
	 */
	private final TransitiveReductionOutputVisitor<ElkObjectProperty> transitiveReductionOutputProcessor_ = new TransitiveReductionOutputVisitor<ElkObjectProperty>() {

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalentDirect<ElkObjectProperty> output) {
			// Properties equivalent to top should not be submitted here.

			final NonBottomTaxonomyNode<ElkObjectProperty> node = taxonomy_
					.getCreateNode(output.getEquivalent());
			taxonomy_.setCreateDirectSupernodes(node,
					output.getDirectlyRelated());

		}

		@Override
		public void visit(
				final TransitiveReductionOutputExtreme<ElkObjectProperty> output) {
			taxonomy_.addToBottomNode(output.getExtremeMember());
		}

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalent<ElkObjectProperty> output) {
			throw new IllegalArgumentException(
					"Object property transitive reduction is not expected to emit "
							+ output.getClass());
		}

	};

	/**
	 * Creates stage that computes object property taxonomy.
	 * 
	 * @param reasoner
	 *            the reasoner for which the reasoner stage is created
	 * @param preStages
	 *            the reasoner stages that should be executed directly before
	 *            this stage
	 * @return the stage that computes object property taxonomy.
	 */
	AbstractReasonerStage createStage(final AbstractReasonerState reasoner,
			final AbstractReasonerStage... preStages) {
		return new AbstractReasonerStage(reasoner, preStages) {

			/**
			 * The computation used for this stage.
			 */
			private ObjectPropertyTaxonomyComputation computation_ = null;

			@Override
			public String getName() {
				return "Object Property Taxonomy Computation";
			}

			@Override
			public boolean preExecute() {
				if (!super.preExecute()) {
					return false;
				}

				initTaxonomy();

				computation_ = new ObjectPropertyTaxonomyComputation(
						reasoner.ontologyIndex, reasoner.getInterrupter(),
						transitiveReductionOutputProcessor_,
						reasoner.getElkFactory(), reasoner.getProcessExecutor(),
						workerNo, reasoner.getProgressMonitor());

				return true;
			}

			@Override
			void executeStage() throws ElkException {
				computation_.process();
			}

			@Override
			public boolean postExecute() {
				if (!super.postExecute()) {
					return false;
				}
				this.computation_ = null;
				return true;
			}

			@Override
			boolean invalidate() {
				final boolean invalidated = super.invalidate();
				if (invalidated) {
					clearTaxonomy();
				}
				return invalidated;
			}

			@Override
			public void printInfo() {
				// TODO Auto-generated method stub

			}

		};
	}

}
