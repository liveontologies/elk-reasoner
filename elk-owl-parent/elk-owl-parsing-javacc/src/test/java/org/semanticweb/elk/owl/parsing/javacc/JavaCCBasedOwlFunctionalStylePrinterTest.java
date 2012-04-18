package org.semanticweb.elk.owl.parsing.javacc;

import java.io.Reader;

import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.printers.AbstractImplOwl2FunctionalSyntaxPrinterTest;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class JavaCCBasedOwlFunctionalStylePrinterTest extends AbstractImplOwl2FunctionalSyntaxPrinterTest{

	@Override
	protected Owl2Parser instantiateParser(Reader reader) {
		return new Owl2FunctionalStyleParser(reader);
	}
}