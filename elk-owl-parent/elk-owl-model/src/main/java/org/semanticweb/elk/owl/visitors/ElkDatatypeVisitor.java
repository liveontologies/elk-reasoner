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

import org.semanticweb.elk.owl.interfaces.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.LiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.StringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.UndefinedDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.XmlLiteralDatatype;

/**
 *
 * @author Pospishnyi Olexandr
 */
public interface ElkDatatypeVisitor<O> {

	O visit(LiteralDatatype datatype);

	O visit(DateTimeDatatype datatype);

	O visit(DateTimeStampDatatype datatype);

	O visit(Base64BinaryDatatype datatype);

	O visit(HexBinaryDatatype datatype);

	O visit(AnyUriDatatype datatype);

	O visit(RealDatatype datatype);

	O visit(RationalDatatype datatype);

	O visit(DecimalDatatype datatype);

	O visit(IntegerDatatype datatype);

	O visit(NonNegativeIntegerDatatype datatype);

	O visit(PlainLiteralDatatype datatype);

	O visit(StringDatatype datatype);

	O visit(NormalizedStringDatatype datatype);

	O visit(TokenDatatype datatype);

	O visit(NameDatatype datatype);

	O visit(NcNameDatatype datatype);

	O visit(NmTokenDatatype datatype);

	O visit(XmlLiteralDatatype datatype);

	O visit(UndefinedDatatype datatype);
}
