/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import java.io.StringReader;

import org.junit.Before;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.javacc.AbstractOwl2FunctionalStyleParser;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.datatypes.handlers.ElkDatatypeHandler;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;

/**
 * Abstract super class for low-level testing of {@link ValueSpace}s 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractValueSpaceTest {

	private final Owl2FunctionalStyleParserFactory parserFactory_ = new Owl2FunctionalStyleParserFactory();
	private final ElkPrefixDeclarations prefixes_ = new ElkPrefixDeclarationsImpl();

	@Before
	public void setUpPrefixes() {
		prefixes_.addPrefix(new ElkPrefix("xsd:", new ElkFullIri(
				"http://www.w3.org/2001/XMLSchema#")));
		prefixes_.addPrefix(new ElkPrefix("owl:", new ElkFullIri(
				"http://www.w3.org/2002/07/owl#")));
	}	
	
	protected boolean contains(String range1, String range2)
			throws ParseException {
		ValueSpace dataRange1 = dataRange(range1);
		ValueSpace dataRange2 = dataRange(range2);

		return dataRange1.contains(dataRange2);
	}
	
	/*
	 * we do need the javaCC parser here to parse specific OWL 2 expression
	 * (e.g., datatype restrictions) if we start getting CCEs here, that means
	 * the javaCC parser need to be obtained in other ways
	 */
	protected AbstractOwl2FunctionalStyleParser getJavaCCParser(
			StringReader reader) {
		return (AbstractOwl2FunctionalStyleParser) parserFactory_.getParser(
				reader, prefixes_);
	}

	/*
	 * parses the data range
	 */
	protected ValueSpace dataRange(String string) throws ParseException {
		AbstractOwl2FunctionalStyleParser parser = getJavaCCParser(new StringReader(
				string));
		ElkDataRange dataRange = parser.dataRange();

		return dataRange.accept(ElkDatatypeHandler.getInstance());
	}	
	
	protected boolean tryDataRange(String string) throws ParseException {
		try {
			dataRange(string);

			return true;
		} catch (ElkIndexingException e) {
			return false;
		}
	}	
}
