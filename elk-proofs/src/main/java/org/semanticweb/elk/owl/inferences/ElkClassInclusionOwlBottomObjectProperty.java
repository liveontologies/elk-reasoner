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
package org.semanticweb.elk.owl.inferences;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * ∃ owl:bottomObjectProperty . ⊤ ⊑ ⊥
 * </pre>
 * 
 * @author Peter Skocovsky
 */
public class ElkClassInclusionOwlBottomObjectProperty
		extends AbstractElkInference {

	static final ElkClassInclusionOwlBottomObjectProperty INSTANCE = new ElkClassInclusionOwlBottomObjectProperty();

	private final static String NAME_ = "Bottom Object Property Tautology";

	private ElkClassInclusionOwlBottomObjectProperty() {
		// Empty.
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return 0;
	}

	@Override
	public ElkSubClassOfAxiom getPremise(final int index,
			final ElkObject.Factory factory) {
		return failGetPremise(index);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(final ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(factory.getObjectSomeValuesFrom(
				factory.getOwlBottomObjectProperty(), factory.getOwlThing()),
				factory.getOwlNothing());
	}

	@Override
	public <O> O accept(final ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances.
	 * 
	 * @author Peter Skocovsky
	 */
	public interface Factory {

		ElkClassInclusionOwlBottomObjectProperty getElkClassInclusionOwlBottomObjectProperty();

	}

	/**
	 * The visitor pattern for instances.
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkClassInclusionOwlBottomObjectProperty inference);

	}

}
