/**
 * 
 */
package org.semanticweb.elk.owl.visitors;
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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
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
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseElkDatatypeVisitor<O> implements
		ElkDatatypeVisitor<O> {

	protected abstract O defaultVisit(ElkDatatype datatype);
	
	@Override
	public O visit(LiteralDatatype datatype) {
		return defaultVisit(datatype);
	}

	@Override
	public O visit(DateTimeDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(DateTimeStampDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(Base64BinaryDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(HexBinaryDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(AnyUriDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(RealDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(RationalDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(DecimalDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(IntegerDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(NonNegativeIntegerDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(PlainLiteralDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(StringDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(NormalizedStringDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(TokenDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(NameDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(NcNameDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(NmTokenDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(XmlLiteralDatatype datatype) {
		return defaultVisit(datatype);

	}

	@Override
	public O visit(UndefinedDatatype datatype) {
		return defaultVisit(datatype);

	}

}
