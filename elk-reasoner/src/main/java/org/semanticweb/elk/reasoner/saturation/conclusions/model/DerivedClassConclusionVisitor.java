package org.semanticweb.elk.reasoner.saturation.conclusions.model;

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

/**
 * A visitor that can be used to enumerate derived {@link ClassConclusion} that
 * explain an entailment of a subsumption. If the visitor methods should return
 * {@code true}, if the next derived conclusion should be visited and
 * {@code false} otherwise.
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface DerivedClassConclusionVisitor {

	/**
	 * @param conclusion
	 *            a {@link ClassInconsistency} with
	 *            {@link ClassInconsistency#getDestination()} =
	 *            {@code owl:Thing}. Since this conclusion is derived, the
	 *            ontology is inconsistent.
	 * @return {@code true} if other conclusions should be visited and
	 *         {@code false} otherwise
	 * @throws ElkException
	 *             if something goes wrong
	 */
	boolean inconsistentOwlThing(ClassInconsistency conclusion)
			throws ElkException;

	/**
	 * @param conclusion
	 *            a {@link ClassInconsistency} with
	 *            {@link ClassInconsistency#getDestination()} corresponding to
	 *            the given inconsistent {@link ElkIndividual}. Since this
	 *            conclusion is derived, the ontology is inconsistent.
	 * @param inconsistent
	 *            the individual that corresponds to
	 *            {@link ClassInconsistency#getDestination()}
	 * @return {@code true} if other conclusions should be visited and
	 *         {@code false} otherwise
	 * @throws ElkException
	 *             if something goes wrong
	 */
	boolean inconsistentIndividual(ClassInconsistency conclusion,
			ElkIndividual inconsistent) throws ElkException;

	/**
	 * @param conclusion
	 *            a {@link ClassInconsistency} with
	 *            {@link ClassInconsistency#getDestination()} corresponding to
	 *            the sub-class of the subsumption for which the entailment is
	 *            explained. Since this conclusion is derived, this sub-class is
	 *            inconsistent.
	 * @return {@code true} if other conclusions should be visited and
	 *         {@code false} otherwise
	 * @throws ElkException
	 *             if something goes wrong
	 */
	boolean inconsistentSubClass(ClassInconsistency conclusion)
			throws ElkException;

	/**
	 * @param conclusion
	 *            a {@link SubClassInclusionComposed} with
	 *            {@link SubClassInclusionComposed#getDestination()}
	 *            corresponding to the sub-class of the subsumption for which
	 *            the entailment is explained, and
	 *            {@link SubClassInclusionComposed#getSubsumer()} corresponding
	 *            to its super-class.
	 * @return {@code true} if other conclusions should be visited and
	 *         {@code false} otherwise
	 * @throws ElkException
	 *             if something goes wrong
	 */
	boolean derivedClassInclusion(SubClassInclusionComposed conclusion)
			throws ElkException;

}
