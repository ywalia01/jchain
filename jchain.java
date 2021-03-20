import java.util.*;
import java.security.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;

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
		// System.out.println("Block Mined! -> " + hash);
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
	private static TextArea statusBox;
	private static int temp1 = 40;
	private static int temp2 = 40;

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
				System.out.println("Current hashes not equal");
				return false;
			}
			// Compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous hashes not equal");
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
				if (msg.equals("") || msg.equals("Enter Data")) {
					statusBox.setText("Please enter some data first");
					return;
				} else {

					if (counter == 0) {
						blockchain.add(new Block(msg, "0"));

					} else {
						blockchain.add(new Block(msg, blockchain.get(blockchain.size() - 1).hash));
					}
					frame.setVisible(false);
					frame.setVisible(true);
					statusBox.setText("Trying to mine block " + (counter + 1) + "...");
					blockchain.get(counter).mineBlock(difficulty);
					statusBox.append("\nBlock Mined! -> " + blockchain.get(blockchain.size() - 1).hash);
					statusBox.append("\nBlockchain is Valid: " + isChainValid());

					JButton blockButton = new JButton(Integer.toString(counter + 1));
					if(temp1 <=260 )
					blockButton.setBounds(temp1, 350, 50, 50);
					else{
					blockButton.setBounds(temp2, 450, 50, 50);
					temp2 += 55;
					}

					blockButton.setForeground(Color.RED);
					frame.add(blockButton);

					blockButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int block_number = Integer.parseInt(blockButton.getText());
							statusBox.setText("Block " + block_number + ":");
							statusBox.append("\n    {");
							statusBox.append(
									"\n      " + "'hash': " + "'" + blockchain.get(block_number - 1).hash + "',");
							statusBox.append("\n      " + "'previousHash': " + "'"
									+ blockchain.get(block_number - 1).previousHash + "',");
							statusBox.append(
									"\n      " + "'data': " + "'" + blockchain.get(block_number - 1).getData() + "',");
							statusBox.append("\n      " + "'timeStamp': "
									+ blockchain.get(block_number - 1).getTimeStamp() + ",");
							statusBox.append("\n      " + "'nonce': " + blockchain.get(block_number - 1).getNonce());
							statusBox.append("\n    }");
						}
					});
					temp1 += 55;
					counter += 1;
				}
			}
			tf1.setText("Enter Data");
		}
	}

	public static class viewBlockchain implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == viewBlocks) {
				if (blockchain.isEmpty() == true) {
					statusBox.setText("Blockchain empty!");
				} else {
					statusBox.setText("The block chain:" + "\n[");
					for (int i = 0; i < blockchain.size(); i++) {
						statusBox.append("\n    {");
						statusBox.append("\n      " + "'hash': " + "'" + blockchain.get(i).hash + "',");
						statusBox.append("\n      " + "'previousHash': " + "'" + blockchain.get(i).previousHash + "',");
						statusBox.append("\n      " + "'data': " + "'" + blockchain.get(i).getData() + "',");
						statusBox.append("\n      " + "'timeStamp': " + blockchain.get(i).getTimeStamp() + ",");
						statusBox.append("\n      " + "'nonce': " + blockchain.get(i).getNonce());
						statusBox.append("\n    },");
					}
					statusBox.append("\n]");
				}
			}
		}
	}

	public static void main(String[] args) {
		frame = new JFrame("JChain");
		JLabel label;
		label = new JLabel("Welcome to JChain: A Blockchain Simulation...");
		label.setBounds(40,50,1000,100);
		label.setFont(new Font("Verdana", Font.BOLD, 37));
		tf1 = new TextField("Enter Data");
		//tf1.setBounds(40, 70, 280, 30);
		tf1.setBounds(40, 180, 280, 30);
		tf1.addMouseListener(new insertMouseAction());

		addBlock = new Button("Create");
		//addBlock.setBounds(33, 110, 70, 30);
		addBlock.setBounds(33, 220, 70, 30);
		addBlock.addActionListener(new insertAction());

		viewBlocks = new Button("View");
		//viewBlocks.setBounds(113, 110, 60, 30);
		viewBlocks.setBounds(113, 220, 60, 30);
		viewBlocks.addActionListener(new viewBlockchain());

		statusBox = new TextArea("");
		//statusBox.setBounds(350, 65, 650, 260);
		statusBox.setBounds(350, 175, 650, 260);
		statusBox.setEditable(false);
		statusBox.setText("YOUR BLOCKCHAIN WILL BE DISPLAYED HERE");

		frame.add(statusBox);
		frame.add(label);
		frame.add(tf1);
		frame.add(addBlock);
		frame.add(viewBlocks);
		frame.setSize(1040, 600);
		frame.getContentPane().setBackground(Color.decode("#28527a"));
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}