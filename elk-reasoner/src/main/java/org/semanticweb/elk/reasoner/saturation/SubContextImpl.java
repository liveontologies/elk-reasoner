package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class SubContextImpl extends ArrayHashSet<IndexedContextRoot> implements
		SubContext {

	private static final SubConclusionVisitor<SubContextImpl, Boolean> SUB_CONCLUSION_INSERTER_ = new SubConclusionInserter();
	private static final SubConclusionVisitor<SubContextImpl, Boolean> SUB_CONCLUSION_DELETOR_ = new SubConclusionDeletor();
	private static final SubConclusionVisitor<SubContextImpl, Boolean> SUB_CONCLUSION_OCCURRENCE_CHECKER_ = new SubConclusionOccurrenceChecker();

	Set<IndexedObjectSomeValuesFrom> propagatedSubsumers_;

	/**
	 * {@code true} if this {@link SubContext} was initialized
	 */
	boolean isInitialized_ = false;

	public SubContextImpl() {
		// represents the set of roots linked by the stored backward links
		super(3);
	}

	@Override
	public Set<IndexedContextRoot> getLinkedRoots() {
		return this;
	}

	@Override
	public Set<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers() {
		if (propagatedSubsumers_ == null)
			return Collections.emptySet();
		// else
		return propagatedSubsumers_;
	}

	@Override
	public boolean addSubConclusion(SubConclusion conclusion) {
		return conclusion.accept(SUB_CONCLUSION_INSERTER_, this);
	}

	@Override
	public boolean removeSubConclusion(SubConclusion conclusion) {
		return conclusion.accept(SUB_CONCLUSION_DELETOR_, this);
	}

	@Override
	public boolean containsSubConclusion(SubConclusion conclusion) {
		return conclusion.accept(SUB_CONCLUSION_OCCURRENCE_CHECKER_, this);
	}

	@Override
	public boolean isInitialized() {
		return isInitialized_;
	}

	public static class SubConclusionInserter implements
			SubConclusionVisitor<SubContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, SubContextImpl input) {
			return input.add(subConclusion.getOriginRoot());
		}

		@Override
		public Boolean visit(Propagation subConclusion, SubContextImpl input) {
			if (input.propagatedSubsumers_ == null)
				input.propagatedSubsumers_ = new ArrayHashSet<IndexedObjectSomeValuesFrom>(
						3);
			return input.propagatedSubsumers_.add(subConclusion.getCarry());
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				SubContextImpl input) {
			if (input.isInitialized_)
				// already initialized
				return false;
			// else
			input.isInitialized_ = true;
			return true;
		}
	}

	public static class SubConclusionDeletor implements
			SubConclusionVisitor<SubContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, SubContextImpl input) {
			return input.remove(subConclusion.getOriginRoot());
		}

		@Override
		public Boolean visit(Propagation subConclusion, SubContextImpl input) {
			if (input.propagatedSubsumers_ == null)
				return false;
			// else
			return input.propagatedSubsumers_.remove(subConclusion.getCarry());
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				SubContextImpl input) {
			if (!input.isInitialized_)
				// already not initialized
				return false;
			// else
			input.isInitialized_ = false;
			return true;
		}
	}

	public static class SubConclusionOccurrenceChecker implements
			SubConclusionVisitor<SubContextImpl, Boolean> {

		@Override
		public Boolean visit(BackwardLink subConclusion, SubContextImpl input) {
			return input.contains(subConclusion.getOriginRoot());
		}

		@Override
		public Boolean visit(Propagation subConclusion, SubContextImpl input) {
			if (input.propagatedSubsumers_ == null)
				return false;
			// else
			return input.propagatedSubsumers_
					.contains(subConclusion.getCarry());
		}

		@Override
		public Boolean visit(SubContextInitialization subConclusion,
				SubContextImpl input) {
			return input.isInitialized_;
		}
	}

}
