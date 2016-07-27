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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.indexing.classes.BaseModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.query.QueryNode;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Keeps track of class expressions that were queried for satisfiability,
 * equivalent classes, super-classes or sub-classes.
 * 
 * @author Peter Skocovsky
 */
public class ClassExpressionQueryState {

	/**
	 * Maps class expressions that were queried and added to the index to the
	 * result of indexing.
	 */
	private final Map<ElkClassExpression, IndexedClassExpression> queried_ = new ConcurrentHashMap<ElkClassExpression, IndexedClassExpression>();

	/**
	 * Contains indexed class expressions that were queried and added to the
	 * index, but not saturated.
	 */
	private final Set<IndexedClassExpression> notSaturated_ = Collections
			.newSetFromMap(
					new ConcurrentHashMap<IndexedClassExpression, Boolean>());

	/**
	 * Contains indexed class expressions that were queried, added to the index
	 * and saturated.
	 */
	private final Set<IndexedClassExpression> saturated_ = Collections
			.newSetFromMap(
					new ConcurrentHashMap<IndexedClassExpression, Boolean>());

	/**
	 * Maps indexed class expressions that were queried, added to the index,
	 * saturated and are satisfiable to the nodes with their equivalent classes.
	 */
	private final Map<IndexedClassExpression, QueryNode<ElkClass>> satisfiable_ = new ConcurrentHashMap<IndexedClassExpression, QueryNode<ElkClass>>();

	private final SaturationState<? extends Context> saturationState_;

	private final ElkPolarityExpressionConverter updatingExpressionConverter_;

	private final ElkPolarityExpressionConverter resolvingExpressionConverter_;

	private final ClassInconsistency.Factory conclusionFactory_;

	public <C extends Context> ClassExpressionQueryState(
			final SaturationState<C> saturationState,
			final PredefinedElkClassFactory elkFactory,
			final ModifiableOntologyIndex ontologyIndex,
			final ClassInconsistency.Factory conclusionFactory) {
		this.saturationState_ = saturationState;
		this.updatingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory,
				new UpdatingModifiableIndexedObjectFactory(
						new BaseModifiableIndexedObjectFactory(), ontologyIndex,
						OccurrenceIncrement.getDualIncrement(1)));
		this.resolvingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory, ontologyIndex);
		this.conclusionFactory_ = conclusionFactory;
		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedClassExpression) {
							final IndexedClassExpression ice = (IndexedClassExpression) root;
							/*
							 * Saturation and "un-saturation" should not happen
							 * on the same time, so this should be thread-safe.
							 */
							if (saturated_.remove(ice)) {
								notSaturated_.add(ice);
								satisfiable_.remove(ice);
							}
						}
					}

					@Override
					public void contextsClear() {
						// TODO: does this need to be more thread-safe ??
						notSaturated_.addAll(saturated_);
						saturated_.clear();
						satisfiable_.clear();
					}

				});
	}

	/**
	 * Registers the supplied class expression for querying. Methods
	 * {@link #isSatisfiable(ElkClassExpression)}, ... will always return
	 * <code>null</code> for class expressions that were not registered.
	 * 
	 * @param classExpression
	 */
	void loadQuery(final ElkClassExpression classExpression) {
		/* @formatter:off
		 * 
		 * If it was already queried, do nothing. Otherwise:
		 * index it by the converter
		 * put it into the sets according to whether it is saturated
		 * 
		 * @formatter:on
		 */

		final IndexedClassExpression ice;
		synchronized (queried_) {
			if (queried_.containsKey(classExpression)) {
				return;
			}
			ice = classExpression.accept(updatingExpressionConverter_);
			queried_.put(classExpression, ice);
		}

		final Context context = saturationState_.getContext(ice);
		if (context != null && context.isInitialized() && context.isSaturated()
				&& context.containsConclusion(
						conclusionFactory_.getContradiction(ice))) {
			saturated_.add(ice);
		} else {
			notSaturated_.add(ice);
		}

	}

	private final TransitiveReductionOutputVisitor<IndexedClassExpression> transitiveReductionOutputProcessor_ = new TransitiveReductionOutputVisitor<IndexedClassExpression>() {

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalentDirect<IndexedClassExpression> output) {

			final IndexedClassExpression ice = output.getRoot();
			/*
			 * Saturation and "un-saturation" should not happen at the same
			 * time, so this should be thread-safe.
			 */
			if (!notSaturated_.remove(ice)) {
				return;
			}
			saturated_.add(ice);

			final List<ElkClass> equivalent = output.getEquivalent();
			final Collection<? extends List<ElkClass>> directSubsumers = output
					.getDirectSubsumers();

			final QueryNode<ElkClass> node = new QueryNode<ElkClass>(equivalent,
					equivalent.size(), ElkClassKeyProvider.INSTANCE);

			for (final List<ElkClass> directSubs : directSubsumers) {
				// direct subsumers should not be empty
				final QueryNode<ElkClass> directSuperNode = new QueryNode<ElkClass>(
						directSubs, directSubs.size(),
						ElkClassKeyProvider.INSTANCE);
				node.addDirectSuperNode(directSuperNode);
			}

			satisfiable_.put(ice, node);
		}

		@Override
		public void visit(
				final TransitiveReductionOutputUnsatisfiable<IndexedClassExpression> output) {

			final IndexedClassExpression ice = output.getRoot();
			/*
			 * Saturation and "un-saturation" should not happen at the same
			 * time, so this should be thread-safe.
			 */
			if (!notSaturated_.remove(ice)) {
				return;
			}
			saturated_.add(ice);
		}

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalent<IndexedClassExpression> output) {
			// transitive reduction computation should not produce these
			throw new IllegalArgumentException(
					"Unexpected output of transitive reduction!");
		}

	};

	/**
	 * @return processor of output of transitive reduction of the queried class
	 *         expressions.
	 */
	TransitiveReductionOutputVisitor<IndexedClassExpression> getTransitiveReductionOutputProcessor() {
		return transitiveReductionOutputProcessor_;
	}

	/**
	 * @return indexed class expressions that were queried and added to the
	 *         index, but not saturated.
	 */
	public Collection<IndexedClassExpression> getNotSaturatedQueriedClassExpressions() {
		return notSaturated_;
	}

	/**
	 * Checks whether the supplied class expression is satisfiable, if the
	 * result was already computed. If the class expression was not registered
	 * by {@link #loadQuery(ElkClassExpression)} or the appropriate stage was
	 * not completed yet, returns <code>null</code>.
	 * 
	 * @param classExpression
	 * @return <code>null</code> if the result is not ready, otherwise whether
	 *         the supplied class expression is satisfiable.
	 */
	Boolean isSatisfiable(final ElkClassExpression classExpression) {

		final IndexedClassExpression ice = queried_.get(classExpression);

		if (ice != null && saturated_.contains(ice)) {
			return satisfiable_.containsKey(ice);
		}

		return null;
	}

	/**
	 * Returns {@link Node} containing all {@link ElkClass}es equivalent to the
	 * supplied class expression, if the result was already computed. If the
	 * class expression was not registered by
	 * {@link #loadQuery(ElkClassExpression)} or the appropriate stage was not
	 * completed yet, returns <code>null</code>.
	 * 
	 * @param classExpression
	 * @return <code>null</code> if the result is not ready, otherwise atomic
	 *         classes equivalent to the supplied class expression.
	 */
	Node<ElkClass> getEquivalentClasses(
			final ElkClassExpression classExpression) {

		final IndexedClassExpression ice = queried_.get(classExpression);

		if (ice != null && saturated_.contains(ice)) {
			return satisfiable_.get(ice);
		}

		return null;
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict super-classes of the supplied class expression, if the
	 * result was already computed. If the class expression was not registered
	 * by {@link #loadQuery(ElkClassExpression)} or the appropriate stage was
	 * not completed yet, returns <code>null</code>.
	 * 
	 * @param classExpression
	 * @return <code>null</code> if the result is not ready, otherwise atomic
	 *         direct strict super-classes of the supplied class expression.
	 */
	Set<? extends Node<ElkClass>> getDirectSuperClasses(
			final ElkClassExpression classExpression) {

		final IndexedClassExpression ice = queried_.get(classExpression);

		if (ice != null && saturated_.contains(ice)) {
			final QueryNode<ElkClass> node = satisfiable_.get(ice);
			return node.getDirectSuperNodes();
		}

		return null;
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict sub-classes of the supplied class expression, if the result
	 * was already computed. If the class expression was not registered by
	 * {@link #loadQuery(ElkClassExpression)} or the appropriate stage was not
	 * completed yet, returns <code>null</code>.
	 * 
	 * @param classExpression
	 * @param taxonomy
	 * @return <code>null</code> if the result is not ready, otherwise atomic
	 *         direct strict sub-classes of the supplied class expression.
	 */
	Set<? extends Node<ElkClass>> getDirectSubClasses(
			final ElkClassExpression classExpression,
			final Taxonomy<ElkClass> taxonomy) {

		final IndexedClassExpression ice = queried_.get(classExpression);

		if (ice == null || !saturated_.contains(ice)) {
			return null;
		}
		// else

		final QueryNode<ElkClass> node = satisfiable_.get(ice);

		final Iterator<ElkClass> iter = node.iterator();
		if (iter.hasNext()) {
			final ElkClass cls = iter.next();
			return taxonomy.getNode(cls).getDirectSubNodes();
		}
		/*
		 * Else, if classExpression is not equivalent to any atomic class,
		 * direct atomic sub-classes of classExpression are atomic classes that
		 * have classExpression among their subsumers, but no other of their
		 * strict subsumers have classExpression among its subsumers.
		 * 
		 * The search can be limited to all sub-classes of direct super-classes
		 * of classExpression.
		 */

		final Set<TaxonomyNode<ElkClass>> result = new ArrayHashSet<TaxonomyNode<ElkClass>>();

		final Queue<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();
		final Set<TaxonomyNode<ElkClass>> done = new ArrayHashSet<TaxonomyNode<ElkClass>>();

		for (final Node<ElkClass> superNode : node.getDirectSuperNodes()) {
			final Set<? extends TaxonomyNode<ElkClass>> subNodes = taxonomy
					.getNode(superNode.getCanonicalMember())
					.getDirectSubNodes();
			for (final TaxonomyNode<ElkClass> subNode : subNodes) {
				if (done.add(subNode)) {
					toDo.add(subNode);
				}
			}
		}

		while (!toDo.isEmpty()) {
			final TaxonomyNode<ElkClass> subNode = toDo.poll();

			// test direct subsumption

			final IndexedClassExpression canonical = subNode
					.getCanonicalMember().accept(resolvingExpressionConverter_);

			boolean isDirect = true;
			boolean isSubClass = false;
			for (final IndexedClassExpression subsumer : saturationState_
					.getContext(canonical).getComposedSubsumers()) {
				if (subsumer == ice) {
					isSubClass = true;
				} else {
					final Context context = saturationState_
							.getContext(subsumer);
					if (context != null) {
						final Set<IndexedClassExpression> subsumers = context
								.getComposedSubsumers();
						if (!subsumers.contains(canonical)
								&& subsumers.contains(ice)) {
							// not direct
							isDirect = false;
							break;
						}
					}
				}
			}

			if (isDirect && isSubClass) {
				result.add(subNode);
			}

			// queue up sub-nodes

			for (final TaxonomyNode<ElkClass> subSubNode : subNode
					.getDirectSubNodes()) {
				if (done.add(subSubNode)) {
					toDo.add(subSubNode);
				}
			}

		}

		return Collections.unmodifiableSet(result);
	}

}
