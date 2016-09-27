/*-
 * #%L
 * ELK Common Utilities
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
package org.semanticweb.elk.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Deserializers {

	public static final byte LIST_TERMINATING_SERIAL_ID = 0;

	private Deserializers() {
		// Preventing subclasses of a utility class.
	}

	private static final Map<Byte, Deserializer<?>> DESERIALIZERS = new HashMap<Byte, Deserializer<?>>();

	public static void register(final Deserializer<?> deserializer) {
		if (deserializer.getSerialId() == LIST_TERMINATING_SERIAL_ID) {
			throw new IllegalArgumentException(
					"Cannot register deserializer with reserved serial ID: "
							+ LIST_TERMINATING_SERIAL_ID);
		}
		final Deserializer<?> previous = DESERIALIZERS
				.put(deserializer.getSerialId(), deserializer);
		if (previous != null) {
			throw new IllegalStateException(
					"Multiple deserializers registered with the same serial ID: "
							+ deserializer.getSerialId());
		}
	}

	public static Deserializer<?> get(final byte serialId)
			throws ElkSerializationException {
		final Deserializer<?> result = DESERIALIZERS.get(serialId);
		if (result == null) {
			throw new ElkSerializationException(
					"No deserializers registered with the serial ID: "
							+ serialId);
		}
		return result;
	}

	public static <DC, T extends Deserializable<DC>> T read(
			final DataInputStream input, final Class<T> expectedType,
			final DC deserializationContext)
			throws IOException, ElkSerializationException {

		final byte serialId = input.readByte();
		Object object;
		try {
			@SuppressWarnings("unchecked")
			final Deserializer<DC> deserializer = (Deserializer<DC>) DESERIALIZERS
					.get(serialId);
			if (deserializer == null) {
				throw new ElkSerializationException(
						"No deserializers registered with the serial ID "
								+ serialId + ". Expected deserializer for "
								+ expectedType);

			}
			object = deserializer.read(input, deserializationContext);
		} catch (final ClassCastException e) {
			throw new ElkSerializationException(
					"Wrong context passed to deserializer registered with serial ID "
							+ serialId,
					e);
		}
		if (!expectedType.isInstance(object)) {
			throw new ElkSerializationException("Expected a " + expectedType
					+ " but got a " + object.getClass());
		}
		return expectedType.cast(object);
	}

	public static <DC, T extends Deserializable<DC>> List<T> readList(
			final DataInputStream input, final Class<T> expectedType,
			final DC deserializationContext)
			throws IOException, ElkSerializationException {

		final List<T> result = new ArrayList<T>();

		byte serialId;
		while ((serialId = input.readByte()) != LIST_TERMINATING_SERIAL_ID) {

			Object object;
			try {
				@SuppressWarnings("unchecked")
				final Deserializer<DC> deserializer = (Deserializer<DC>) DESERIALIZERS
						.get(serialId);
				if (deserializer == null) {
					throw new ElkSerializationException(
							"No deserializers registered with the serial ID "
									+ serialId + ". Expected deserializer for "
									+ expectedType);

				}
				object = deserializer.read(input, deserializationContext);
			} catch (final ClassCastException e) {
				throw new ElkSerializationException(
						"Wrong context passed to deserializer registered with serial ID "
								+ serialId,
						e);
			}
			if (!expectedType.isInstance(object)) {
				throw new ElkSerializationException("Expected a " + expectedType
						+ " but got a " + object.getClass());
			}

			result.add(expectedType.cast(object));
		}

		return result;
	}

	public static <DC> void writeList(
			final List<? extends Deserializable<DC>> list,
			final DataOutputStream output)
			throws ElkSerializationException, IOException {

		for (final Deserializable<DC> element : list) {
			element.write(output);
		}

		output.writeByte(LIST_TERMINATING_SERIAL_ID);

	}

}
