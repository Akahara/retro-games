package fr.wonder.display.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferUtils {

	public static ByteBuffer readAllToBuffer(InputStream is) throws IOException {
		byte[] buf = new byte[1024];
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		int read;
		while((read = is.read(buf)) != -1) {
			buffer.put(buf, 0, read);
			if(buffer.position() == buffer.capacity()) {
				ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() + 4*1024);
				newBuffer.put(buffer);
				buffer = newBuffer;
			}
		}
		buffer.position(0);
		return buffer;
	}
	
	public static byte[] readAll(InputStream is) throws IOException {
		byte[] buf = new byte[1024];
		int totalRead = 0;
		while(true) {
			int read = is.read(buf, totalRead, 1024);
			totalRead += read;
			if(totalRead == buf.length) {
				byte[] newBuf = new byte[buf.length + 4096];
				for(int i = 0; i < totalRead; i++)
					newBuf[i] = buf[i];
				buf = newBuf;
			}
			if(read != 1024)
				break;
		}
		byte[] finalBuf = new byte[totalRead];
		for(int i = 0; i < totalRead; i++)
			finalBuf[i] = buf[i];
		return finalBuf;
	}
	
}
