package com.easytotalizer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.utils.Convert;

import com.easytotalizer.contracts.EasyTotalizer;
import com.easytotalizer.panels.TotalizerView;
import javax.swing.Box;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MainWindow {
	
	private static final String frameTitle = "EasyTotalizes";

	private JFrame mainFrame;
	private JPanel contractPanel;
	private JTextField balanceField;
	private TotalizerView totalizerView;
	
	private Web3j web3j;
	private Credentials credentials;
	private Timer updateTimer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainWindow window = new MainWindow();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle(frameTitle);
		mainFrame.setBounds(100, 100, 508, 400);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnAccount = new JMenu("Account");
		menuBar.add(mnAccount);
		
		JMenuItem mntmOpenFile = new JMenuItem("Open file");
		mntmOpenFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileDialog();
			}
		});
		mnAccount.add(mntmOpenFile);
		
		JMenuItem mntmSetPrivateKey = new JMenuItem("Set private key");
		mntmSetPrivateKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openPrivateKeyDialog();
			}
		});
		mnAccount.add(mntmSetPrivateKey);
		
		JMenu mnContract = new JMenu("Contract");
		menuBar.add(mnContract);
		
		JMenuItem mntmOpenAddress = new JMenuItem("Open address");
		mntmOpenAddress.addActionListener((e) -> {
			openContractAddressDialog();
		});
		mnContract.add(mntmOpenAddress);
		
		JMenuItem mntmCreateTotalizer = new JMenuItem("Create totalizer");
		mntmCreateTotalizer.addActionListener((e) -> {
			openCreateTotalizerContract();
		});
		mnContract.add(mntmCreateTotalizer);
		
		Box verticalBox = Box.createVerticalBox();
		mainFrame.getContentPane().add(verticalBox, BorderLayout.CENTER);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(horizontalBox);
		
		JLabel lblBalance = new JLabel("Balance: ");
		horizontalBox.add(lblBalance);
		
		balanceField = new JTextField();
		balanceField.setEditable(false);
		balanceField.setAlignmentX(Component.LEFT_ALIGNMENT);
		balanceField.setMaximumSize(new Dimension(999, 25));
		horizontalBox.add(balanceField);
		balanceField.setColumns(10);
		
		JLabel lblEth = new JLabel("Eth");
		horizontalBox.add(lblEth);
		
		contractPanel = new JPanel();
		contractPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(contractPanel);
		
		this.initializeWeb3();
	}
	
	private void initializeWeb3() {
		this.web3j = Web3j.build(new HttpService());
	}
	
	private void initializeCredentials(File accountFile, String password) {
		try {
			this.credentials = WalletUtils.loadCredentials(password, accountFile);
			this.mainFrame.setTitle(frameTitle + " - " + this.credentials.getAddress());
			scheduleAsyncBalanceUpdate();
		} catch (IOException | CipherException e) {
			e.printStackTrace();
			this.openErrorDialog(e.getLocalizedMessage());
		}
	}
	
	private void initializeCredentials(String privateKey) {
		this.credentials = Credentials.create(privateKey);
		this.mainFrame.setTitle(frameTitle + " - " + this.credentials.getAddress());
		scheduleAsyncBalanceUpdate();
	}
	
	private void updateBalance() {
		web3j.ethGetBalance(this.credentials.getAddress(), DefaultBlockParameterName.LATEST)
		.sendAsync()
		.thenAccept(currentBalance -> {
			BigInteger balance = currentBalance.getBalance();
			balanceField.setText(Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER).toString());
		})
		.exceptionally(this::openErrorDialog);
	}
	
	private void scheduleAsyncBalanceUpdate() {
		if(updateTimer == null) {
			updateTimer = new Timer(true);
		}
		else {
			updateTimer.cancel();
		}
		updateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateBalance();
			}
		}, 0, 5000);
	}
	
	private void openFileDialog() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setDialogTitle("Please choose you Ethereum account file");
		if(fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			File choosedFile = fileChooser.getSelectedFile();
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Please, input password: ");
			JPasswordField password = new JPasswordField(45);
			panel.add(label);
			panel.add(password);
			String[] options = new String[]{"OK", "Cancel"};
			int option = JOptionPane.showOptionDialog(null, panel, "Account file chooser",
			                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
			                         null, options, options[0]);
			if(option == 0) {
				this.initializeCredentials(choosedFile, String.valueOf(password.getPassword()));
			}
		}
	}
	
	private void openPrivateKeyDialog() {
		String privateKey = JOptionPane.showInputDialog(mainFrame, "Input your ethereum account private key");
		initializeCredentials(privateKey);
	}
	
	private void openErrorDialog(String errorText) {
		JOptionPane.showMessageDialog(mainFrame, errorText, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private Void openErrorDialog(Throwable error) {
		JOptionPane.showMessageDialog(mainFrame, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		return null;
	}
	
	private void openCreateTotalizerContract() {
		final CreateTotalizer createTotalizer = new CreateTotalizer(this.mainFrame);
		createTotalizer.setVisible(true);
		Tuple5<Utf8String, Utf8String, Uint256, Uint8, DynamicArray<Bytes32>>
			contractConstructorData = createTotalizer.getContractData();
		if(contractConstructorData != null) {
			EasyTotalizer.deploy(
					web3j,
					credentials,
					EasyTotalizer.GAS_PRICE,
					EasyTotalizer.GAS_LIMIT,
					contractConstructorData.getValue1(),
					contractConstructorData.getValue2(),
					new Uint256(
							Convert.toWei(new BigDecimal(contractConstructorData.getValue3().getValue()), Convert.Unit.ETHER).toBigInteger()
					),
					contractConstructorData.getValue4(),
					new Address(credentials.getAddress()),
					contractConstructorData.getValue5()
				)
			.sendAsync()
			.thenAccept((easyTotalizer) -> {
				showTotalizer(easyTotalizer.getContractAddress());
			})
			.exceptionally(this::openErrorDialog);
		}
	}
	
	private void openContractAddressDialog() {
		String address = JOptionPane.showInputDialog(mainFrame, "Input contract address");
		showTotalizer(address);
	}
	
	private void showTotalizer(String address) {
		if(totalizerView != null) {
			this.contractPanel.remove(totalizerView);
		}
		totalizerView = new TotalizerView(address, web3j, credentials);
		this.contractPanel.add(totalizerView, BorderLayout.CENTER);
	}

}
