/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkAnnotationImpl implements ElkAnnotation {

	private final ElkAnnotationProperty annProperty;
	private final ElkAnnotationValue annValue;
	
	ElkAnnotationImpl(ElkAnnotationProperty annProperty, ElkAnnotationValue annValue) {
		this.annProperty = annProperty;
		this.annValue = annValue;
	}
	
	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		// TODO extend the visitor
		return null;
	}

	@Override
	public ElkAnnotationProperty getProperty() {
		return annProperty;
	}

	@Override
	public ElkAnnotationValue getValue() {
		return annValue;
	}
}
