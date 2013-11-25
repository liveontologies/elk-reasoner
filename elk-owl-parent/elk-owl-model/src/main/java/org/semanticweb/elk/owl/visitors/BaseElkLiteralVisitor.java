/**
 * 
 */
package org.semanticweb.elk.owl.visitors;
/*
 * #%L
 * ELK OWL Object Interfaces
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

import org.semanticweb.elk.owl.interfaces.literals.ElkAnyUriLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkBase64BinaryLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkDateTimeLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkDateTimeStampLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkDecimalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkHexBinaryLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntegerLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLongLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkNameLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkNcNameLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkNmTokenLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkNormalizedStringLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkPlainLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRationalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRealLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkStringLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkTokenLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkXmlLiteral;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseElkLiteralVisitor<O> implements ElkLiteralVisitor<O> {

	protected abstract O defaultVisit(ElkLiteral elkLiteral);
	
	@Override
	public O visit(ElkLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkDateTimeLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkDateTimeStampLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkBase64BinaryLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkHexBinaryLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkAnyUriLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkRealLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkRationalLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkDecimalLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkIntegerLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}	

	@Override
	public O visit(ElkIntLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkLongLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkPlainLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkStringLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkNormalizedStringLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkTokenLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkNameLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkNcNameLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkNmTokenLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkXmlLiteral elkLiteral) {
		return defaultVisit(elkLiteral);
	}
	
}
