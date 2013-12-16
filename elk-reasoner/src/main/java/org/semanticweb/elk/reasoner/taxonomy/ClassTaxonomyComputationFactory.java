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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionFactory;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionListener;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputationFactory.Engine;
import org.semanticweb.elk.reasoner.taxonomy.nodes.Node;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that concurrently construct a {@link GenericTaxonomy}
 * . The jobs are submitted using the method {@link Engine#submit(Collection)},
 * which require the computation of the {@link Node} for the input
 * {@link Collection} of {@link IndexedClass}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ClassTaxonomyComputationFactory implements
		InputProcessorFactory<Collection<IndexedClass>, Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassTaxonomyComputationFactory.class);

	/**
	 * The class taxonomy object into which we write the result
	 */
	private final UpdateableTaxonomy<ElkIri, ElkClass> taxonomy_;
	/**
	 * The transitive reduction shared structures used in the taxonomy
	 * construction
	 */
	private final TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>> transitiveReductionShared_;
	/**
	 * The objects creating or update the nodes from the result of the
	 * transitive reduction
	 */
	private final TransitiveReductionOutputProcessor outputProcessor_;

	/**
	 * Create a shared engine for the input ontology index and a partially
	 * pre-computed taxonomy object. The taxonomy is used to avoid computations
	 * that have been made before. For this to work, the taxonomy object must
	 * originate from an earlier run of this engine on the same ontology.
	 * 
	 * @param saturationState
	 *            the saturation state of the reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param partialTaxonomy
	 *            the (partially pre-computed) class taxonomy object to store
	 *            results in
	 */
	public ClassTaxonomyComputationFactory(SaturationState saturationState,
			int maxWorkers, UpdateableTaxonomy<ElkIri, ElkClass> partialTaxonomy) {
		this.taxonomy_ = partialTaxonomy;
		this.transitiveReductionShared_ = new TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>>(
				saturationState, maxWorkers,
				new ThisTransitiveReductionListener());
		this.outputProcessor_ = new TransitiveReductionOutputProcessor();
	}

	/**
	 * Create a new class taxonomy engine for the input ontology index.
	 * 
	 * @param saturationState
	 *            the saturation state of the reasoner
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassTaxonomyComputationFactory(SaturationState saturationState,
			int maxWorkers) {
		this(
				saturationState,
				maxWorkers,
				new ConcurrentTaxonomy<ElkIri, ElkClass>(
						new EntityArray<ElkClass>(
								Collections
										.<ElkClass> singleton(PredefinedElkClass.OWL_THING)),
						getDefaultBottomMembers()));
	}

	/**
	 * @return thread-safe read-write map initialized with bottom members
	 */
	private static Map<ElkIri, ElkClass> getDefaultBottomMembers() {
		// TODO: create a map view of ElkClass set instead
		Map<ElkIri, ElkClass> result = new ConcurrentHashMap<ElkIri, ElkClass>(
				128);
		result.put(PredefinedElkIri.OWL_NOTHING.get(),
				PredefinedElkClass.OWL_NOTHING);
		return result;
	}

	/**
	 * The listener class used for the transitive reduction engine, which is
	 * used within this class taxonomy computation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	private class ThisTransitiveReductionListener implements
			TransitiveReductionListener<TransitiveReductionJob<IndexedClass>> {

		@Override
		public void notifyFinished(TransitiveReductionJob<IndexedClass> job)
				throws InterruptedException {
			job.getOutput().accept(outputProcessor_);
		}

	}

	/**
	 * The class for processing the finished transitive reduction jobs. It
	 * implements the visitor pattern for
	 * {@link TransitiveReductionOutputVisitor}.
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	private class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedClass> {

		private final Transformation<TransitiveReductionOutputEquivalent<IndexedClass>, Map<ElkIri, ElkClass>> transform_ = new Transformation<TransitiveReductionOutputEquivalent<IndexedClass>, Map<ElkIri, ElkClass>>() {
			@Override
			public Map<ElkIri, ElkClass> transform(
					TransitiveReductionOutputEquivalent<IndexedClass> element) {
				return new EntityArray<ElkClass>(element.getEquivalent());
			}
		};

		@Override
		public void visit(
				TransitiveReductionOutputEquivalentDirect<IndexedClass> output) {
			Iterable<Map<ElkIri, ElkClass>> directSubsumersIterator = Operations
					.map(output.getDirectSubsumers(), transform_);
			taxonomy_.setDirectRelations(
					new EntityArray<ElkClass>(output.getEquivalent()),
					directSubsumersIterator);
		}

		@Override
		public void visit(
				TransitiveReductionOutputUnsatisfiable<IndexedClass> output) {
			ElkClass unsatisfiable = output.getRoot().getElkClass();
			taxonomy_.addBottomMember(unsatisfiable.getIri(), unsatisfiable);
		}

		@Override
		public void visit(
				TransitiveReductionOutputEquivalent<IndexedClass> output) {
			// this should not happen: all transitive reduction results should
			// be computed with direct super classes
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Returns the taxonomy constructed by this engine
	 * 
	 * @return the taxonomy constructed by this engine
	 */
	public UpdateableTaxonomy<ElkIri, ElkClass> getTaxonomy() {
		return this.taxonomy_;
	}

	@Override
	public Engine getEngine() {
		return new Engine();

	}

	@Override
	public void finish() {
		transitiveReductionShared_.finish();
	}

	/**
	 * Print statistics about taxonomy construction
	 */
	public void printStatistics() {
		transitiveReductionShared_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return transitiveReductionShared_.getRuleAndConclusionStatistics();
	}

	/**
	 * 
	 */
	public class Engine implements InputProcessor<Collection<IndexedClass>> {

		/**
		 * The transitive reduction engine used in the taxonomy construction
		 */
		protected final TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>>.Engine transitiveReductionEngine = transitiveReductionShared_
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(Collection<IndexedClass> input) {
			for (IndexedClass ic : input) {
				LOGGER_.trace("{}: taxonomy construction started", ic);

				transitiveReductionEngine
						.submit(new TransitiveReductionJob<IndexedClass>(ic));
			}
		}

		@Override
		public final void process() throws InterruptedException {
			transitiveReductionEngine.process();
		}

		@Override
		public void finish() {
			transitiveReductionEngine.finish();
		}

	}

}
