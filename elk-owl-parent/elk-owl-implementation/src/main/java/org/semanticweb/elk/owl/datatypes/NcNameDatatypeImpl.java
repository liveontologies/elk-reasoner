/*
 * Copyright 2013 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.owl.datatypes;

import org.semanticweb.elk.owl.implementation.ElkDatatypeImpl;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.DatatypeVisitor;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class NcNameDatatypeImpl extends ElkDatatypeImpl implements NcNameDatatype {

	public NcNameDatatypeImpl() {
		super(PredefinedElkIri.XSD_NCNAME.get());
	}

	@Override
	public <O> O accept(DatatypeVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
