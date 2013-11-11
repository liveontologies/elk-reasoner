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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.PatternValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;
import org.semanticweb.elk.util.collections.Pair;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.Datatypes;
import dk.brics.automaton.RegExp;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;

/**
 * rdf:PlainLiteral, xsd:string, xsd:normalizedString, xsd:token, xsd:Name,
 * xsd:NCName, xsd:NMTOKEN datatype handler
 * <p>
 * uses {@link LengthRestrictedValueSpace} and {@link PatternValueSpace} to
 * represent datatype restrictions
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class PlainLiteralDatatypeHandler extends AbstractDatatypeHandler {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(PlainLiteralDatatypeHandler.class);

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
	public ValueSpace visit(ElkLiteral literal) {
		ElkDatatype datatype = literal.getDatatype();
		ElkDatatype effectiveDatatype = datatype;

		String lexicalForm = literal.getLexicalForm();

		String[] pair = parse(lexicalForm);
		String value = pair[0];
		String language = pair[1] != null ? pair[1] : literal.getLanguage();

		effectiveDatatype = determineDatatype(value);

		if (value != null) {
			if (language != null && !language.isEmpty()) {
				// language tag is present
				return new LiteralValue(new Pair<String, String>(value,
						language), datatype, effectiveDatatype);
			} else {
				// no language tag for this literal
				return new LiteralValue(value, datatype, effectiveDatatype);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public ValueSpace visit(ElkDatatype elkDatatype) {
		return new EntireValueSpace(elkDatatype);
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		Integer minLength = 0;
		Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);

		ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

		List<? extends ElkFacetRestriction> facetRestrictions = elkDatatypeRestriction
				.getFacetRestrictions();
		outerloop: for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction
					.getConstrainingFacet().getFullIriAsString());
			String lexicalForm = facetRestriction.getRestrictionValue()
					.getLexicalForm();
			String[] pair = parse(lexicalForm);
			String value = pair[0];

			switch (facet) {
			case LENGTH:
				minLength = Integer.valueOf(value);
				maxLength = minLength;
				break outerloop;
			case MIN_LENGTH:
				minLength = Integer.valueOf(value);
				break;
			case MAX_LENGTH:
				maxLength = Integer.valueOf(value);
				break;
			case PATTERN:
				Automaton pattern = new RegExp(value).toAutomaton();
				pattern.setInfo(value);
				ElkDatatype effectiveDatatype = determineDatatype(pattern);
				PatternValueSpace vs = new PatternValueSpace(pattern, datatype,
						effectiveDatatype);
				if (vs.isEmpty()) {
					return EmptyValueSpace.INSTANCE;
				} else {
					return vs;
				}
			default:
				LOGGER_.warn("Unsupported facet: " + facet.iri);
				return null;
			}
		}

		LengthRestrictedValueSpace vs = new LengthRestrictedValueSpace(
				datatype, minLength, maxLength);
		if (vs.isEmpty()) {
			return EmptyValueSpace.INSTANCE;
		} else {
			return vs;
		}
	}

	private String[] parse(String lexicalForm) {
		int lastAt = lexicalForm.lastIndexOf('@');
		if (lastAt != -1) {
			String string = lexicalForm.substring(0, lastAt);
			String languageTag = lexicalForm.substring(lastAt + 1);
			return new String[] { string, languageTag };
		} else {
			return new String[] { lexicalForm, null };
		}
	}

	/**
	 * Determine most specific datatype for input string
	 *
	 * @param string
	 *            input
	 * @return most specific {@link Datatype}
	 */
	public ElkDatatype determineDatatype(String string) {
		ElkDatatype retType = ElkDatatypeMap.get(PredefinedElkIri.RDF_PLAIN_LITERAL.get());
		if (stringAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_STRING.get());
		} else {
			return retType;
		}
		if (normalizedStringAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NORMALIZED_STRING.get());
		} else {
			return retType;
		}
		if (tokenAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_TOKEN.get());
		} else {
			return retType;
		}
		if (NMTokenAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NMTOCKEN.get());
		} else {
			return retType;
		}
		if (NameAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NAME.get());
		} else {
			return retType;
		}
		if (NCNameAutomaton.run(string)) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NCNAME.get());
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
	public ElkDatatype determineDatatype(Automaton pattern) {
		ElkDatatype retType = ElkDatatypeMap.get(PredefinedElkIri.RDF_PLAIN_LITERAL.get());
		if (!stringAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_STRING.get());
		} else {
			return retType;
		}
		if (!normalizedStringAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NORMALIZED_STRING.get());
		} else {
			return retType;
		}
		if (!tokenAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_TOKEN.get());
		} else {
			return retType;
		}
		if (!NMTokenAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NMTOCKEN.get());
		} else {
			return retType;
		}
		if (!NameAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NAME.get());
		} else {
			return retType;
		}
		if (!NCNameAutomaton.intersection(pattern).isEmpty()) {
			retType = ElkDatatypeMap.get(PredefinedElkIri.XSD_NCNAME.get());
		} else {
			return retType;
		}
		return retType;
	}

}
