/**
 * 
 */
package org.semanticweb.elk.owl.implementation.literals;
/*
 * #%L
 * ELK OWL Model Implementation
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.literals.UnsupportedElkLiteral;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkUnsupportedLiteralImpl extends ElkLiteralImpl implements
		UnsupportedElkLiteral {

	private final ElkDatatype datatype_;
	
	public ElkUnsupportedLiteralImpl(String lexicalForm, ElkDatatype datatype) {
		super(lexicalForm);
		datatype_ = datatype;
	}

	@Override
	public ElkDatatype getDatatype() {
		return datatype_;
	}
}
