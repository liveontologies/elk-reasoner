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

import org.semanticweb.elk.owl.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.datatypes.LiteralDatatype;
import org.semanticweb.elk.owl.datatypes.NameDatatype;
import org.semanticweb.elk.owl.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.datatypes.RealDatatype;
import org.semanticweb.elk.owl.datatypes.StringDatatype;
import org.semanticweb.elk.owl.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.datatypes.UndefinedDatatype;
import org.semanticweb.elk.owl.datatypes.XmlLiteralDatatype;

/**
 *
 * @author Pospishnyi Olexandr
 */
public interface ElkDatatypeParser<O, P> {

	O parse(LiteralDatatype datatype, P param);

	O parse(DateTimeDatatype datatype, P param);

	O parse(DateTimeStampDatatype datatype, P param);

	O parse(Base64BinaryDatatype datatype, P param);

	O parse(HexBinaryDatatype datatype, P param);

	O parse(AnyUriDatatype datatype, P param);

	O parse(RealDatatype datatype, P param);

	O parse(RationalDatatype datatype, P param);

	O parse(DecimalDatatype datatype, P param);

	O parse(IntegerDatatype datatype, P param);

	O parse(NonNegativeIntegerDatatype datatype, P param);

	O parse(PlainLiteralDatatype datatype, P param);

	O parse(StringDatatype datatype, P param);

	O parse(NormalizedStringDatatype datatype, P param);

	O parse(TokenDatatype datatype, P param);

	O parse(NameDatatype datatype, P param);

	O parse(NcNameDatatype datatype, P param);

	O parse(NmTokenDatatype datatype, P param);

	O parse(XmlLiteralDatatype datatype, P param);

	O parse(UndefinedDatatype datatype, P param);
}
