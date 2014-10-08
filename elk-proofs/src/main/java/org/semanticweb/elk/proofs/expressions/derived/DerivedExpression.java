/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;
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
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * Extension of {@link Expression} which returns {@link Inference}s which derive
 * it. This class by itself does not specify whether some or all inferences will
 * be returned. Instead, the code which returns derived expressions should
 * specify completeness guarantees.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface DerivedExpression extends Expression {

	/**
	 * 
	 * @return
	 * @throws ElkException
	 */
	public Iterable<Inference> getInferences() throws ElkException;
}
