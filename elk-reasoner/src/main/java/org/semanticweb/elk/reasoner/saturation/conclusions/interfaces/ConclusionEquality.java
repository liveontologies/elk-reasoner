package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

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

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;

public class ConclusionEquality implements ConclusionVisitor<Void, Conclusion> {

	private final Object object_;

	private ConclusionEquality(Object object) {
		this.object_ = object;
	}

	public static boolean equals(Conclusion first, Object second) {
		return first == null ? second == null : first.accept(
				new ConclusionEquality(second), null) == second;
	}

	private static boolean equals(IndexedObject first, IndexedObject second) {
		return first == second;
	}

	private static boolean equals(ElkObject first, ElkObject second) {
		return (ElkObjectEquality.equals(first, second));
	}

	@Override
	public BackwardLink visit(BackwardLink subConclusion, Void input) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof BackwardLink) {
			BackwardLink result = (BackwardLink) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot())
					&& equals(result.getOriginRoot(),
							subConclusion.getOriginRoot()))
				return result;
		}
		return null;
	}

	@Override
	public Propagation visit(Propagation subConclusion, Void input) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof Propagation) {
			Propagation result = (Propagation) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot())
					&& equals(result.getCarry(), subConclusion.getCarry()))
				return result;
		}
		return null;
	}

	@Override
	public SubContextInitialization visit(
			SubContextInitialization subConclusion, Void input) {
		if (object_ == subConclusion)
			return subConclusion;
		if (object_ instanceof SubContextInitialization) {
			SubContextInitialization result = (SubContextInitialization) object_;
			if (equals(result.getConclusionRoot(),
					subConclusion.getConclusionRoot())
					&& equals(result.getConclusionSubRoot(),
							subConclusion.getConclusionSubRoot()))
				return result;
		}
		return null;
	}

	@Override
	public ComposedSubsumer visit(ComposedSubsumer conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof ComposedSubsumer) {
			ComposedSubsumer result = (ComposedSubsumer) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getExpression(),
							conclusion.getExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ContextInitialization visit(ContextInitialization conclusion,
			Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof ContextInitialization) {
			ContextInitialization result = (ContextInitialization) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot()))
				return result;
		}
		return null;
	}

	@Override
	public Contradiction visit(Contradiction conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof Contradiction) {
			Contradiction result = (Contradiction) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot()))
				return result;
		}
		return null;
	}

	@Override
	public DecomposedSubsumer visit(DecomposedSubsumer conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof DecomposedSubsumer) {
			DecomposedSubsumer result = (DecomposedSubsumer) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getExpression(),
							conclusion.getExpression()))
				return result;
		}
		return null;
	}

	@Override
	public DisjointSubsumer visit(DisjointSubsumer conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof DisjointSubsumer) {
			DisjointSubsumer result = (DisjointSubsumer) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getAxiom(), conclusion.getAxiom())
					&& equals(result.getMember(), conclusion.getMember())
					&& equals(result.getReason(), conclusion.getReason()))
				return result;
		}
		return null;
	}

	@Override
	public ForwardLink visit(ForwardLink conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof ForwardLink) {
			ForwardLink result = (ForwardLink) object_;
			if (equals(result.getConclusionRoot(),
					conclusion.getConclusionRoot())
					&& equals(result.getForwardChain(),
							conclusion.getForwardChain())
					&& equals(result.getTarget(), conclusion.getTarget()))
				return result;
		}
		return null;
	}

}
