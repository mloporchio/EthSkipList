import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * This program parses a list of Ethereum blocks (in compressed JSON format)
 * and computes the number of ones in the corresponding logsBloom field.
 * The program outputs a CSV file where each row contains the following fields:
 * 
 * 	<code>blockId,timestamp,numOnes</code>
 * 
 * @author Matteo Loporchio
 */
public class FilterStats {
	public static final Gson gson = new Gson();
	public static final int BUFSIZE = 1048576;
	
	public static void main(String[] args) {
		// Check and parse command-line arguments.
		if (args.length < 2) {
			System.err.println("Usage: FilterStats <inputFile> <outputFile>");
			System.exit(1);
		}
		final String inputFile = args[0], outputFile = args[1];
		long start = System.currentTimeMillis();
		// Reads GZIP-compressed JSON file.
		try (
			JsonReader reader = new JsonReader(
					new InputStreamReader(
							new GZIPInputStream(
									new FileInputStream(inputFile), BUFSIZE)));
			PrintWriter out = new PrintWriter(outputFile);
		) {
			reader.beginArray();
			while (reader.hasNext()) {
				// Deserialize block and extract all fields of interest.
				Block b = gson.fromJson(reader, Block.class);
				int blockId = Integer.decode(b.number);
				long timestamp = Long.decode(b.timestamp);
				byte[] filter = fromHex(b.logsBloom.substring(2));
				int numOnes = countOnes(filter);
				out.printf("%d,%d,%s\n", blockId, timestamp, numOnes);
			}
			reader.endArray();
		}
		catch (Exception e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
		}
		long end = System.currentTimeMillis();
		System.out.printf("Elapsed time:\t%d ms\n", end-start);
	}
	
	/**
	 *	Converts a hexadecimal string to an array of bytes.
	 *	@param s the hexadecimal string
	 * 	@return	an array of bytes 
	 */
	public static byte[] fromHex(String s) {
		int len = s.length();
		assert len % 2 == 0;
		// Allocate 1 byte per 2 hex characters
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			// Convert each character into a integer (base-16), then bit-shift into place
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	/**
	 *  Counts the number of bits equal to 1 in the given array of bytes.
	 *  @param data the array of bytes
	 *  @return number of ones in the array
	 */
	public static int countOnes(byte[] data) {
		int result = 0;
		for (int i = 0; i < data.length; i++)
			result += Integer.bitCount(data[i] & 0xff);
		return result;
	}
}
