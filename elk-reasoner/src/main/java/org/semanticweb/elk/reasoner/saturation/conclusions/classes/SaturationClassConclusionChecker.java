package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Checks if adding or removing a {@link ClassConclusion} can result in changing
 * the saturation of the {@link Context} for its
 * {@link ClassConclusion#getTraceRoot()}. For this to happen, the added or
 * removed {@link ClassConclusion} must contain only {@link IndexedSubObject}s
 * that occur in the ontology (with the proper polarity) and
 * {@link SubClassConclusion#getTraceSubRoot()} must be {@code null}.
 * 
 * @see Context#isSaturated()
 * 
 * @author Yevgeny Kazakov
 *
 */
public class SaturationClassConclusionChecker
		extends DerivedClassConclusionChecker {

	private static final ClassConclusion.Visitor<Boolean> INSTANCE_ = new SaturationClassConclusionChecker();

	/**
	 * Checks if adding or removing the given {@link ClassConclusion} can result
	 * in changing the saturation of the {@link Context} for its
	 * {@link ClassConclusion#getTraceRoot()}.
	 * 
	 * @param conclusion
	 *            the {@link ClassConclusion} for which the check is performed
	 * @return {@code true} if the {@link Context} for
	 *         {@link ClassConclusion#getTraceRoot()} may become unsaturated,
	 *         and {@code false} if the {@link Context} remains saturated. The
	 *         latter happens, for example, if the given {@link ClassConclusion}
	 *         contains {@link IndexedSubObject}s that no longer occur in the
	 *         ontology (with the required polarity).
	 * 
	 * @see Context#isSaturated()
	 */
	public static boolean check(ClassConclusion conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	@Override
	protected boolean defaultVisit(SubClassConclusion conclusion) {
		return conclusion.getTraceSubRoot() == null
				&& super.defaultVisit(conclusion);
	}

}
