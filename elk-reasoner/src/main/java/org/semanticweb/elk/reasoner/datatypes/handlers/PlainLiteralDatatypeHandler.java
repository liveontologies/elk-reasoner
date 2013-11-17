/*
 * #%L
 * ELK Reasoner
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.handlers;

import java.util.HashMap;

import org.semanticweb.elk.owl.datatypes.NameDatatype;
import org.semanticweb.elk.owl.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.datatypes.StringDatatype;
import org.semanticweb.elk.owl.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.datatypes.util.LiteralParser;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.PatternValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;
import org.semanticweb.elk.util.collections.Pair;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.Datatypes;
import dk.brics.automaton.RegExp;

/**
 * rdf:PlainLiteral, xsd:string, xsd:normalizedString, xsd:token, xsd:Name,
 * xsd:NCName, xsd:NMTOKEN datatype handler.
 * <p>
 * uses {@link LengthRestrictedValueSpace} and {@link PatternValueSpace} to
 * represent datatype restrictions.
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class PlainLiteralDatatypeHandler extends AbstractDatatypeHandler {

	private final Automaton stringAutomaton;
	private final Automaton normalizedStringAutomaton;
	private final Automaton tokenAutomaton;
	private final Automaton NameAutomaton;
	private final Automaton NCNameAutomaton;
	private final Automaton NMTokenAutomaton;

	public PlainLiteralDatatypeHandler() {
		// building automaton cache for construction of our automatons
		HashMap<String, Automaton> autoMap = new HashMap<String, Automaton>();
		autoMap.put("NameChar", Datatypes.get("NameChar"));
		autoMap.put("Letter", Datatypes.get("Letter"));

		/*
		 * Valid XML character is any Unicode character, excluding the surrogate
		 * blocks
		 *
		 * Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
		 * [#x10000-#x10FFFF]
		 */
		autoMap.put(
			"XmlChar",
			new RegExp(
			"[\t\n\r\u0020-\uD7FF\ue000-\ufffd]|[\uD800-\uDBFF][\uDC00-\uDFFF]")
			.toAutomaton());

		/*
		 * NormalChar is the XmlChar without \t \n and \r characters
		 */
		autoMap.put("NormalChar", new RegExp(
				"[\u0020-\uD7FF\ue000-\ufffd]|[\uD800-\uDBFF][\uDC00-\uDFFF]")
				.toAutomaton());

		/*
		 * NoWhitespace is the NormalChar without whitespace character
		 */
		autoMap.put("NoWhitespace", new RegExp(
				"[\u0021-\uD7FF\ue000-\ufffd]|[\uD800-\uDBFF][\uDC00-\uDFFF]")
				.toAutomaton());

		/*
		 * xsd:string is the set of finite-length sequences of valid XML
		 * characters
		 */
		stringAutomaton = new RegExp("<XmlChar>*").toAutomaton(autoMap);

		/*
		 * xsd:normalizedString is the set of strings that do not contain the
		 * carriage return (#xD), line feed (#xA) nor tab (#x9) characters
		 */
		normalizedStringAutomaton = new RegExp("<NormalChar>*")
				.toAutomaton(autoMap);

		/*
		 * xsd:token is the set of strings that do not contain the carriage
		 * return (#xD), line feed (#xA) nor tab (#x9) characters, that have no
		 * leading or trailing spaces (#x20) and that have no internal sequences
		 * of two or more spaces.
		 */
		tokenAutomaton = new RegExp(
				"(<NoWhitespace>+(\u0020<NoWhitespace>+)*)?")
				.toAutomaton(autoMap);

		/*
		 * xsd:Name is the set of all strings which match the Name production
		 * NameChar ::= Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar |
		 * Extender Name ::= (Letter | '_' | ':') ( NameChar)*
		 */
		NameAutomaton = new RegExp("(<Letter>|[_:])<NameChar>*")
				.toAutomaton(autoMap);

		/*
		 * xsd:NCName is the set of all strings which match the NCName
		 * production NCNameChar ::= Letter | Digit | '.' | '-' | '_' |
		 * CombiningChar | Extender NCName ::= (Letter | '_') (NCNameChar)*
		 */
		NCNameAutomaton = Datatypes.get("NCName");

		/*
		 * xsd:NMToken is the set of all strings which match the NMToken
		 * production NameChar ::= Letter | Digit | '.' | '-' | '_' | ':' |
		 * CombiningChar | Extender Nmtoken ::= (NameChar)+
		 */
		NMTokenAutomaton = new RegExp("<NameChar>+").toAutomaton(autoMap);
	}

	@Override
	protected ElkDatatypeVisitor<ValueSpace<?>> getLiteralConverter(
			final ElkLiteral literal) {
		return new BaseElkDatatypeVisitor<ValueSpace<?>>() {

			@Override
			public ValueSpace<?> visit(PlainLiteralDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(StringDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(NormalizedStringDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(TokenDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(NameDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(NcNameDatatype datatype) {
				return createLiteralValue(datatype);
			}

			@Override
			public ValueSpace<?> visit(NmTokenDatatype datatype) {
				return createLiteralValue(datatype);
			}
			
			private ValueSpace<?> createLiteralValue(ElkDatatype datatype) {
				String[] parsed = LiteralParser.parseStringLiteral(literal.getLexicalForm());
				String value = parsed[0];
				String language = parsed.length > 1 ? parsed[1] : literal.getLanguage();
				ElkDatatype effectiveDatatype = determineDatatype(value);
				//check that the effective datatype is compatible with the declared datatype
				if (effectiveDatatype.isCompatibleWith(datatype)) {
					if (language != null && !language.isEmpty()) {
						// language tag is present
						return new LiteralValue(new Pair<String, String>(value, language), effectiveDatatype);
					} else {
						// no language tag for this literal
						return new LiteralValue(value, effectiveDatatype);
					}
				}
				else {
					return EmptyValueSpace.INSTANCE;
				}
			}
			
		};
	}


	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return new BaseElkDataRangeVisitor<ValueSpace<?>>() {

			@Override
			public ValueSpace<?> visit(ElkDatatype elkDatatype) {
				return elkDatatype.accept(new BaseElkDatatypeVisitor<EntireValueSpace<?>>() {

					@Override
					public EntireValueSpace<?> visit(
							PlainLiteralDatatype datatype) {
						return EntireValueSpace.RDF_PLAIN_LITERAL;
					}

					@Override
					public EntireValueSpace<?> visit(StringDatatype datatype) {
						return EntireValueSpace.XSD_STRING;
					}

					@Override
					public EntireValueSpace<?> visit(
							NormalizedStringDatatype datatype) {
						return EntireValueSpace.XSD_NORMALIZED_STRING;
					}

					@Override
					public EntireValueSpace<?> visit(TokenDatatype datatype) {
						return EntireValueSpace.XSD_TOKEN;
					}

					@Override
					public EntireValueSpace<?> visit(NameDatatype datatype) {
						return EntireValueSpace.XSD_NAME;
					}

					@Override
					public EntireValueSpace<?> visit(NcNameDatatype datatype) {
						return EntireValueSpace.XSD_NCNAME;
					}

					@Override
					public EntireValueSpace<?> visit(NmTokenDatatype datatype) {
						return EntireValueSpace.XSD_NMTOKEN;
					}
					
				});
			}

			@Override
			public ValueSpace<?> visit(
					ElkDatatypeRestriction elkDatatypeRestriction) {
				Integer minLength = 0;
				Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);

				for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
						.getFacetRestrictions()) {
					PredefinedElkIri facet = PredefinedElkIri.lookup(facetRestriction
							.getConstrainingFacet());
					String lexicalForm = facetRestriction.getRestrictionValue()
							.getLexicalForm();
					String value = LiteralParser.parseStringLiteral(lexicalForm)[0];

					switch (facet) {
					case XSD_LENGTH:
						minLength = Integer.valueOf(value);
						maxLength = minLength;
						
						return new LengthRestrictedValueSpace(elkDatatypeRestriction.getDatatype(), minLength, maxLength);
					case XSD_MIN_LENGTH:
						minLength = Integer.valueOf(value);
						break;
					case XSD_MAX_LENGTH:
						maxLength = Integer.valueOf(value);
						break;
					case XSD_PATTERN:
						Automaton pattern = new RegExp(value).toAutomaton();
						pattern.setInfo(value);
						ElkDatatype effectiveDatatype = determineDatatype(pattern);
						
						if (effectiveDatatype.isCompatibleWith(elkDatatypeRestriction.getDatatype())) {
							return new PatternValueSpace(pattern, effectiveDatatype);
						}
						else {
							return EmptyValueSpace.INSTANCE;
						}

					default:
						throw new ElkUnexpectedIndexingException("Unsupported facet: " + facet);
					}
				}
				
				return new LengthRestrictedValueSpace(elkDatatypeRestriction.getDatatype(), minLength, maxLength);
			}
			
		};
	}


	/**
	 * Determine most specific datatype for the input string.
	 *
	 * @param string
	 *            input
	 * @return most specific {@link Datatype}
	 */
	private ElkDatatype determineDatatype(String string) {
		ElkDatatype retType = ElkDatatypeMap.RDF_PLAIN_LITERAL;
		if (stringAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_STRING;
		} else {
			return retType;
		}
		if (normalizedStringAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_NORMALIZED_STRING;
		} else {
			return retType;
		}
		if (tokenAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_TOKEN;
		} else {
			return retType;
		}
		if (NMTokenAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_NMTOKEN;
		} else {
			return retType;
		}
		if (NameAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_NAME;
		} else {
			return retType;
		}
		if (NCNameAutomaton.run(string)) {
			retType = ElkDatatypeMap.XSD_NCNAME;
		} else {
			return retType;
		}
		return retType;
	}

	/**
	 * Determine most specific datatype for input pattern
	 *
	 * @param pattern
	 *            regular expression automaton
	 * @return most specific {@link Datatype}
	 */
	private ElkDatatype determineDatatype(Automaton pattern) {
		ElkDatatype retType = ElkDatatypeMap.RDF_PLAIN_LITERAL;
		if (!stringAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_STRING;
		} else {
			return retType;
		}
		if (!normalizedStringAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_NORMALIZED_STRING;
		} else {
			return retType;
		}
		if (!tokenAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_TOKEN;
		} else {
			return retType;
		}
		if (!NMTokenAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_NMTOKEN;
		} else {
			return retType;
		}
		if (!NameAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_NAME;
		} else {
			return retType;
		}
		if (!NCNameAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.XSD_NCNAME;
		} else {
			return retType;
		}
		return retType;
	}

}
