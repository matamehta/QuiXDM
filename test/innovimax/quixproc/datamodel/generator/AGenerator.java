package innovimax.quixproc.datamodel.generator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public abstract class AGenerator {

	public enum FileExtension {
		XML, HTML, JSON, YAML
	}

	public enum Variation {
		NO_VARIATION, SEQUENTIAL, RANDOM;

	}

	public enum Encoding {

		SEVEN_BITS, // 1 byte (minus the upper bit) is one char
		EIGHT_BITS, // 1 byte (including upper bit) is one char
		TWO_BYTES, // 2 bytes per char
		UNICODE
	}

	public enum FileProperty {
		REPEATABLE/*
					 * repeated part of the content makes it potentially
					 * infinite
					 */, SYMMETRIC/*
								 * the file is symmetric to its middle, usually
								 * you must provide a length
								 */, CONTENT_VARIATION/*
													 * content is varying from
													 * one occurrence to the
													 * other
													 */,
	}

	private final FileExtension type;
	protected final Random random = new Random();

	public enum Unit {
		BYTE(1, "B"), KBYTE(1000, "KB"), MBYTE(1000000, "MB"), GBYTE(1000000000, "GB"), TBYTE(1000000000000l, "TB");
		private long value;
		private String display;

		Unit(long value, String display) {
			this.value = value;
			this.display = display;
		}

		public long value() {
			return this.value;
		}

		public String display() {
			return this.display;
		}
	}
	/*
	 * protected static AGenerator instance(FileExtension type) { switch (type)
	 * { case HTML: break; case JSON: break; case XML: return new
	 * AXMLGenerator(); break; case YAML: break; default: break;
	 * 
	 * } return null; }
	 */

	public void generate(File output, long size) throws IOException {
		generate(output, size, Unit.BYTE, Variation.NO_VARIATION);
	}

	public void generate(File output, long size, Unit unit) throws IOException {
		generate(output, size, unit, Variation.NO_VARIATION);
	}

	public abstract byte[] applyVariation(Variation variation, byte[][] bs, int pos);

	public void generate(File output, long size, Unit unit, Variation variation) throws IOException {
		output.getParentFile().mkdirs();
		final long total = size * unit.value();
		FileOutputStream fos = new FileOutputStream(output);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1000 * 1000);
		final byte[] start = getStart();
		final byte[][] patterns = getPatterns();
		final byte[] end = getEnd();
		// ensure that at minimum the size is start+end
		long current_size = start.length + end.length;
		int current_pattern = -1;
		// write the start pattern
		bos.write(start);
		// System.out.println(current_size);
		while (notFinished(current_size, current_pattern, total)) {
			// move to next pattern
			current_pattern = updatePattern(current_pattern);
			// System.out.println(current_size);
			// write the alternate pattern
			bos.write(applyVariation(variation, patterns, current_pattern));
			// update the size
			current_size = updateSize(current_size, current_pattern);
		}
		// write the end pattern
		bos.write(end);
		bos.flush();
		bos.close();
		fos.close();
	}

	public InputStream getInputStream(long size, Unit unit, Variation variation) {
		return new GeneratorInputStream(size, unit, variation);
	}

	// protected abstract byte[] applyRandom(byte[][] bs, int pos);

	protected abstract boolean notFinished(long current_size, int current_pattern, long total);

	protected abstract int updatePattern(int current_pattern);

	protected abstract long updateSize(long current_size, int current_pattern);

	protected abstract byte[] getEnd();

	protected abstract byte[][] getPatterns();

	protected abstract byte[] getStart();

	private enum InputStreamState {
		START, CURRENT, END
	};

	public class GeneratorInputStream extends InputStream {

		final byte[] start = getStart();
		final byte[][] patterns = getPatterns();
		final byte[] end = getEnd();
		// ensure that at minimum the size is start+end
		long current_size = start.length + end.length;
		int current_pattern = -1;
		int offset = -1;
		byte[] buffer = null;
		InputStreamState state;
		final long total;
		final Variation variation;

		public GeneratorInputStream(long size, Unit unit, Variation variation) {
			state = InputStreamState.START;
			buffer = start;
			total = size * unit.value();
			this.variation = variation;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			// System.out.println("off : "+off+" ; len : "+len+" :
			// "+display(b));
			if (b == null) {
				throw new NullPointerException();
			}
			if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			}
			if (len == 0) {
				return 0;
			}
			if (buffer == null)
				return -1;
			int total = 0;
			if (offset + 1 == buffer.length) {
				// System.out.println("offset : "+offset);
				// System.out.println("length : "+len);
				update();
				// System.out.println("offset : "+offset);
				// System.out.println("length : "+len);
				if (buffer == null)
					return -1;
			}
			do {
				// System.out.println("offset : "+offset);
				// System.out.println("length : "+len);
				int length = Math.min(buffer.length - (offset + 1), len - total);
				// System.out.println("length : "+length);
				System.arraycopy(buffer, offset + 1, b, off + total, length);
				total += length;
				// System.out.println("total : "+total);
				offset = offset + 1 + length - 1;
				// System.out.println("offset : "+offset);
				if (offset == buffer.length - 1)
					update();
				if (total == len) {
					// System.out.println("length : "+len+" : "+display(b));
					return len;

				}
				if (buffer == null) {
					// System.out.println("length : "+total+" : "+display(b));
					return total;
				}
			} while (true);
		}

		private void update() {
			// offset == buffer.length
			switch (state) {
			case START:
				if (notFinished(current_size, current_pattern, total)) {
					// move to next pattern
					current_pattern = updatePattern(current_pattern);
					// System.out.println(current_size);
					// write the alternate pattern
					buffer = applyVariation(variation, patterns, current_pattern);
					// update the size
					current_size = updateSize(current_size, current_pattern);
					offset = -1;
					return;
				}
				state = InputStreamState.CURRENT;
				// FALL-THROUGH
			case CURRENT:
				buffer = end;
				offset = -1;
				state = InputStreamState.END;
				return;
			case END:
				buffer = null;
			}
		}

		@Override
		public int read() throws IOException {
			if (buffer == null)
				return -1;
			offset++;
			if (offset < buffer.length) {
				int c = buffer[offset];
				// System.out.println("read : "+display((byte) (c & 0xFF)));
				return c;
			}
			update();
			if (buffer == null)
				return -1;
			offset++;
			int c = buffer[offset];
			// System.out.println("read : "+display((byte) (c & 0xFF)));
			return c;
		}

	}

	protected static String display(byte b) {
		return Integer.toHexString(b & 0xFF) + "(" + Character.toString((char) (b & 0xFF)) + ")";
	}

	protected static String display(byte[] bytea) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < bytea.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(display(bytea[i]));
		}
		sb.append("]");
		return sb.toString();
	}

	protected AGenerator(FileExtension type) {
		this.type = type;
	}

}