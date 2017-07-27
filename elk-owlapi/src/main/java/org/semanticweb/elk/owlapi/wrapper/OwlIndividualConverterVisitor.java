package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * An implementation of the visitor pattern for OWL individuals to convert OWL
 * individuals to ELK individuals.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlIndividualConverterVisitor
		implements OWLIndividualVisitorEx<ElkIndividual> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlIndividualConverterVisitor INSTANCE_ = new OwlIndividualConverterVisitor();

	public static OwlIndividualConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlIndividualConverterVisitor() {
	}

	@Override
	public ElkAnonymousIndividual visit(
			OWLAnonymousIndividual owlAnonymousIndividual) {
		return CONVERTER.convert(owlAnonymousIndividual);
	}

	@Override
	public ElkNamedIndividual visit(OWLNamedIndividual owlNamedIndividual) {
		return CONVERTER.convert(owlNamedIndividual);
	}

}
