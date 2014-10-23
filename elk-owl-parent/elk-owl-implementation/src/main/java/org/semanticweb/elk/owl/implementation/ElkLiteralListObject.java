/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.implementation;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkLiteral;

/**
 * Implementation for ElkObjects that maintain a list of literals.
 * 
 * @author Markus Kroetzsch
 */
public abstract class ElkLiteralListObject extends
		ElkObjectListObject<ElkLiteral> {

	ElkLiteralListObject(List<? extends ElkLiteral> literals) {
		super(literals);
	}

	public List<? extends ElkLiteral> getIndividuals() {
		return getObjects();
	}

}
