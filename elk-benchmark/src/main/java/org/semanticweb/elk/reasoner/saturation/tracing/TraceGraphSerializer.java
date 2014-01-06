/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.semanticweb.elk.io.IOUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TraceGraphSerializer {

	public static void serialize(final TraceGraph graph, final String fileName) throws IOException {
		FileOutputStream stream = null;
		
		try {
			File file = new File(fileName);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			stream = new FileOutputStream(new File(fileName));
			serialize(graph, stream);
		} finally {
			if (stream != null) {
				stream.flush();
				stream.close();
			}
		}
	}

	public static TraceGraph deserialize(final String fileName) throws IOException {
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(new File(fileName));

			return deserialize(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	public static void serialize(final TraceGraph graph, final OutputStream stream) throws IOException {
		ObjectOutputStream out = null;
		
		try {
			out = new ObjectOutputStream(stream);
			out.writeObject(graph);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static TraceGraph deserialize(final InputStream stream) throws IOException {
		ObjectInputStream in = null;
		TraceGraph graph = null;
		
		try {
			in = new ObjectInputStream(stream);
			graph = (TraceGraph) in.readObject();
		}
		catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return graph;
	}
}
