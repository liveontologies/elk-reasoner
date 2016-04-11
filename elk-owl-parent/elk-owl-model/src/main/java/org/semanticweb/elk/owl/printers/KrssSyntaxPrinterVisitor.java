package org.semanticweb.elk.owl.printers;

import java.io.IOException;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkIris;
import org.semanticweb.elk.owl.visitors.AbstractElkObjectVisitor;

public class KrssSyntaxPrinterVisitor extends AbstractElkObjectVisitor<Void> {

	private final Appendable writer;

	public KrssSyntaxPrinterVisitor(final Appendable writer) {
		this.writer = writer;
	}

	@Override
	protected Void defaultVisit(final ElkObject elkObject) {
		throw new PrintingException(
				"Currently not supported by KRSS printer: " + elkObject);
	}

	@Override
	public Void visit(final ElkSubClassOfAxiom elkSubClassOfAxiom) {
		write("(implies ");
		write(elkSubClassOfAxiom.getSubClassExpression());
		write(' ');
		write(elkSubClassOfAxiom.getSuperClassExpression());
		write(")\n");
		return null;
	}

	@Override
	public Void visit(
			final ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		write("(equivalent ");
		write(elkEquivalentClassesAxiom.getClassExpressions());
		write(")\n");
		return null;
	}

	@Override
	public Void visit(
			final ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		write("(role-inclusion ");
		write(elkSubObjectPropertyOfAxiom.getSubObjectPropertyExpression());
		write(' ');
		write(elkSubObjectPropertyOfAxiom.getSuperObjectPropertyExpression());
		write(")\n");
		return null;
	}

	@Override
	public Void visit(
			final ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		write("(transitive ");
		write(elkTransitiveObjectPropertyAxiom.getProperty());
		write(")\n");
		return null;
	}

	@Override
	public Void visit(final ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		write("(some ");
		write(elkObjectSomeValuesFrom.getProperty());
		write(' ');
		write(elkObjectSomeValuesFrom.getFiller());
		write(')');
		return null;
	}

	@Override
	public Void visit(final ElkObjectIntersectionOf elkObjectIntersectionOf) {
		write("(and ");
		write(elkObjectIntersectionOf.getClassExpressions());
		write(')');
		return null;
	}

	@Override
	public Void visit(final ElkClass elkClass) {
		write(elkClass);
		return null;
	}

	@Override
	public Void visit(final ElkObjectProperty elkObjectProperty) {
		write(elkObjectProperty);
		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyChain elkObjectPropertyChain) {
		write("(compose ");
		write(elkObjectPropertyChain.getObjectPropertyExpressions());
		write(')');
		return null;
	}

	@Override
	public Void visit(final ElkFullIri iri) {
		write(iri);
		return null;
	}

	@Override
	public Void visit(final ElkAbbreviatedIri iri) {
		write(iri);
		return null;
	}

	protected final void write(final char ch) {
		try {
			writer.append(ch);
		} catch (final IOException e) {
			throw new PrintingException(e.getMessage(), e.getCause());
		}
	}

	protected final void write(final String string) {
		try {
			writer.append(string);
		} catch (final IOException e) {
			throw new PrintingException(e.getMessage(), e.getCause());
		}
	}
	
	protected final void write(final ElkIri iri) {
		if (PredefinedElkIris.OWL_THING.equals(iri)) {
			write("top");
		} else {
			write(iri.getFullIriAsString());
		}
	}

	protected final void write(final ElkEntity elkEntity) {
		write(elkEntity.getIri());
	}

	protected final void write(final ElkObject elkObject) {
		elkObject.accept(this);
	}

	protected final void write(final Iterable<? extends ElkObject> elkObjects) {
		boolean first = true;
		for (final ElkObject elkObject : elkObjects) {
			if (!first) {
				write(' ');
			} else {
				first = false;
			}
			write(elkObject);
		}
	}

}
