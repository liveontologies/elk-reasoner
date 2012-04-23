/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.owl.interfaces;

import java.util.List;

/**
 * Common interface for DataSomeValuesFrom and DataAllValuesFrom
 * restrictions which can be based on a list of data (not object) property expression.
 * 
 * Arity of the datarange <i>must</i> correspond to the number of properties in the list. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkDataPropertyListRestrictionQualified extends ElkClassExpression {

	public List<? extends ElkDataPropertyExpression> getDataPropertyExpressions();
	
	public ElkDataRange getDataRange();
}
