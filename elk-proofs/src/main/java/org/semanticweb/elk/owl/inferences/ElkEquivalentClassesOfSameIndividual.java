package org.semanticweb.elk.owl.inferences;

import java.util.ArrayList;
import java.util.Collections;

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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *      SameIndividual(a0 a1 ... an)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  EquivalentClasses({a0} {a1} ... {an})
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkEquivalentClassesOfSameIndividual extends AbstractElkInference {

	private final static String NAME_ = "Same Individual Translation";

	private final List<? extends ElkIndividual> same_;

	ElkEquivalentClassesOfSameIndividual(List<? extends ElkIndividual> same) {
		this.same_ = same;
	}

	public List<? extends ElkIndividual> getSame() {
		return same_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObject.Factory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkSameIndividualAxiom getPremise(ElkObject.Factory factory) {
		return factory.getSameIndividualAxiom(same_);
	}

	@Override
	public ElkEquivalentClassesAxiom getConclusion(ElkObject.Factory factory) {
		List<ElkObjectOneOf> equivalent = new ArrayList<ElkObjectOneOf>(
				same_.size());
		for (ElkIndividual individual : same_) {
			equivalent.add(factory
					.getObjectOneOf(Collections.singletonList(individual)));
		}
		return factory.getEquivalentClassesAxiom(equivalent);
	}

	@Override
	public ElkInference getExample() {
		return new ElkEquivalentClassesOfSameIndividual(
				getIndividuals("a", same_.size()));
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkEquivalentClassesOfSameIndividual getElkEquivalentClassesOfSameIndividual(
				List<? extends ElkIndividual> same);

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

		O visit(ElkEquivalentClassesOfSameIndividual inference);

	}

}
