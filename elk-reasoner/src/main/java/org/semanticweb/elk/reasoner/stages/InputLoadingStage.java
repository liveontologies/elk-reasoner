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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ClassQueryLoader;
import org.semanticweb.elk.loading.EntailmentQueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.CombinedElkAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionProcessor;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.completeness.OccurrencesInOntology;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.incremental.AxiomLoadingListener;
import org.semanticweb.elk.reasoner.indexing.classes.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.ClassQueryIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.NonIncrementalElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.query.EntailmentQueryIndexingProcessor;
import org.semanticweb.elk.reasoner.query.IndexedEntailmentQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ReasonerStage} during which the input is loaded into the reasoner.
 * Input consists of ontology, changes to the ontology, or queried classes. This
 * stage is also responsible for setting the incremental mode.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class InputLoadingStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(InputLoadingStage.class);

	/**
	 * the {@link AxiomLoader} using which the axioms are loaded
	 */
	private volatile AxiomLoader loader_;

	/**
	 * the {@link ClassQueryLoader} using which the class queries are loaded
	 */
	private volatile ClassQueryLoader classQueryLoader_;

	/**
	 * the {@link EntailmentQueryLoader} using which the entailment queries are
	 * loaded
	 */
	private volatile EntailmentQueryLoader entailmentQueryLoader_;

	/**
	 * {@code true} if this stage was not yet successfully completed before
	 */
	private boolean firstLoad_ = true;

	/**
	 * the {@link ElkAxiomProcessor}s using which the axioms are inserted and
	 * deleted
	 */
	private ElkAxiomProcessor axiomInsertionProcessor_, axiomDeletionProcessor_;

	/**
	 * the {@link ElkClassExpressionProcessor}s using which the class queries
	 * are inserted and deleted
	 */
	private ElkClassExpressionProcessor classQueryInsertionProcessor_,
			classQueryDeletionProcessor_;

	/**
	 * used to insert and delete entailment queries
	 */
	private ElkAxiomVisitor<IndexedEntailmentQuery<? extends Entailment>> entailmentQueryInserter_,
			entailmentQueryDeleter_;

	/**
	 * the ontology index where the loaded and indexed axioms are stored.
	 */
	private ModifiableOntologyIndex ontologyIndex_;

	public InputLoadingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Input Loading";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}
		if (!firstLoad_ && !reasoner.saturationState.getContexts().isEmpty()) {
			reasoner.trySetIncrementalMode();
		}
		loader_ = reasoner.getAxiomLoader();
		classQueryLoader_ = reasoner.getClassQueryLoader();
		entailmentQueryLoader_ = reasoner.getEntailmentQueryLoader();

		ontologyIndex_ = reasoner.getModifiableOntologyIndex();
		ElkObject.Factory elkFactory = reasoner.getElkFactory();

		if (loader_ != null && !loader_.isLoadingFinished()) {

			ElkAxiomConverter axiomInserter = new ElkAxiomConverterImpl(
					elkFactory, ontologyIndex_, 1);
			ElkAxiomConverter axiomDeleter = new ElkAxiomConverterImpl(
					elkFactory, ontologyIndex_, -1);

			/*
			 * wrapping both the inserter and the deleter to receive
			 * notifications if some axiom change can't be incorporated
			 * incrementally
			 */
			/**
			 * The listener used to count axioms and detect if the axiom cannot
			 * be loaded incrementally
			 */
			AxiomLoadingListener<ElkAxiom> listener = new AxiomLoadingListener<ElkAxiom>() {

				boolean resetDone = false;

				@Override
				public void notify(ElkAxiom axiom) {
					if (resetDone)
						return;
					LOGGER_.debug("{}: axiom not supported in incremental mode",
							axiom);
					reasoner.stageManager.propertyInitializationStage
							.invalidateRecursive();
					reasoner.setNonIncrementalMode();
					resetDone = true;
				}
			};

			axiomInserter = new NonIncrementalElkAxiomVisitor(axiomInserter,
					listener);
			axiomDeleter = new NonIncrementalElkAxiomVisitor(axiomDeleter,
					listener);

			this.axiomInsertionProcessor_ = new ChangeIndexingProcessor(
					axiomInserter, 1, ontologyIndex_);
			this.axiomDeletionProcessor_ = new ChangeIndexingProcessor(
					axiomDeleter, -1, ontologyIndex_);

		}

		if (classQueryLoader_ != null
				&& !classQueryLoader_.isLoadingFinished()) {

			classQueryInsertionProcessor_ = new ClassQueryIndexingProcessor(
					new ElkPolarityExpressionConverterImpl(elkFactory,
							ontologyIndex_, 1),
					1, ontologyIndex_);
			classQueryDeletionProcessor_ = new ClassQueryIndexingProcessor(
					new ElkPolarityExpressionConverterImpl(elkFactory,
							ontologyIndex_, -1),
					-1, ontologyIndex_);

		}

		if (entailmentQueryLoader_ != null
				&& !entailmentQueryLoader_.isLoadingFinished()) {

			entailmentQueryInserter_ = new EntailmentQueryIndexingProcessor(
					elkFactory, ontologyIndex_, 1, ontologyIndex_);
			entailmentQueryDeleter_ = new EntailmentQueryIndexingProcessor(
					elkFactory, ontologyIndex_, -1, ontologyIndex_);

		}

		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		if (loader_ != null && !loader_.isLoadingFinished()) {
			OccurrencesInOntology ontologyFeatures = reasoner
					.getOccurrencesInOntology();
			ontologyIndex_.addOccurrenceListener(ontologyFeatures);
			try {
				loader_.load(
						new CombinedElkAxiomProcessor(ontologyFeatures,
								axiomInsertionProcessor_),
						new CombinedElkAxiomProcessor(ontologyFeatures,
								axiomDeletionProcessor_));
			} finally {
				ontologyIndex_.removeOccurrenceListener(ontologyFeatures);
			}
		}
		if (classQueryLoader_ != null
				&& !classQueryLoader_.isLoadingFinished()) {
			OccurrenceListener classFeatureListener = reasoner.classExpressionQueryState
					.getOccurrenceListener();			
			ontologyIndex_.addOccurrenceListener(classFeatureListener);
			try {
				classQueryLoader_.load(classQueryInsertionProcessor_,
						classQueryDeletionProcessor_);
			} finally {
				ontologyIndex_.removeOccurrenceListener(classFeatureListener);
			}
		}
		if (entailmentQueryLoader_ != null
				&& !entailmentQueryLoader_.isLoadingFinished()) {
			OccurrenceListener entailmentFeatureListener =
					reasoner.entailmentQueryState.getOccurrenceListener();
			ontologyIndex_.addOccurrenceListener(entailmentFeatureListener);
			try {
				entailmentQueryLoader_.load(entailmentQueryInserter_,
						entailmentQueryDeleter_);
			} finally {
				ontologyIndex_
						.removeOccurrenceListener(entailmentFeatureListener);
			}
		}
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		if (loader_ != null && !loader_.isLoadingFinished()) {
			throw new ElkRuntimeException("Axiom loading not finished!");
		}
		if (classQueryLoader_ != null
				&& !classQueryLoader_.isLoadingFinished()) {
			throw new ElkRuntimeException("Class query loading not finished!");
		}
		if (entailmentQueryLoader_ != null
				&& !entailmentQueryLoader_.isLoadingFinished()) {
			throw new ElkRuntimeException(
					"Entailment query loading not finished!");
		}
		this.firstLoad_ = false;
		this.loader_ = null;
		this.classQueryLoader_ = null;
		this.entailmentQueryLoader_ = null;
		this.ontologyIndex_ = null;
		this.axiomInsertionProcessor_ = null;
		this.axiomDeletionProcessor_ = null;
		this.classQueryInsertionProcessor_ = null;
		this.classQueryDeletionProcessor_ = null;
		this.entailmentQueryInserter_ = null;
		this.entailmentQueryDeleter_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}

}
