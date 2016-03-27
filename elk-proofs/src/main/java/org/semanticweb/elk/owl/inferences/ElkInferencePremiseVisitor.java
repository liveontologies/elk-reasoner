package org.semanticweb.elk.owl.inferences;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

public class ElkInferencePremiseVisitor<O> implements ElkInference.Visitor<O> {

	private final ElkObjectFactory conclusionFactory_;

	private final ElkAxiomVisitor<?> conclusionVisitor_;

	public ElkInferencePremiseVisitor(ElkObjectFactory conclusionFactory,
			ElkAxiomVisitor<?> conclusionVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
	}

	@Override
	public O visit(ElkClassInclusionExistentialFillerUnfolding inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionExistentialOfObjectHasSelf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionExistentialPropertyUnfolding inference) {
		for (int i = 1; i <= inference.getExistentialPremiseCount(); i++) {
			conclusionVisitor_.visit(
					inference.getExistentialPremise(i, conclusionFactory_));
		}
		conclusionVisitor_.visit(inference.getLastPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionHierarchy inference) {
		for (int i = 1; i <= inference.getPremiseCount(); i++) {
			conclusionVisitor_
					.visit(inference.getPremise(i, conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(ElkClassInclusionObjectIntersectionOfComposition inference) {
		for (int i = 1; i <= inference.getPremiseCount(); i++) {
			conclusionVisitor_
					.visit(inference.getPremise(i, conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionObjectUnionOfComposition inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionOfEquivalence inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionOfObjectPropertyDomain inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionOfReflexiveObjectProperty inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionOwlThing inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(ElkClassInclusionReflexivePropertyRange inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkClassInclusionTautology inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(ElkPropertyInclusionHierarchy inference) {
		for (int i = 1; i <= inference.getPremiseCount(); i++) {
			conclusionVisitor_
					.visit(inference.getPremise(i, conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ElkPropertyInclusionTautology inference) {
		// no premises
		return null;
	}

	@Override
	public O visit(ElkPropertyRangePropertyUnfolding inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

}
