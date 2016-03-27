package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Proofs Package
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

public class ForwardLinkMatch2
		extends
			AbstractClassConclusionMatch<ForwardLinkMatch1> {

	private final IndexedContextRootMatchChain intermediateRoots_;
	
	private final IndexedContextRootMatch targetMatch_;

	ForwardLinkMatch2(ForwardLinkMatch1 parent,
			IndexedContextRootMatchChain intermediateRoots,
			IndexedContextRootMatch targetMatch) {
		super(parent);
		this.intermediateRoots_ = intermediateRoots;
		this.targetMatch_ = targetMatch;
	}
	
	public IndexedContextRootMatchChain getIntermediateRoots() {
		return intermediateRoots_;
	}

	public IndexedContextRootMatch getTargetMatch() {
		return targetMatch_;
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkMatch2 getForwardLinkMatch2(ForwardLinkMatch1 parent,
				IndexedContextRootMatchChain intermediateRoots,
				IndexedContextRootMatch targetMatch);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ForwardLinkMatch2 conclusionMatch);

	}

}