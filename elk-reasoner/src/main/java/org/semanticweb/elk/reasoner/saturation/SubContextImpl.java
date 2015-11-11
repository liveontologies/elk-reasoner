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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class SubContextImpl extends ArrayHashSet<IndexedContextRoot> implements
		SubContext {

	private static final SubClassConclusion.Visitor<SubContextImpl, Boolean> SUB_CONCLUSION_INSERTER_ = new SubConclusionInserter();
	private static final SubClassConclusion.Visitor<SubContextImpl, Boolean> SUB_CONCLUSION_DELETOR_ = new SubConclusionDeletor();
	private static final SubClassConclusion.Visitor<SubContextImpl, Boolean> SUB_CONCLUSION_OCCURRENCE_CHECKER_ = new SubConclusionOccurrenceChecker();

	Set<IndexedObjectSomeValuesFrom> propagatedSubsumers_;

	/**
	 * {@code true} if this {@link SubContext} was initialized
	 */
	private boolean isInitialized_ = false;

	/**
	 * the number of {@link SubClassConclusion}s contained in this {@link SubContext}
	 */
	private int size_ = 0;

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
	public boolean addSubConclusion(SubClassConclusion conclusion) {
		boolean success = conclusion.accept(SUB_CONCLUSION_INSERTER_, this);
		if (success)
			size_++;
		return success;
	}

	@Override
	public boolean removeSubConclusion(SubClassConclusion conclusion) {
		boolean success = conclusion.accept(SUB_CONCLUSION_DELETOR_, this);
		if (success)
			size_--;
		return success;
	}

	@Override
	public boolean containsSubConclusion(SubClassConclusion conclusion) {
		return conclusion.accept(SUB_CONCLUSION_OCCURRENCE_CHECKER_, this);
	}

	@Override
	public boolean isInitialized() {
		return isInitialized_;
	}

	@Override
	public boolean isEmpty() {
		return size_ == 0;
	}

	public static class SubConclusionInserter implements
			SubClassConclusion.Visitor<SubContextImpl, Boolean> {

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
			SubClassConclusion.Visitor<SubContextImpl, Boolean> {

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
			SubClassConclusion.Visitor<SubContextImpl, Boolean> {

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
