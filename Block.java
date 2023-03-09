import java.util.List;

/**
 * Represents a generic block of the Ethereum blockchain.
 * 
 * @author Matteo Loporchio
 */
public class Block {
	/**
	 * Height of the block.
	 */
	public final String number;

	/**
	 * Hexadecimal encoding of the logsBloom filter of the block.
	 */
	public final String logsBloom;

	/**
	 * Timestamp of the block.
	 */
	public final String timestamp;

	/**
	 * List of transactions included in the block.
	 */
	public final List<Transaction> transactions;
	
	/**
	 * Constructs a new Ethereum block.
	 * @param number height of the block
	 * @param logsBloom Bloom filter summarizing events in the block
	 * @param timestamp timestamp of the block
	 * @param transactions list of transactions included in the block
	 */
	public Block(String number, String logsBloom, String timestamp, List<Transaction> transactions) {
		this.number = number;
		this.logsBloom = logsBloom;
		this.timestamp = timestamp;
		this.transactions = transactions;
	}
}