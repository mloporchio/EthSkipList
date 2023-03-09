import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * This program parses a list of Ethereum blocks (in compressed JSON format)
 * and computes statistics about transaction receipts.
 * More precisely, for each block we compute the following information:
 * 
 * 1) block identifier (i.e., its height);
 * 2) number of transactions in the block;
 * 3) number of logs in the block (we count all logs of all transactions);
 * 4) number of keys in the block (we count all keys for all logs);
 * 5) number of unique keys in the block (as keys can be repeated multiple times).
 * 
 * The output of this program is a CSV file with one line for each block.
 * Each line is formatted as follows:
 * 
 * <blockId>,<txCount>,<numLogs>,<numKeys>,<numDistKeys>
 * 
 * @author Matteo Loporchio
 */
public class ReceiptStats {
	public static final Gson gson = new Gson();
	public static final int BUFSIZE = 65536;

	public static void main(String[] args) {
		// Check and parse command-line arguments.
		if (args.length < 2) {
			System.err.println("Usage: ReceiptStats <inputFile> <outputFile>");
			System.exit(1);
		}
		final String inputFile = args[0], outputFile = args[1];
		// Read GZIP-compressed JSON file.
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
				int blockId = Integer.decode(b.number), txCount = 0, numLogs = 0, numKeys = 0;
				Set<String> keySet = new HashSet<>();
				// Examine all transactions in the block (if any).
				if (b.transactions != null) {
					txCount = b.transactions.size();
					// For each transaction, check its logs.
					for (Transaction t : b.transactions) {
						if (t.logs != null) {
							numLogs += t.logs.size();
							for (Log l : t.logs) {
								if (l.address == null) {
									System.err.printf("Null log address: blockId=%d, txId=%s, logIndex=%s\n", blockId, t.hash, l.logIndex);
								}
								else {
									numKeys += 1;
									keySet.add(l.address);
								}
								if (l.topics == null)  {
									System.err.printf("Null log topics: blockId=%d, txId=%s, logIndex=%s\n", blockId, t.hash, l.logIndex);
								}	
								else {
									numKeys += l.topics.size();
									keySet.addAll(l.topics);
								}
							}
						}
					}
				}
				out.printf("%d,%d,%d,%d,%d\n", blockId, txCount, numLogs, numKeys, keySet.size());
			}
			reader.endArray();
		}
		catch (Exception e) {
			System.err.printf("Error: %s\n", e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
