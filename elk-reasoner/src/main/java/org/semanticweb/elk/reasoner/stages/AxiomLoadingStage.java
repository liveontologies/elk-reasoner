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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.incremental.AxiomLoadingListener;
import org.semanticweb.elk.reasoner.indexing.classes.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.classes.NonIncrementalElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
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
 * A {@link ReasonerStage} during which the input ontology is loaded into the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class AxiomLoadingStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AxiomLoadingStage.class);

	/**
	 * the {@link AxiomLoader} using which the axioms are loaded
	 */
	private volatile AxiomLoader loader_;
	
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
	 * the ontology index where the loaded and indexed axioms are stored.
	 */
	private ModifiableOntologyIndex ontologyIndex_;

	/**
	 * the listener notified about expressions whose indexing is unsupported
	 */
	private IndexingUnsupportedListener indexingUnsupportedListener_;

	/**
	 * whether {@link #indexingUnsupportedListener_} was successfully registered
	 */
	private boolean isIndexingUnsupportedListenerRegistered_;

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
		isIndexingUnsupportedListenerRegistered_ = false;
		if (!super.preExecute())
			return false;
		if (!firstLoad_) {
			reasoner.trySetIncrementalMode();			
		}
		loader_ = reasoner.getAxiomLoader();
		if (loader_ == null || loader_.isLoadingFinished()) {
			return true;
		}

		ontologyIndex_ = reasoner.getModifiableOntologyIndex();
		ElkObject.Factory elkFactory = reasoner.getElkFactory();

		indexingUnsupportedListener_ = new IndexingUnsupportedListener();
		isIndexingUnsupportedListenerRegistered_ = ontologyIndex_
				.addIndexingUnsupportedListener(indexingUnsupportedListener_);

		ElkAxiomConverter axiomInserter = new ElkAxiomConverterImpl(elkFactory,
				ontologyIndex_, 1);
		ElkAxiomConverter axiomDeleter = new ElkAxiomConverterImpl(elkFactory,
				ontologyIndex_, -1);

		/*
		 * wrapping both the inserter and the deleter to receive notifications
		 * if some axiom change can't be incorporated incrementally
		 */
		/**
		 * The listener used to count axioms and detect if the axiom cannot be
		 * loaded incrementally
		 */
		AxiomLoadingListener<ElkAxiom> listener = new AxiomLoadingListener<ElkAxiom>() {

			boolean resetDone = false;

			@Override
			public void notify(ElkAxiom axiom) {
				if (resetDone)
					return;
				LOGGER_.debug("{}: axiom not supported in incremental mode", axiom);
				reasoner.resetPropertySaturation();
				reasoner.setNonIncrementalMode();
				resetDone = true;
			}
		};

		axiomInserter = new NonIncrementalElkAxiomVisitor(axiomInserter,
				listener);
		axiomDeleter = new NonIncrementalElkAxiomVisitor(axiomDeleter, listener);

		this.axiomInsertionProcessor_ = new ChangeIndexingProcessor(
				axiomInserter, ChangeIndexingProcessor.ADDITION);
		this.axiomDeletionProcessor_ = new ChangeIndexingProcessor(
				axiomDeleter, ChangeIndexingProcessor.REMOVAL);
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		loader_.load(axiomInsertionProcessor_, axiomDeletionProcessor_);
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;	
		if (!loader_.isLoadingFinished()) {
			throw new ElkRuntimeException("Loading not finished!");
		}
		if (isIndexingUnsupportedListenerRegistered_) {
			ontologyIndex_.removeIndexingUnsupportedListener(
					indexingUnsupportedListener_);
			indexingUnsupportedListener_.processEvents();
		}
		this.firstLoad_ = false;
		this.loader_ = null;
		this.ontologyIndex_ = null;
		this.indexingUnsupportedListener_ = null;
		this.axiomInsertionProcessor_ = null;
		this.axiomDeletionProcessor_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}
	
	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(loader_, flag);
	}

	private static class IndexingUnsupportedListener
			implements ModifiableOntologyIndex.IndexingUnsupportedListener {

		private final Queue<ModifiableIndexedObject> unsupportedIndexed_ = new ConcurrentLinkedQueue<ModifiableIndexedObject>();
		private final Queue<ElkObject> unsupportedElk_ = new ConcurrentLinkedQueue<ElkObject>();

		@Override
		public void indexingUnsupported(
				final ModifiableIndexedObject indexedObject,
				final OccurrenceIncrement increment) {
			unsupportedIndexed_.add(indexedObject);
		}

		@Override
		public void indexingUnsupported(final ElkObject elkObject) {
			unsupportedElk_.add(elkObject);
		}

		public void processEvents() {

			ModifiableIndexedObject indexedObject;
			while ((indexedObject = unsupportedIndexed_.poll()) != null) {
				indexedObject
						.accept(INDEXING_UNSUPPORTED_INDEXED_OBJECT_VISITOR_);
			}

			ElkObject elkObject;
			while ((elkObject = unsupportedElk_.poll()) != null) {
				elkObject.accept(INDEXING_UNSUPPORTED_ELK_OBJECT_VISITOR_);
			}

		}

	}

	private static final ElkObjectVisitor<Void> INDEXING_UNSUPPORTED_ELK_OBJECT_VISITOR_ = new DummyElkObjectVisitor<Void>() {

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

	};

	private static final IndexedObject.Visitor<Void> INDEXING_UNSUPPORTED_INDEXED_OBJECT_VISITOR_ = new DummyIndexedObjectVisitor<Void>() {

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

}
