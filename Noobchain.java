import java.util.*;
import java.security.*;

class StringUtil {
	public static String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

class Block {
	public String hash;
	public String previousHash;
	private String data; // A simple message.
	private long timeStamp; // No of milliseconds since 1/1/1970.
	private int nonce;

	// Block Constructor
	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); // Making sure we do this after we set the other values.
	}

	public String getData() {
		return this.data;
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

	public int getNonce() {
		return this.nonce;
	}

	// Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
		return calculatedhash;
	}

	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * "0"
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined! -> " + hash);
	}
}

class Noobchain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 4;

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// Loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			// Compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int ch, counter = 0;
		String msg;
		while (true) {
			System.out.println("\n******** Blockchain in java ********");
			System.out.println("1. Create Block\n2. View Blockchain\n3. Exit");
			System.out.println("Enter your choice: ");
			ch = sc.nextInt();
			sc.nextLine();

			switch (ch) {
			case 1:
				System.out.print("\nEnter message: ");
				msg = sc.nextLine();
				if (counter == 0) {
					blockchain.add(new Block(msg, "0"));
					System.out.println("Trying to mine block " + (counter + 1) + "...");
					blockchain.get(counter).mineBlock(difficulty);
				} else {
					blockchain.add(new Block(msg, blockchain.get(blockchain.size() - 1).hash));
					System.out.println("Trying to mine block " + (counter + 1) + "...");
					blockchain.get(counter).mineBlock(difficulty);
				}
				System.out.println("Blockchain is Valid: " + isChainValid());
				counter += 1;
				break;
			case 2:
				if (blockchain.isEmpty() == true) {
					System.out.println("\nBlockchain empty!");
				} else {
					viewBlockchain();
				}
				break;
			case 3:
				System.exit(0);
				break;
			default:
				System.out.println("\nError! Invalid\n");
			}
		}
	}

	static void viewBlockchain() {
		System.out.println("\nThe block chain:");
		System.out.println("[");
		for (int i = 0; i < blockchain.size(); i++) {
			System.out.println("\t{");
			System.out.println("\t  " + "'hash': " + "'" + blockchain.get(i).hash + "',");
			System.out.println("\t  " + "'previousHash': " + "'" + blockchain.get(i).previousHash + "',");
			System.out.println("\t  " + "'data': " + "'" + blockchain.get(i).getData() + "',");
			System.out.println("\t  " + "'timeStamp': " + blockchain.get(i).getTimeStamp() + ",");
			System.out.println("\t  " + "'nonce': " + blockchain.get(i).getNonce());
			System.out.println("\t},");
		}
		System.out.println("]");
	}
}
