package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DerivedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link DerivedClassConclusionVisitor} that stops at the first conclusion
 * and does nothing. Can be used as a prototype for other visitors by overriding
 * the default visit method.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class DerivedClassConclusionDummyVisitor
		implements DerivedClassConclusionVisitor {

	/**
	 * The default visitor method for conclusions explaining a subsumption
	 * 
	 * @param conclusion
	 * @return {@code true} if other conclusions should be visited and
	 *         {@code false} otherwise
	 * 
	 */
	@SuppressWarnings("static-method")
	protected boolean defaultVisit(ClassConclusion conclusion) {
		return false;
	}

	@Override
	public boolean inconsistentOwlThing(ClassInconsistency conclusion)
			throws ElkException {
		return defaultVisit(conclusion);
	}

	@Override
	public boolean inconsistentIndividual(ClassInconsistency conclusion,
			ElkIndividual inconsistent) throws ElkException {
		return defaultVisit(conclusion);
	}

	@Override
	public boolean inconsistentSubClass(ClassInconsistency conclusion)
			throws ElkException {
		return defaultVisit(conclusion);
	}

	@Override
	public boolean derivedClassInclusion(SubClassInclusionComposed conclusion)
			throws ElkException {
		return defaultVisit(conclusion);
	}

}
