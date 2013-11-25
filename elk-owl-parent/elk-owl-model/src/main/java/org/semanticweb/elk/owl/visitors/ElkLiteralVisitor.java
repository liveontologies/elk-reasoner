/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.visitors;

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
 * Visitor interface for {@link ElkLiteral}.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 * @param <O>
 *            the output type of the visitor
 */
public interface ElkLiteralVisitor<O> {

	O visit(ElkLiteral elkLiteral);

	O visit(ElkDateTimeLiteral elkLiteral);
	
	O visit(ElkDateTimeStampLiteral elkLiteral);
	
	O visit(ElkBase64BinaryLiteral elkLiteral);
	
	O visit(ElkHexBinaryLiteral elkLiteral);
	
	O visit(ElkAnyUriLiteral elkLiteral);
	
	O visit(ElkRealLiteral elkLiteral);
	
	O visit(ElkRationalLiteral elkLiteral);
	
	O visit(ElkDecimalLiteral elkLiteral);
	
	O visit(ElkIntegerLiteral elkLiteral);
	
	O visit(ElkIntLiteral elkLiteral);
	
	O visit(ElkLongLiteral elkLiteral);
	
	O visit(ElkPlainLiteral elkLiteral);
	
	O visit(ElkStringLiteral elkLiteral);
	
	O visit(ElkNormalizedStringLiteral elkLiteral);
	
	O visit(ElkTokenLiteral elkLiteral);
	
	O visit(ElkNameLiteral elkLiteral);
	
	O visit(ElkNcNameLiteral elkLiteral);
	
	O visit(ElkNmTokenLiteral elkLiteral);
	
	O visit(ElkXmlLiteral elkLiteral);
}
