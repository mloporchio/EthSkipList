import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * This program parses a list of Ethereum blocks (in compressed JSON format)
 * and extracts the block height and the corresponding logsBloom field.
 * The program outputs a CSV file where each row contains the following fields:
 * 
 * 	<code>blockId,timestamp,logsBloom</code>
 * 
 * @author Matteo Loporchio
 */
public class FilterReader {
	public static final Gson gson = new Gson();
	public static final int BUFSIZE = 65536;
	
	public static void main(String[] args) {
		// Check and parse command-line arguments.
		if (args.length < 2) {
			System.err.println("Usage: FilterReader <inputFile> <outputFile>");
			System.exit(1);
		}
		final String inputFile = args[0], outputFile = args[1];
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
				out.printf("%d,%d,%s\n", blockId, timestamp, b.logsBloom);
			}
			reader.endArray();
		}
		catch (Exception e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
		}
	}

}
