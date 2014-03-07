package org.semanticweb.elk.alc.saturation;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.fail;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomVisitor;

public class SaturationCheckingAxiomVisitor implements
		IndexedAxiomVisitor<Void> {

	private final SaturationState saturationState_;

	public SaturationCheckingAxiomVisitor(SaturationState state) {
		this.saturationState_ = state;
	}

	static Void failVisit(IndexedAxiom axiom) {
		fail("Saturation should contain " + axiom);
		return null;
	}

	@Override
	public Void visit(IndexedSubClassOfAxiom axiom) {
		Root root = new Root(axiom.getSubClass());
		Context context = saturationState_.getContext(root);
		if (context != null
				&& context.getSubsumers().contains(axiom.getSuperClass()))
			return null;
		// else
		return failVisit(axiom);
	}

}
