package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

/**
 * Factory for creating
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Datatype_Maps">built-in
 * datatypes</a> in the OWL 2 specification.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface PredefinedElkDatatypeFactory {

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code owl:real}
	 */
	ElkDatatype getOwlReal();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code owl:rational}
	 */
	ElkDatatype getOwlRational();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:decimal}
	 */
	ElkDatatype getXsdDecimal();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:integer}
	 */
	ElkDatatype getXsdInteger();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:nonNegativeInteger}
	 */
	ElkDatatype getXsdNonNegativeInteger();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:nonPositiveInteger}
	 */
	ElkDatatype getXsdNonPositiveInteger();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:positiveInteger}
	 */
	ElkDatatype getXsdPositiveInteger();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:negativeInteger}
	 */
	ElkDatatype getXsdNegativeInteger();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:long}
	 */
	ElkDatatype getXsdLong();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:int}
	 */
	ElkDatatype getXsdInt();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:short}
	 */
	ElkDatatype getXsdShort();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:byte}
	 */
	ElkDatatype getXsdByte();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:unsignedLong}
	 */
	ElkDatatype getXsdUnsignedLong();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:unsignedInt}
	 */
	ElkDatatype getXsdUnsignedInt();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:unsignedShort}
	 */
	ElkDatatype getXsdUnsignedShort();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code unsignedByte}
	 */
	ElkDatatype getXsdUnsignedByte();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:double}
	 */
	ElkDatatype getXsdDouble();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:float}
	 */
	ElkDatatype getXsdFloat();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:string}
	 */
	ElkDatatype getXsdString();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:normalizedString}
	 */
	ElkDatatype getXsdNormalizedString();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:token}
	 */
	ElkDatatype getXsdToken();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:language}
	 */
	ElkDatatype getXsdLanguage();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:Name}
	 */
	ElkDatatype getXsdName();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:NCName}
	 */
	ElkDatatype getXsdNCName();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:NMTOKEN}
	 */
	ElkDatatype getXsdNMTOKEN();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:hexBinary}
	 */
	ElkDatatype getXsdHexBinary();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:base64Binary}
	 */
	ElkDatatype getXsdBase64Binary();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:anyURI}
	 */
	ElkDatatype getXsdAnyUri();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code xsd:dateTime}
	 */
	ElkDatatype getXsdDateTime();

	/**
	 * @return the {@link ElkDatatype} corresponding to
	 *         {@code xsd:dateTimeStamp}
	 */
	ElkDatatype getXsdDateTimeStamp();

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code rdf:XMLLiteral}
	 */
	ElkDatatype getRdfXMLLiteral();

	/*
	 * not listed in Section 4 of OWL specifications but listed in Table 5
	 */

	/**
	 * @return the {@link ElkDatatype} corresponding to {@code rdfs:Literal}
	 */
	ElkDatatype getRdfsLiteral();

}
