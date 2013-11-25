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

import org.semanticweb.elk.owl.exceptions.ElkException;
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
 * This interface helps parsing the lexical representation of a literal (passed
 * as the parameter) based on the datatype of that literal.
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public interface ElkDatatypeParser<O, P, E extends ElkException> {

	O parse(LiteralDatatype datatype, P param) throws E;

	O parse(DateTimeDatatype datatype, P param) throws E;

	O parse(DateTimeStampDatatype datatype, P param) throws E;

	O parse(Base64BinaryDatatype datatype, P param) throws E;

	O parse(HexBinaryDatatype datatype, P param) throws E;

	O parse(AnyUriDatatype datatype, P param) throws E;

	O parse(RealDatatype datatype, P param) throws E;

	O parse(RationalDatatype datatype, P param) throws E;

	O parse(DecimalDatatype datatype, P param) throws E;

	O parse(IntegerDatatype datatype, P param) throws E;

	O parse(NonNegativeIntegerDatatype datatype, P param) throws E;

	O parse(PlainLiteralDatatype datatype, P param) throws E;

	O parse(StringDatatype datatype, P param) throws E;

	O parse(NormalizedStringDatatype datatype, P param) throws E;

	O parse(TokenDatatype datatype, P param) throws E;

	O parse(NameDatatype datatype, P param) throws E;

	O parse(NcNameDatatype datatype, P param) throws E;

	O parse(NmTokenDatatype datatype, P param) throws E;

	O parse(XmlLiteralDatatype datatype, P param) throws E;

	O parse(UndefinedDatatype datatype, P param) throws E;
}
