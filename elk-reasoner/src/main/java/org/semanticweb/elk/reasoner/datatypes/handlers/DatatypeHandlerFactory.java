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
package org.semanticweb.elk.reasoner.datatypes.handlers;

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
import org.semanticweb.elk.owl.visitors.DatatypeVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingUnsupportedException;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class DatatypeHandlerFactory implements DatatypeVisitor<DatatypeHandler> {

	private AnyURIDatatypeHandler anyURIHandler = new AnyURIDatatypeHandler();
	private BinaryDatatypeHandler binaryHandler = new BinaryDatatypeHandler();
	private DateTimeDatatypeHandler dateTimeHandler = new DateTimeDatatypeHandler();
	private LiteralDatatypeHandler literalHandler = new LiteralDatatypeHandler();
	private NumericDatatypeHandler numericHandler = new NumericDatatypeHandler();
	private PlainLiteralDatatypeHandler plainLiteralHandler = new PlainLiteralDatatypeHandler();
	private XMLLiteralDatatypeHandler xmlLiteralHandler = new XMLLiteralDatatypeHandler();

	@Override
	public DatatypeHandler visit(LiteralDatatype datatype) {
		return literalHandler;
	}

	@Override
	public DatatypeHandler visit(DateTimeDatatype datatype) {
		return dateTimeHandler;
	}

	@Override
	public DatatypeHandler visit(DateTimeStampDatatype datatype) {
		return dateTimeHandler;
	}

	@Override
	public DatatypeHandler visit(Base64BinaryDatatype datatype) {
		return binaryHandler;
	}

	@Override
	public DatatypeHandler visit(HexBinaryDatatype datatype) {
		return binaryHandler;
	}

	@Override
	public DatatypeHandler visit(AnyUriDatatype datatype) {
		return anyURIHandler;
	}

	@Override
	public DatatypeHandler visit(RealDatatype datatype) {
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(RationalDatatype datatype) {
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(DecimalDatatype datatype) {
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(IntegerDatatype datatype) {
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(NonNegativeIntegerDatatype datatype) {
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(PlainLiteralDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(StringDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NormalizedStringDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(TokenDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NameDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NcNameDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NmTokenDatatype datatype) {
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(XmlLiteralDatatype datatype) {
		return xmlLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(UndefinedDatatype datatype) {
		throw new ElkIndexingUnsupportedException(datatype);
	}
}
