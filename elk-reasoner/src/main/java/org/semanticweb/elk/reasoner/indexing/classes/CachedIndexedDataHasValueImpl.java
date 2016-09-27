/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.indexing.classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.indexing.SerializationContext;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.serialization.Deserializer;
import org.semanticweb.elk.serialization.Deserializers;
import org.semanticweb.elk.serialization.ElkSerializationException;

/**
 * Implements {@link CachedIndexedDataHasValue}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class CachedIndexedDataHasValueImpl
		extends
			CachedIndexedComplexClassExpressionImpl<CachedIndexedDataHasValue>
		implements
			CachedIndexedDataHasValue {

	private final ElkDataProperty property_;

	private final ElkLiteral filler_;

	private CachedIndexedDataHasValueImpl(ElkDataProperty property,
			ElkLiteral filler) {
		super(CachedIndexedDataHasValue.Helper.structuralHashCode(property,
				filler));
		this.property_ = property;
		this.filler_ = filler;
	}

	CachedIndexedDataHasValueImpl(ElkDataHasValue elkDataHasValue) {
		this((ElkDataProperty) elkDataHasValue.getProperty(),
				elkDataHasValue.getFiller());
	}

	@Override
	public final ElkDataProperty getRelation() {
		return property_;
	}

	@Override
	public final ElkLiteral getFiller() {
		return filler_;
	}

	@Override
	public final CachedIndexedDataHasValue structuralEquals(Object other) {
		return CachedIndexedDataHasValue.Helper.structuralEquals(this, other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(
			final ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		positiveOccurrenceNo += increment.positiveIncrement;
		negativeOccurrenceNo += increment.negativeIncrement;
		return true;
	}

	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedDataHasValue accept(
			CachedIndexedClassExpression.Filter filter) {
		return filter.filter(this);
	}

	private static final Deserializer<SerializationContext> DESERIALIZER = new Deserializer<SerializationContext>() {

		@Override
		public byte getSerialId() {
			return 11;
		}

		@Override
		public Object read(final DataInputStream input,
				final SerializationContext context)
				throws IOException, ElkSerializationException {

			final ElkIri iri = new ElkFullIri(input.readUTF());
			final ElkDataProperty property = context.getElkFactory()
					.getDataProperty(iri);

			final String lexicalForm = input.readUTF();
			final ElkDatatype datatype = context.getElkFactory()
					.getDatatype(new ElkFullIri(input.readUTF()));
			final ElkLiteral filler = context.getElkFactory()
					.getLiteral(lexicalForm, datatype);

			return context.getIndexedObjectFactory().getIndexedDataHasValue(
					context.getElkFactory().getDataHasValue(property, filler));
		}

	};

	static {
		Deserializers.register(DESERIALIZER);
	}

	@Override
	public Deserializer<SerializationContext> getDeserializer() {
		return DESERIALIZER;
	}

	@Override
	public void write(final DataOutputStream output)
			throws IOException, ElkSerializationException {

		output.writeByte(getDeserializer().getSerialId());

		output.writeUTF(getRelation().getIri().getFullIriAsString());

		output.writeUTF(getFiller().getLexicalForm());

		output.writeUTF(
				getFiller().getDatatype().getIri().getFullIriAsString());

	}

}