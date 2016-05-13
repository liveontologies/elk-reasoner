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

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;

public class PropagationMatch1
		extends AbstractClassConclusionMatch<Propagation> {

	private final IndexedContextRootMatch destinationMatch_;

	private final ElkObjectProperty subDestinationMatch_;

	private final IndexedObjectSomeValuesFromMatch carryMatch_;

	PropagationMatch1(Propagation parent,
			IndexedContextRootMatch destinationMatch,
			ElkObjectProperty subDestinationMatch,
			IndexedObjectSomeValuesFromMatch carryMatch) {
		super(parent);
		this.destinationMatch_ = destinationMatch;
		this.subDestinationMatch_ = subDestinationMatch;
		this.carryMatch_ = carryMatch;
	}

	public IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public ElkObjectProperty getSubDestinationMatch() {
		return subDestinationMatch_;
	}

	public IndexedObjectSomeValuesFromMatch getCarryMatch() {
		return carryMatch_;
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

		PropagationMatch1 getPropagationMatch1(Propagation parent,
				IndexedContextRootMatch destinationMatch,
				ElkObjectProperty subDestinationMatch,
				IndexedObjectSomeValuesFromMatch carryMatch);

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

		O visit(PropagationMatch1 conclusionMatch);

	}

}
