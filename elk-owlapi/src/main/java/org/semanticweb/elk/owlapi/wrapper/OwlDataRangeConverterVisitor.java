package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;

/**
 * An implementation of the visitor pattern for OWL data ranges to convert OWL
 * data ranges to ELK data ranges.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlDataRangeConverterVisitor
		implements OWLDataRangeVisitorEx<ElkDataRange> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlDataRangeConverterVisitor INSTANCE_ = new OwlDataRangeConverterVisitor();

	public static OwlDataRangeConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlDataRangeConverterVisitor() {
	}

	@Override
	public ElkDataComplementOf visit(OWLDataComplementOf owlDataComplementOf) {
		return CONVERTER.convert(owlDataComplementOf);
	}

	@Override
	public ElkDataIntersectionOf visit(
			OWLDataIntersectionOf owlDataIntersectionOf) {
		return CONVERTER.convert(owlDataIntersectionOf);
	}

	@Override
	public ElkDataOneOf visit(OWLDataOneOf owlDataOneOf) {
		return CONVERTER.convert(owlDataOneOf);
	}

	@Override
	public ElkDatatype visit(OWLDatatype owlDatatype) {
		return CONVERTER.convert(owlDatatype);
	}

	@Override
	public ElkDatatypeRestriction visit(
			OWLDatatypeRestriction owlDatatypeRestriction) {
		return CONVERTER.convert(owlDatatypeRestriction);
	}

	@Override
	public ElkDataUnionOf visit(OWLDataUnionOf owlDataUnionOf) {
		return CONVERTER.convert(owlDataUnionOf);
	}

}
