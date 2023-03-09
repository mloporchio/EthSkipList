import java.util.List;

/**
 * Represents an Ethereum transaction.
 * 
 * @author Matteo Loporchio
 */
public class Transaction {
	/**
	 * Transaction hash (i.e., the unique transaction identifier).
	 */
	public final String hash;

	/**
	 * Height of the block the transaction is included in.
	 */
	public final String blockNumber;

	/**
	 * Hash of the block the transaction is included in.
	 */
	public final String blockHash;

	/**
	 * List of transaction logs.
	 */
	public final List<Log> logs;
	
	/**
	 * Constructs a new transaction.
	 * @param hash transaction identifier
	 * @param blockNumber number of the block
	 * @param blockHash hash of the block
	 * @param logs list of transaction logs
	 */
	public Transaction(String hash, String blockNumber, String blockHash, List<Log> logs) {
		this.hash = hash;
		this.blockNumber = blockNumber;
		this.blockHash = blockHash;
		this.logs = logs;
	}
	
}
