package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDeclarationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing"
 * >declarations of built-in entities<a> in the OWL 2 specification that are
 * implicitly present in every OWL 2 ontology (see Table 5 in the link).
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public enum PredefinedElkDeclaration implements ElkDeclarationAxiom {

	OWL_THING_DECLARATION(PredefinedElkClass.OWL_THING),

	OWL_NOTHING_DECLARATION(PredefinedElkClass.OWL_NOTHING),

	OWL_TOP_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_TOP_OBJECT_PROPERTY),

	OWL_BOTTOM_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_BOTTOM_OBJECT_PROPERTY),

	;

	private final ElkEntity entity_;

	private PredefinedElkDeclaration(ElkEntity entity) {
		this.entity_ = entity;
	}

	@Override
	public ElkEntity getEntity() {
		return entity_;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDeclarationAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
