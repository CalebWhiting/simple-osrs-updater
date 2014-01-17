package edu.revtek.lang.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Caleb Whiting
 */
public class IOUtil {

	public static void readAndWrite(InputStream in, OutputStream out) {
		byte[] buffer = new byte[2048];
		int read;
		try {
			while ((read = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] read(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		readAndWrite(in, out);
		byte[] bytes = out.toByteArray();
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static byte[] getBytes(Class<?> clazz) throws IOException {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(
				clazz.getName().replaceAll("\\.", "/") + ".class");
		return sun.misc.IOUtils.readFully(stream, stream.available(), true);
	}

}
