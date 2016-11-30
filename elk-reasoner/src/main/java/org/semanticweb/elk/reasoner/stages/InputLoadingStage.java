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
import org.semanticweb.elk.loading.QueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionProcessor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.incremental.AxiomLoadingListener;
import org.semanticweb.elk.reasoner.indexing.classes.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.classes.NonIncrementalElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.classes.QueryIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
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
	 * the {@link QueryLoader} using which the queries are loaded
	 */
	private volatile QueryLoader queryLoader_;
	
	/**
	 * {@code true} if this stage was not yet successfully completed before
	 */
	private boolean firstLoad_ = true;

	/**
	 * the {@link ElkAxiomProcessor}s using which the axioms are inserted and
	 * deleted
	 */
	private ElkAxiomProcessor axiomInsertionProcessor_,
			axiomDeletionProcessor_;

	/**
	 * the {@link ElkClassExpressionProcessor}s using which the queries are
	 * inserted and deleted
	 */
	private ElkClassExpressionProcessor queryInsertionProcessor_,
			queryDeletionProcessor_;

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
		queryLoader_ = reasoner.getQueryLoader();

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
					axiomInserter, ChangeIndexingProcessor.ADDITION);
			this.axiomDeletionProcessor_ = new ChangeIndexingProcessor(
					axiomDeleter, ChangeIndexingProcessor.REMOVAL);

		}

		if (queryLoader_ != null && !queryLoader_.isLoadingFinished()) {

			queryInsertionProcessor_ = new QueryIndexingProcessor(
					new ElkPolarityExpressionConverterImpl(elkFactory,
							ontologyIndex_, 1),
					QueryIndexingProcessor.ADDITION);
			queryDeletionProcessor_ = new QueryIndexingProcessor(
					new ElkPolarityExpressionConverterImpl(elkFactory,
							ontologyIndex_, -1),
					QueryIndexingProcessor.REMOVAL);

		}

		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		if (loader_ != null && !loader_.isLoadingFinished()) {
			final boolean registered = ontologyIndex_
					.addIndexingUnsupportedListener(
							AXIOM_INDEXING_UNSUPPORTED_LISTENER);
			try {
				loader_.load(axiomInsertionProcessor_, axiomDeletionProcessor_);
			} finally {
				if (registered) {
					ontologyIndex_.removeIndexingUnsupportedListener(
							AXIOM_INDEXING_UNSUPPORTED_LISTENER);
				}
			}
		}
		if (queryLoader_ != null && !queryLoader_.isLoadingFinished()) {
			final boolean registered = ontologyIndex_
					.addIndexingUnsupportedListener(
							QUERY_INDEXING_UNSUPPORTED_LISTENER);
			try {
				queryLoader_.load(queryInsertionProcessor_,
						queryDeletionProcessor_);
			} finally {
				if (registered) {
					ontologyIndex_.removeIndexingUnsupportedListener(
							QUERY_INDEXING_UNSUPPORTED_LISTENER);
				}
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
		if (queryLoader_ != null && !queryLoader_.isLoadingFinished()) {
			throw new ElkRuntimeException("Query loading not finished!");
		}
		this.firstLoad_ = false;
		this.loader_ = null;
		this.queryLoader_ = null;
		this.ontologyIndex_ = null;
		this.axiomInsertionProcessor_ = null;
		this.axiomDeletionProcessor_ = null;
		this.queryInsertionProcessor_ = null;
		this.queryDeletionProcessor_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}

	private static class IndexingUnsupportedListener
			implements ModifiableOntologyIndex.IndexingUnsupportedListener {

		private final ElkObjectVisitor<Void> elkObjectVisitor;
		private final IndexedObject.Visitor<Void> indexedObjectVisitor;

		public IndexingUnsupportedListener(
				final ElkObjectVisitor<Void> elkObjectVisitor,
				final IndexedObject.Visitor<Void> indexedObjectVisitor) {
			this.elkObjectVisitor = elkObjectVisitor;
			this.indexedObjectVisitor = indexedObjectVisitor;
		}

		@Override
		public void indexingUnsupported(
				final ModifiableIndexedObject indexedObject,
				final OccurrenceIncrement increment) {
			indexedObject.accept(indexedObjectVisitor);
		}

		@Override
		public void indexingUnsupported(final ElkObject elkObject) {
			elkObject.accept(elkObjectVisitor);
		}

	}

	private static final ElkObjectVisitor<Void> AXIOM_UNSUPPORTED_ELK_OBJECT_VISITOR_ = new DummyElkObjectVisitor<Void>() {
		
		@Override
		public Void visit(final ElkDataHasValue obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.dataHasValue",
						"ELK supports DataHasValue only partially. Reasoning might be incomplete!");
			}
			return super.visit(obj);
		}

		@Override
		public Void visit(final ElkObjectOneOf obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.objectOneOf",
						"ELK supports ObjectOneOf only partially. Reasoning might be incomplete!");
			}
			return super.visit(obj);
		}
		
		@Override
		public Void visit(final ElkObjectPropertyRangeAxiom obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.objectPropertyRangeAxiom",
						"ELK supports ObjectPropertyRangeAxiom only partially. Reasoning might be incomplete!");
			}
			return super.visit(obj);
		}		

	};

	private static final IndexedObject.Visitor<Void> AXIOM_UNSUPPORTED_INDEXED_OBJECT_VISITOR_ = new DummyIndexedObjectVisitor<Void>() {

		@Override
		public Void visit(final IndexedObjectComplementOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectComplementOf",
						"ELK does not support negative occurrences of ObjectComplementOf. Reasoning might be incomplete!");
			}
			return super.visit(element);
		}

		@Override
		public Void visit(final IndexedObjectUnionOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectUnionOf",
						"ELK does not support positive occurrences of ObjectUnionOf or ObjectOneOf. Reasoning might be incomplete!");
			}
			return super.visit(element);
		}

	};

	private static final ElkObjectVisitor<Void> QUERY_UNSUPPORTED_ELK_OBJECT_VISITOR_ = new DummyElkObjectVisitor<Void>() {

		@Override
		public Void visit(final ElkDataHasValue obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.dataHasValue",
						"ELK supports DataHasValue only partially. Query results may be incomplete!");
			}
			return super.visit(obj);
		}

		@Override
		public Void visit(final ElkObjectOneOf obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.objectOneOf",
						"ELK supports ObjectOneOf only partially. Query results may be incomplete!");
			}
			return super.visit(obj);
		}

	};

	private static final IndexedObject.Visitor<Void> QUERY_UNSUPPORTED_INDEXED_OBJECT_VISITOR_ = new DummyIndexedObjectVisitor<Void>() {

		@Override
		public Void visit(final IndexedObjectComplementOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectComplementOf",
						"ELK does not support querying equivalent classes and subclasses of ObjectComplementOf. Query results may be incomplete!");
			}
			return super.visit(element);
		}

		@Override
		public Void visit(final IndexedObjectUnionOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectUnionOf",
						"ELK does not support querying equivalent classes and superclasses of ObjectUnionOf or ObjectOneOf. Reasoning might be incomplete!");
			}
			return super.visit(element);
		}

	};

	private static final IndexingUnsupportedListener AXIOM_INDEXING_UNSUPPORTED_LISTENER = new IndexingUnsupportedListener(
			AXIOM_UNSUPPORTED_ELK_OBJECT_VISITOR_,
			AXIOM_UNSUPPORTED_INDEXED_OBJECT_VISITOR_);

	private static final IndexingUnsupportedListener QUERY_INDEXING_UNSUPPORTED_LISTENER = new IndexingUnsupportedListener(
			QUERY_UNSUPPORTED_ELK_OBJECT_VISITOR_,
			QUERY_UNSUPPORTED_INDEXED_OBJECT_VISITOR_);

}
