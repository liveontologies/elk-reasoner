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
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingUnsupportedException;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class DatatypeHandlerFactory implements ElkDatatypeVisitor<DatatypeHandler> {

	private AnyURIDatatypeHandler anyURIHandler;
	private BinaryDatatypeHandler binaryHandler;
	private DateTimeDatatypeHandler dateTimeHandler;
	private LiteralDatatypeHandler literalHandler;
	private NumericDatatypeHandler numericHandler;
	private PlainLiteralDatatypeHandler plainLiteralHandler;
	private XMLLiteralDatatypeHandler xmlLiteralHandler;

	@Override
	public DatatypeHandler visit(LiteralDatatype datatype) {
		if (literalHandler == null) {
			literalHandler = new LiteralDatatypeHandler();
		}
		return literalHandler;
	}

	@Override
	public DatatypeHandler visit(DateTimeDatatype datatype) {
		if (dateTimeHandler == null) {
			dateTimeHandler = new DateTimeDatatypeHandler();
		}
		return dateTimeHandler;
	}

	@Override
	public DatatypeHandler visit(DateTimeStampDatatype datatype) {
		if (dateTimeHandler == null) {
			dateTimeHandler = new DateTimeDatatypeHandler();
		}
		return dateTimeHandler;
	}

	@Override
	public DatatypeHandler visit(Base64BinaryDatatype datatype) {
		if (binaryHandler == null) {
			binaryHandler = new BinaryDatatypeHandler();
		}
		return binaryHandler;
	}

	@Override
	public DatatypeHandler visit(HexBinaryDatatype datatype) {
		if (binaryHandler == null) {
			binaryHandler = new BinaryDatatypeHandler();
		}
		return binaryHandler;
	}

	@Override
	public DatatypeHandler visit(AnyUriDatatype datatype) {
		if (anyURIHandler == null) {
			anyURIHandler = new AnyURIDatatypeHandler();
		}
		return anyURIHandler;
	}

	@Override
	public DatatypeHandler visit(RealDatatype datatype) {
		if (numericHandler == null) {
			numericHandler = new NumericDatatypeHandler();
		}
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(RationalDatatype datatype) {
		if (numericHandler == null) {
			numericHandler = new NumericDatatypeHandler();
		}
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(DecimalDatatype datatype) {
		if (numericHandler == null) {
			numericHandler = new NumericDatatypeHandler();
		}
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(IntegerDatatype datatype) {
		if (numericHandler == null) {
			numericHandler = new NumericDatatypeHandler();
		}
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(NonNegativeIntegerDatatype datatype) {
		if (numericHandler == null) {
			numericHandler = new NumericDatatypeHandler();
		}
		return numericHandler;
	}

	@Override
	public DatatypeHandler visit(PlainLiteralDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(StringDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NormalizedStringDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(TokenDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NameDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NcNameDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(NmTokenDatatype datatype) {
		if (plainLiteralHandler == null) {
			plainLiteralHandler = new PlainLiteralDatatypeHandler();
		}
		return plainLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(XmlLiteralDatatype datatype) {
		if (xmlLiteralHandler == null) {
			xmlLiteralHandler = new XMLLiteralDatatypeHandler();
		}
		return xmlLiteralHandler;
	}

	@Override
	public DatatypeHandler visit(UndefinedDatatype datatype) {
		throw new ElkIndexingUnsupportedException(datatype);
	}
}
