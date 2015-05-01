/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference based on a ReflexiveObjectProperty axiom in the
 * ontology.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class ToldReflexiveProperty extends
		ReflexivePropertyChain<IndexedObjectProperty> implements
		ObjectPropertyInference {

	/**
	 * The {@link ElkAxiom} responsible for the reflexivity of the property
	 */
	private final ElkAxiom reason_;

	public ToldReflexiveProperty(IndexedObjectProperty property, ElkAxiom reason) {
		super(property);
		this.reason_ = reason;
	}

	public SubObjectProperty getPropertyInitialization() {
		return new SubObjectProperty(getPropertyChain(), getPropertyChain());
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return new InferencePrinter().visit(this, null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ToldReflexiveProperty)) {
			return false;
		}

		ToldReflexiveProperty inf = (ToldReflexiveProperty) obj;

		return getPropertyChain().equals(inf.getPropertyChain());
	}

	@Override
	public int hashCode() {
		return getPropertyChain().hashCode();
	}
}
