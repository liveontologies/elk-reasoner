/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.handlers;
/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.owl.interfaces.literals.ElkDecimalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntegerLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLongLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRationalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRealLiteral;
import org.semanticweb.elk.owl.visitors.BaseElkLiteralVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class BaseElkNumericLiteralVisitor<O> extends BaseElkLiteralVisitor<O> {

	@Override
	protected O defaultVisit(ElkLiteral elkLiteral) {
		throw new ElkUnexpectedIndexingException("Unexpected non-numeric literal " + elkLiteral);
	}
	
	protected abstract O defaultNumericVisit(ElkRealLiteral elkLiteral);

	@Override
	public O visit(ElkRealLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

	@Override
	public O visit(ElkRationalLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

	@Override
	public O visit(ElkDecimalLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

	@Override
	public O visit(ElkIntegerLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

	@Override
	public O visit(ElkIntLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

	@Override
	public O visit(ElkLongLiteral elkLiteral) {
		return defaultNumericVisit(elkLiteral);
	}

}
