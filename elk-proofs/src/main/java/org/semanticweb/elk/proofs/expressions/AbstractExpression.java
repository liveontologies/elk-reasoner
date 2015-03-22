/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;
/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceHasher;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceReader;

/**
 * The abstract base class of all {@link Expression}s.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class AbstractExpression implements Expression {

	private final InferenceReader reader_;
	
	AbstractExpression(InferenceReader reader) {
		reader_ = reader;
	}

	@Override
	public Iterable<Inference> getInferences() throws ElkException {
		return reader_.getInferences(this);
	}
	
	// overriding equals and hashCode to allow expression wrappers which could break the pointer-equality which is otherwise guaranteed by our expression factory.
	// TODO it's bad that we explicitly use structural checker/hasher here, makes it hard to use another checker/hasher in the factory, if needed
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Expression) {
			return new StructuralEquivalenceChecker().equal(this, (Expression) obj);
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return new StructuralEquivalenceHasher().hashCode(this);
	}

}
