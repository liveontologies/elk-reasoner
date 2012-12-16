/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InvalidTaxonomyException extends RuntimeException {

	private static final long serialVersionUID = -511222809937732780L;
	
	private Collection<ElkAxiom> axioms;
	
	InvalidTaxonomyException(String msg) {
		super(msg);
	}
	
	InvalidTaxonomyException(String msg, Collection<ElkAxiom> axioms) {
		super(msg);
		this.axioms = axioms;
	}
	
	Collection<ElkAxiom> getAxioms() {
		return axioms;
	}

}
