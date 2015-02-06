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
package org.semanticweb.elk.reasoner.stages;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.NonIncrementalChangeListener;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomIndexingVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.MainAxiomIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.NonIncrementalChangeCheckingVisitor;

/**
 * A {@link ReasonerStage} during which the input ontology is loaded into the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class AxiomLoadingStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(AxiomLoadingStage.class);

	private volatile AxiomLoader loader_;
	private ElkAxiomProcessor axiomInsertionProcessor_,
			axiomDeletionProcessor_;

	public AxiomLoadingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Loading of Axioms";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		loader_ = reasoner.getAxiomLoader();
		if (loader_ == null || loader_.isLoadingFinished()) {
			return true;
		}

		ModifiableOntologyIndex ontologyIndex = reasoner
				.getModifiableOntologyIndex();

		ElkAxiomIndexingVisitor axiomInserter = new MainAxiomIndexerVisitor(
				ontologyIndex, true);
		ElkAxiomIndexingVisitor axiomDeleter = new MainAxiomIndexerVisitor(
				ontologyIndex, false);

		/*
		 * wrapping both the inserter and the deleter to receive notifications
		 * if some axiom change can't be incorporated incrementally
		 */
		/**
		 * The listener used to detect if the axiom has an impact on the role
		 * hierarchy
		 */
		NonIncrementalChangeListener<ElkAxiom> listener = new NonIncrementalChangeListener<ElkAxiom>() {

			boolean resetDone = false;

			@Override
			public void notify(ElkAxiom axiom) {
				if (resetDone)
					return;
				if (LOGGER_.isDebugEnabled()) {
					LOGGER_.debug("Disallowing incremental mode due to "
							+ OwlFunctionalStylePrinter.toString(axiom));
				}
				reasoner.resetPropertySaturation();
				reasoner.setNonIncrementalMode();
				resetDone = true;
			}
		};

		axiomInserter = new NonIncrementalChangeCheckingVisitor(axiomInserter,
				listener);
		axiomDeleter = new NonIncrementalChangeCheckingVisitor(axiomDeleter,
				listener);

		this.axiomInsertionProcessor_ = new ChangeIndexingProcessor(
				axiomInserter);
		this.axiomDeletionProcessor_ = new ChangeIndexingProcessor(axiomDeleter);
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		loader_.load(axiomInsertionProcessor_, axiomDeletionProcessor_);
	}

	@Override
	public boolean dispose() {
		if (!super.dispose())
			return false;
		loader_.dispose();
		loader_ = null;
		return true;
	}

	@Override
	public void printInfo() {
	}

	@Override
	public void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(loader_, flag);
	}

}
