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
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;

/**
 * Represents the inference:
 * 
 * <pre>
 *  DisjointClasses({a0} {a1} ... {an})
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  DifferentIndividuals(a0 a1 ... an)
 * </pre>
 * 
 * @author Peter Skocovsky
 */
public class ElkDifferentIndividualsOfDisjointClasses
		extends AbstractElkInference {

	public final static String NAME = "Different Individuals Introduction";

	private final List<? extends ElkIndividual> different_;

	ElkDifferentIndividualsOfDisjointClasses(
			final List<? extends ElkIndividual> different) {
		this.different_ = different;
	}

	public List<? extends ElkIndividual> getDifferent() {
		return different_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(final int index,
			final ElkObject.Factory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkDisjointClassesAxiom getPremise(final ElkObject.Factory factory) {
		final List<ElkObjectOneOf> disjoint = new ArrayList<ElkObjectOneOf>(
				different_.size());
		for (final ElkIndividual individual : different_) {
			disjoint.add(factory
					.getObjectOneOf(Collections.singletonList(individual)));
		}
		return factory.getDisjointClassesAxiom(disjoint);
	}

	@Override
	public ElkDifferentIndividualsAxiom getConclusion(
			final ElkObject.Factory factory) {
		return factory.getDifferentIndividualsAxiom(different_);
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Peter Skocovsky
	 */
	public interface Factory {

		ElkDifferentIndividualsOfDisjointClasses getElkDifferentIndividualsOfDisjointClasses(
				List<? extends ElkIndividual> different);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkDifferentIndividualsOfDisjointClasses inference);

	}

}
