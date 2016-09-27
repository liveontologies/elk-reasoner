/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.SerializationContext;
import org.semanticweb.elk.reasoner.indexing.classes.BaseModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.serialization.Deserializable;
import org.semanticweb.elk.serialization.Deserializers;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.testing.VoidTestOutput;

@RunWith(PolySuite.class)
public class SerializationRoundtripTest {

	public static final String INPUT_DATA_LOCATION = "classification_test_input";

	private static final ElkObject.Factory ELK_FACTORY = new ElkObjectEntityRecyclingFactory();

	protected final TestManifest<UrlTestInput> manifest_;

	protected ModifiableOntologyIndex index_;
	protected ElkPolarityExpressionConverter converter_;
	protected SerializationContext serializationContext_;

	public SerializationRoundtripTest(
			final TestManifest<UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Before
	public void setUp() {
		this.index_ = new DirectIndex(ELK_FACTORY);
		this.converter_ = new ElkPolarityExpressionConverterImpl(ELK_FACTORY,
				new UpdatingModifiableIndexedObjectFactory(
						new BaseModifiableIndexedObjectFactory(), index_,
						OccurrenceIncrement.getDualIncrement(1)),
				index_);
		this.serializationContext_ = new SerializationContext(ELK_FACTORY,
				new ResolvingModifiableIndexedObjectFactory(index_));
	}

	@Test
	public void testRoundtrip() throws IOException, Owl2ParseException {

		InputStream ontologyIS = null;

		try {

			ontologyIS = manifest_.getInput().getUrl().openStream();

			final Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
					.getParser(ontologyIS);
			parser.accept(new Owl2ParserAxiomProcessor() {

				@Override
				public void visit(final ElkAxiom elkAxiom)
						throws Owl2ParseException {
					elkAxiom.accept(AXIOM_VISITOR);
				}

				@Override
				public void visit(final ElkPrefix elkPrefix)
						throws Owl2ParseException {
					// Empty.
				}

				@Override
				public void finish() throws Owl2ParseException {
					// Empty.
				}

			});

		} finally {
			IOUtils.closeQuietly(ontologyIS);
		}

	}

	private void roundtrip(final Object obj) {

		if (!(obj instanceof Deserializable)
				|| !(obj instanceof CachedIndexedObject)) {
			return;
		}

		final Deserializable<SerializationContext> deserializable = (Deserializable<SerializationContext>) obj;

		final ByteArrayOutputStream serialized = new ByteArrayOutputStream();

		try {

			deserializable.write(new DataOutputStream(serialized));

			final DataInputStream input = new DataInputStream(
					new ByteArrayInputStream(serialized.toByteArray()));

			final Object deserialized = Deserializers.read(input,
					Deserializable.class, serializationContext_);

			Assert.assertNotNull(
					"expected: <" + deserializable + ">, actual: <"
							+ deserialized + ">",
					index_.resolve((CachedIndexedObject<?>) deserialized));

		} catch (final Exception e) {
			throw new ElkRuntimeException(e);
		}

	}

	protected final ElkAxiomVisitor<Void> AXIOM_VISITOR = new DummyElkAxiomVisitor<Void>() {

		@Override
		public Void visit(final ElkClassAssertionAxiom axiom) {
			roundtrip(axiom.getClassExpression().accept(converter_));
			roundtrip(axiom.getIndividual().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkDifferentIndividualsAxiom axiom) {
			for (final ElkIndividual individual : axiom.getIndividuals()) {
				roundtrip(individual.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkDataPropertyAssertionAxiom axiom) {
			roundtrip(axiom.getSubject().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkNegativeDataPropertyAssertionAxiom axiom) {
			roundtrip(axiom.getSubject().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkNegativeObjectPropertyAssertionAxiom axiom) {
			roundtrip(axiom.getObject().accept(converter_));
			roundtrip(axiom.getProperty().accept(converter_));
			roundtrip(axiom.getSubject().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkObjectPropertyAssertionAxiom axiom) {
			roundtrip(axiom.getObject().accept(converter_));
			roundtrip(axiom.getProperty().accept(converter_));
			roundtrip(axiom.getSubject().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkSameIndividualAxiom axiom) {
			for (final ElkIndividual individual : axiom.getIndividuals()) {
				roundtrip(individual.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkDisjointClassesAxiom axiom) {
			for (final ElkClassExpression classExpression : axiom
					.getClassExpressions()) {
				roundtrip(classExpression.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkDisjointUnionAxiom axiom) {
			for (final ElkClassExpression classExpression : axiom
					.getClassExpressions()) {
				roundtrip(classExpression.accept(converter_));
			}
			roundtrip(axiom.getDefinedClass().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkEquivalentClassesAxiom axiom) {
			for (final ElkClassExpression classExpression : axiom
					.getClassExpressions()) {
				roundtrip(classExpression.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkSubClassOfAxiom axiom) {
			roundtrip(axiom.getSubClassExpression().accept(converter_));
			roundtrip(axiom.getSuperClassExpression().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkDataPropertyDomainAxiom axiom) {
			roundtrip(axiom.getDomain().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkHasKeyAxiom axiom) {
			roundtrip(axiom.getClassExpression().accept(converter_));
			for (final ElkObjectPropertyExpression propertyExpression : axiom
					.getObjectPropertyExpressions()) {
				roundtrip(propertyExpression.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkAsymmetricObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkDisjointObjectPropertiesAxiom axiom) {
			for (final ElkObjectPropertyExpression propertyExpression : axiom
					.getObjectPropertyExpressions()) {
				roundtrip(propertyExpression.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkEquivalentObjectPropertiesAxiom axiom) {
			for (final ElkObjectPropertyExpression propertyExpression : axiom
					.getObjectPropertyExpressions()) {
				roundtrip(propertyExpression.accept(converter_));
			}
			return null;
		}

		@Override
		public Void visit(final ElkFunctionalObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkInverseFunctionalObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkInverseObjectPropertiesAxiom axiom) {
			roundtrip(axiom.getFirstObjectPropertyExpression()
					.accept(converter_));
			roundtrip(axiom.getSecondObjectPropertyExpression()
					.accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkIrreflexiveObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkObjectPropertyDomainAxiom axiom) {
			roundtrip(axiom.getDomain().accept(converter_));
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkObjectPropertyRangeAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			roundtrip(axiom.getRange().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkReflexiveObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkSubObjectPropertyOfAxiom axiom) {
			// TODO: We need this !!!
			// roundtrip(axiom.getSubObjectPropertyExpression().accept(converter_));
			roundtrip(axiom.getSuperObjectPropertyExpression()
					.accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkSymmetricObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

		@Override
		public Void visit(final ElkTransitiveObjectPropertyAxiom axiom) {
			roundtrip(axiom.getProperty().accept(converter_));
			return null;
		}

	};

	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, SerializationRoundtripTest.class, "owl",
				"expected",
				new TestManifestCreator<UrlTestInput, VoidTestOutput, VoidTestOutput>() {
					@Override
					public TestManifestWithOutput<UrlTestInput, VoidTestOutput, VoidTestOutput> create(
							final URL input, final URL output)
							throws IOException {
						// don't need an expected output for these tests
						return new ReasoningTestManifest<VoidTestOutput, VoidTestOutput>(
								input, null);
					}
				});
	}

}
