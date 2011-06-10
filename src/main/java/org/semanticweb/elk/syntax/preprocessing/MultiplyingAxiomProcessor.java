package org.semanticweb.elk.syntax.preprocessing;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;

public class MultiplyingAxiomProcessor implements ElkAxiomProcessor {
	
	protected ElkAxiomProcessor subProcessor;
	protected int multiplicity;
	protected RenamingExpressionVisitor renamingVisitor;
	
	public MultiplyingAxiomProcessor(ElkAxiomProcessor subProcessor, int multiplicity) {
		this.subProcessor = subProcessor;
		this.multiplicity = multiplicity;
		renamingVisitor = new RenamingExpressionVisitor("");
	}

	public void process(ElkAxiom elkAxiom) {
		subProcessor.process(elkAxiom);
		for (int i=1; i<multiplicity; ++i) {
			renamingVisitor.setPostfix("X" + i);
			subProcessor.process(elkAxiom.accept(renamingVisitor));
		}
	}

}
