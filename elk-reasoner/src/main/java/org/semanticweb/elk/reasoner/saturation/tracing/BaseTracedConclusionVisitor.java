/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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



/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseTracedConclusionVisitor<R, C> implements TracedConclusionVisitor<R, C> {

	protected R defaultTracedVisit(TracedConclusion conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(InitializationSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(SubClassOfSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ComposedConjunction conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(DecomposedConjunction conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(PropagatedSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ReflexiveSubsumer conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ComposedBackwardLink conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(ReversedBackwardLink conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(DecomposedExistential conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}

	@Override
	public R visit(TracedPropagation conclusion, C parameter) {
		return defaultTracedVisit(conclusion, parameter);
	}
}
