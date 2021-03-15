import java.util.*;
import java.security.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
	private String data;
	private long timeStamp;
	private int nonce;

	// Block Constructor
	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
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

class jchain {
	private static ArrayList<Block> blockchain = new ArrayList<Block>();
	private static int counter = 0;
	private static int difficulty = 4;
	private static JFrame frame;
	private static TextField tf1;
	private static Button addBlock;
	private static Button viewBlocks;
	private static TextField statusBox;

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
			// Compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// Check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}

	public static class insertMouseAction extends MouseAdapter {
		insertMouseAction() {
			tf1.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {

					if (tf1.getText().equals("Enter Data")) {
						tf1.setText("");
					}

					else if (tf1.getText().trim().equals("")) {
						tf1.setText("Enter Data");
					}
				}
			});
		}
	}

	public static class insertAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String msg = tf1.getText();
			if (e.getSource() == addBlock) {
				statusBox.setText("");
				if (counter == 0) {
					blockchain.add(new Block(msg, "0"));
				} else {
					blockchain.add(new Block(msg, blockchain.get(blockchain.size() - 1).hash));
				}
				System.out.println("\nTrying to mine block " + (counter + 1) + "...");
				blockchain.get(counter).mineBlock(difficulty);
				System.out.println("Blockchain is Valid: " + isChainValid());
				statusBox.setText("Trying to mine block " + (counter + 1) + "...");
				// statusBox.setText("Blockchain is Valid: " + isChainValid());
				counter += 1;
			}
			tf1.setText("Enter Data");
		}
	}

	public static class viewBlockchain implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == viewBlocks) {
				if (blockchain.isEmpty() == true) {
					System.out.println("\nBlockchain empty!\n");
				} else {
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
		}
	}

	public static void main(String[] args) {

		frame = new JFrame("JChain");
		tf1 = new TextField("Enter Data");
		tf1.setBounds(40, 70, 280, 30);
		tf1.addMouseListener(new insertMouseAction());

		addBlock = new Button("Create");
		addBlock.setBounds(33, 110, 70, 30);
		addBlock.addActionListener(new insertAction());

		viewBlocks = new Button("View");
		viewBlocks.setBounds(113, 110, 60, 30);
		viewBlocks.addActionListener(new viewBlockchain());

		statusBox = new TextField("");
		statusBox.setBounds(350, 30, 420, 143);

		frame.add(statusBox);
		frame.add(tf1);
		frame.add(addBlock);
		frame.add(viewBlocks);
		frame.setSize(800, 800);
		// frame.getContentPane().setBackground(Color.decode("#ffffff"));
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}