/**
 * 
 */
package org.semanticweb.owlapitools.proofs.expressions;

/*
 * #%L
 * OWL API Proofs Model
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

/**
 * This interface should be implemented by various wrappers around
 * {@link OWLExpression} if they require that equals is based on equality of
 * wrapped objects. In turn, every implementation of {@link OWLExpression}, if
 * it provides custom equals logic, must first check if the passed object is a
 * wrapper and unwrap the underlying expression using the {@link #getExpression}
 * method.
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 *
 */
public interface OWLExpressionWrap {

	public OWLExpression getExpression();
}
