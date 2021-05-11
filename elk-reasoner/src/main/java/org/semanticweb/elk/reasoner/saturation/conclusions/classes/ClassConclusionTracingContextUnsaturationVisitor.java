package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedContextRootVisitor;
import org.semanticweb.elk.reasoner.indexing.model.HasNegativeOccurrence;
import org.semanticweb.elk.reasoner.indexing.model.HasPositiveOccurrence;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that marks the {@link Context} for the root
 * returned by {@link ClassConclusion#getTraceRoot()} for the visited
 * {@link ClassConclusion}s as not saturated if the {@link ClassConclusion} can
 * potentially be re-derived. The visit method always returns {@code true}.
 * 
 * @see ClassConclusion#getTraceRoot()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassConclusionTracingContextUnsaturationVisitor
		implements ClassConclusion.Visitor<Boolean> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionTracingContextUnsaturationVisitor.class);
 
	
	private final SaturationStateWriter<?> writer_;

	public ClassConclusionTracingContextUnsaturationVisitor(
			SaturationStateWriter<?> writer) {
		this.writer_ = writer;
	}

	private final static IndexedContextRoot.Visitor<Boolean> ROOT_OCCURRENCE_CHECKER_ = new DummyIndexedContextRootVisitor<Boolean>() {

		@Override
		protected Boolean defaultVisit(IndexedClassExpression element) {
			return occursPositively(element);
		}

		@Override
		public Boolean visit(IndexedRangeFiller element) {
			return occursPositively(element.getFiller())
					&& occurs(element.getProperty());
		}

	};

	private static boolean occurs(IndexedSubObject iobj) {
		return iobj.occurs();
	}

	private static boolean occurs(IndexedClassExpression iobj) {
		return iobj.occurs();
	}

	private static boolean occurs(IndexedContextRoot root) {
		return root.accept(ROOT_OCCURRENCE_CHECKER_);
	}

	private static boolean occursNegatively(IndexedComplexClassExpression ice) {
		return ice.occursNegatively();
	}

	private static boolean occursNegatively(IndexedSubObject obj) {
		if (obj instanceof HasNegativeOccurrence) {
			return ((HasNegativeOccurrence) obj).occursNegatively();
		}
		// else
		return occurs(obj);
	}

	private static boolean occursPositively(IndexedSubObject obj) {
		if (obj instanceof HasPositiveOccurrence) {
			return ((HasPositiveOccurrence) obj).occursPositively();
		}
		// else
		return occurs(obj);
	}

	private boolean log(ClassConclusion conclusion) {
		LOGGER_.trace("Conclusion could be rederived: {}", conclusion);
		return true;
	}

	protected boolean defaultVisit(ClassConclusion conclusion) {
		return !occurs(conclusion.getDestination())
				|| !occurs(conclusion.getTraceRoot())
				|| writer_.markAsNotSaturated(conclusion.getTraceRoot())
				|| log(conclusion) || true;
	}

	protected boolean defaultVisit(SubClassConclusion conclusion) {
		return !occurs(conclusion.getSubDestination())
				|| !occurs(conclusion.getTraceRoot())
				|| defaultVisit((ClassConclusion) conclusion);
	}

	@Override
	public Boolean visit(BackwardLink conclusion) {
		return !occurs(conclusion.getRelation())
				|| !occurs(conclusion.getSource()) || defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(ClassInconsistency conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(ContextInitialization conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion) {
		return !occurs(conclusion.getDisjointExpressions())
				|| defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(ForwardLink conclusion) {
		return !occurs(conclusion.getChain()) || !occurs(conclusion.getTarget())
				|| defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(Propagation subConclusion) {
		return !occurs(subConclusion.getRelation())
				|| !occursNegatively(subConclusion.getCarry())
				|| defaultVisit(subConclusion);
	}

	@Override
	public Boolean visit(SubClassInclusionComposed conclusion) {
		return !occursNegatively(conclusion.getSubsumer())
				|| defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposed conclusion) {
		return !occurs(conclusion.getSubsumer()) || defaultVisit(conclusion);
	}

	@Override
	public Boolean visit(SubContextInitialization conclusion) {
		return defaultVisit(conclusion);
	}

}
