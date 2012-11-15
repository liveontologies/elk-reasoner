/**
 * 
 */
package org.semanticweb.elk.benchmark.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LoaderThatRemembersAxioms extends Owl2StreamLoader implements ElkAxiomProcessor {

	private final List<ElkAxiom> loaded_ = new LinkedList<ElkAxiom>();
	
	private ElkAxiomProcessor processor_;
	
	public LoaderThatRemembersAxioms(Owl2ParserFactory parserFactory, File file)
			throws FileNotFoundException {
		super(parserFactory, file);
	}
	
	@Override
	public Loader getLoader(ElkAxiomProcessor axiomLoader) {
		processor_ = axiomLoader;
		
		return super.getLoader(this);
	}


	@Override
	public void visit(ElkAxiom elkAxiom) {
		processor_.visit(elkAxiom);
		loaded_.add(elkAxiom);
	}


	public List<ElkAxiom> getLoadedAxioms() {
		return loaded_;
	}
}