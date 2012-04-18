package org.semanticweb.elk.owl.printers;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.ElkTestAxiomProcessor;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;

/**
 * Test for the printer which uses this concrete implementation of the OWL model.
 * Unfortunately, it's still abstract because we also need some implementation of the parser
 * which live in other modules
 * 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public abstract class AbstractImplOwl2FunctionalSyntaxPrinterTest extends ModelOwl2FunctionalSyntaxPrinterTest {

	@Override
	protected Set<? extends ElkObject> getOriginalElkObjects() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("owl2primer.owl");
		
		assertNotNull(input);
		
		return parseAxioms(new InputStreamReader(input));
	}

	@Override
	protected Set<? extends ElkObject> loadPrintedElkObjects(String input) {
		String ontology = " Ontology(<http://example.com/owl/> \n" + input + "\n)"; 
		
		return parseAxioms(new StringReader(ontology));
	}
	
	protected Set<? extends ElkObject> parseAxioms(Reader reader) {
		Owl2Parser parser = instantiateParser(reader);
		ElkTestAxiomProcessor counter = new ElkTestAxiomProcessor();
		/*ElkPrefixDeclarations prefixDeclarations = new ElkPrefixDeclarationsImpl();
		
		prefixDeclarations.addOwlDefaultPrefixes();
		parser.setPrefixDeclarations(prefixDeclarations);*/
		
		try {
			parser.parseOntology(counter);
		} catch (Owl2ParseException e) {
			throw new RuntimeException("Failed to load axioms for testing", e); 
		}
		
		return counter.getAllAxioms();
	}
	
	protected abstract Owl2Parser instantiateParser(Reader reader);
}
