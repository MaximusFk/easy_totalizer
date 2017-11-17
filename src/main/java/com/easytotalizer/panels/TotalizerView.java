package com.easytotalizer.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Convert;

import com.easytotalizer.contracts.EasyTotalizer;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class TotalizerView extends JPanel {
	
	private static final Logger log = Logger.getLogger(TotalizerView.class.getName());
	
	private JLabel title;
	private JLabel description;
	private JLabel minimumBet;
	private JLabel percent;
	private JTextField addressField;
	private JTextField bankField;
	private JTextField payAmountField;
	private JTextField isClosedField;
	private JTable variantTable;
	private JButton btnClose;
	private JButton btnBet;
	private JButton btnTakeReward;
	
	private final String contractAddress;
	private Web3j web3j;
	private Credentials credentials;
	private EasyTotalizer easyTotalizerContract;
	
	private BigInteger minimumBetWei;

	/**
	 * Create the panel.
	 */
	public TotalizerView(String contractAddress, Web3j web3, Credentials credentials) {
		this.contractAddress = contractAddress;
		this.web3j = web3;
		this.credentials = credentials;
		this.initialize();
	}
	
	private void initialize() {
		setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		setLayout(new BorderLayout(0, 0));
		
		Box infoBox = Box.createVerticalBox();
		add(infoBox, BorderLayout.NORTH);
		
		title = new JLabel("Title");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoBox.add(title);
		
		description = new JLabel("Description");
		description.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoBox.add(description);
		
		Box minBetBox = Box.createHorizontalBox();
		infoBox.add(minBetBox);
		
		JLabel lblMinimumBet = new JLabel("Minimum bet: ");
		minBetBox.add(lblMinimumBet);
		
		minimumBet = new JLabel("5 eth");
		minBetBox.add(minimumBet);
		
		Box percentBox = Box.createHorizontalBox();
		infoBox.add(percentBox);
		
		JLabel lblPercent = new JLabel("Percent: ");
		percentBox.add(lblPercent);
		
		percent = new JLabel("5 %");
		percentBox.add(percent);
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(null);
		add(verticalBox, BorderLayout.CENTER);
		
		Box addressBox = Box.createHorizontalBox();
		addressBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(addressBox);
		
		JLabel lblAddress = new JLabel("Address: ");
		lblAddress.setHorizontalAlignment(SwingConstants.LEFT);
		addressBox.add(lblAddress);
		
		addressField = new JTextField();
		addressField.setEditable(false);
		addressBox.add(addressField);
		addressField.setColumns(10);
		
		Box bankBox = Box.createHorizontalBox();
		bankBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(bankBox);
		
		JLabel lblBank = new JLabel("Bank: ");
		bankBox.add(lblBank);
		
		bankField = new JTextField();
		bankField.setEditable(false);
		bankBox.add(bankField);
		bankField.setColumns(10);
		
		Box isClosedBox = Box.createHorizontalBox();
		isClosedBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(isClosedBox);
		
		JLabel lblClosed = new JLabel("Closed: ");
		isClosedBox.add(lblClosed);
		
		isClosedField = new JTextField();
		isClosedField.setEditable(false);
		isClosedBox.add(isClosedField);
		isClosedField.setColumns(10);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(horizontalBox);
		
		JLabel lblPayAmount = new JLabel("Pay amount: ");
		horizontalBox.add(lblPayAmount);
		
		payAmountField = new JTextField();
		payAmountField.setEditable(false);
		horizontalBox.add(payAmountField);
		payAmountField.setColumns(10);
		
		Box controlsBox = Box.createHorizontalBox();
		controlsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(controlsBox);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateContractInfo();
			}
		});
		controlsBox.add(btnUpdate);
		
		btnBet = new JButton("Bet");
		btnBet.setEnabled(false);
		btnBet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				betBySelectedTableRow();
			}
		});
		controlsBox.add(btnBet);
		
		btnClose = new JButton("Close");
		btnClose.setEnabled(false);
		btnClose.setVisible(false);
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeBySelectedTableRow();
			}
		});
		controlsBox.add(btnClose);
		
		btnTakeReward = new JButton("Take reward");
		btnTakeReward.setEnabled(false);
		btnTakeReward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				takeReward();
			}
		});
		controlsBox.add(btnTakeReward);
		
		variantTable = new JTable();
		variantTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		variantTable.setModel(new DefaultTableModel(
			new String[] {
				"#",
				"Name",
				"Bets"
			},
			0
		) {
			Class[] columnTypes = new Class[] {
				Long.class,
				String.class,
				String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		variantTable.getSelectionModel().addListSelectionListener(event -> {
			btnBet.setEnabled(true);
			btnClose.setEnabled(true);
		});
		verticalBox.add(new JScrollPane(variantTable));
		
		this.easyTotalizerContract = EasyTotalizer.load(
				contractAddress,
				web3j,
				credentials,
				EasyTotalizer.GAS_PRICE,
				EasyTotalizer.GAS_LIMIT
			);
		loadContractInfo();
	}
	
	private void loadContractInfo() {
		this.easyTotalizerContract.title().sendAsync().thenAccept(this::setTitle).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.description().sendAsync().thenAccept(this::setDescription).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.minimumBet().sendAsync().thenAccept(this::setMinimumBet).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.percent().sendAsync().thenAccept(this::setPercent).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.bank().sendAsync().thenAccept(this::setBank).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.isClosed().sendAsync().thenAccept(this::setClosed).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.payAmount().sendAsync().thenAccept(this::setPayAmount).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.getVariantsCount().sendAsync().thenAccept(this::loadVariants).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.organizer().sendAsync().thenAccept(this::setOrganizerAddress).exceptionally(this::showErrorDialog);
		this.setAddress(contractAddress);
	}
	
	private void updateContractInfo() {
		this.easyTotalizerContract.bank().sendAsync().thenAccept(this::setBank).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.isClosed().sendAsync().thenAccept(this::setClosed).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.payAmount().sendAsync().thenAccept(this::setPayAmount).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.organizer().sendAsync().thenAccept(this::setOrganizerAddress).exceptionally(this::showErrorDialog);
		this.updateVariants();
		this.setAddress(contractAddress);
	}
	
	private void loadVariants(Uint256 count) {
		long countVariants = count.getValue().longValue();
		for(long i = 0; i < countVariants; ++i) {
			try {
				Tuple2<Bytes32, Uint8> variant = this.easyTotalizerContract.variants(new Uint256(i)).send();
				addVariant(variant, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateVariants() {
		this.easyTotalizerContract.getVariantsCount().sendAsync().thenAccept(this::updateVariants).exceptionally(this::showErrorDialog);
	}
	
	private void updateVariants(Uint256 count) {
		long countVariants = count.getValue().longValue();
		for(int i = 0; i < countVariants; ++i) {
			try {
				Tuple2<Bytes32, Uint8> variant = this.easyTotalizerContract.variants(new Uint256(i)).send();
				setVariantValue(variant.getValue2(), i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void addVariant(Tuple2<Bytes32, Uint8> variant, long variantIndex) {
		String name = new String(variant.getValue1().getValue());
		String bets = variant.getValue2().getValue().toString();
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		model.addRow(new Object[] { variantIndex, name, bets });
	}
	
	private void setVariantValue(Uint8 bets, int row) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		model.setValueAt(bets.getValue().toString(), row, model.findColumn("Bets"));
	}
	
	private void setTitle(Utf8String title) {
		this.title.setText(title.getValue());
	}
	
	private void setDescription(Utf8String desc) {
		this.description.setText(desc.getValue());
	}
	
	private void setMinimumBet(Uint256 betWei) {
		this.minimumBetWei = betWei.getValue();
		this.minimumBet.setText(Convert.fromWei(betWei.getValue().toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setPercent(Uint8 percent) {
		this.percent.setText(percent.getValue().toString() + " %");
	}
	
	private void setBank(Uint256 bank) {
		this.bankField.setText(Convert.fromWei(bank.getValue().toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setClosed(Bool isClosed) {
		this.isClosedField.setText(isClosed.getValue() ? "Yes" : "No");
		btnTakeReward.setEnabled(isClosed.getValue());
	}
	
	private void setPayAmount(Uint256 payAmount) {
		this.payAmountField.setText(Convert.fromWei(payAmount.getValue().toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setOrganizerAddress(Address organizerAddress) {
		btnClose.setVisible(organizerAddress.getValue().equals(credentials.getAddress()));
	}
	
	private void setAddress(String address) {
		this.addressField.setText(address);
	}
	
	private void bet(Uint256 variantIndex) {
		if(this.minimumBetWei != null && !this.minimumBetWei.equals(BigInteger.ZERO)) {
			this.easyTotalizerContract.bet(variantIndex, this.minimumBetWei).sendAsync().thenAccept(transaction -> {
				updateVariants();
			}).exceptionally(this::showErrorDialog);
		}
	}
	
	private void close(Uint256 variantIndex) {
		this.easyTotalizerContract.close(variantIndex).sendAsync().thenAccept(transaction -> {
			updateContractInfo();
		}).exceptionally(this::showErrorDialog);
	}
	
	private void takeReward() {
		this.easyTotalizerContract.takeReward().sendAsync().thenAccept(transaction -> {
			updateContractInfo();
		}).exceptionally(this::showErrorDialog);
	}
	
	private void closeBySelectedTableRow() {
		int selectedRow = variantTable.getSelectedRow();
		DefaultTableModel model = (DefaultTableModel) variantTable.getModel();
		Long variantIndex = (Long) model.getValueAt(selectedRow, model.findColumn("#"));
		close(new Uint256(variantIndex));
	}
	
	private void betBySelectedTableRow() {
		int selectedRow = variantTable.getSelectedRow();
		DefaultTableModel model = (DefaultTableModel) variantTable.getModel();
		Long variantIndex = (Long) model.getValueAt(selectedRow, model.findColumn("#"));
		bet(new Uint256(variantIndex));
	}
	
	private Void showErrorDialog(Throwable error) {
		JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		log.log(Level.SEVERE, error.getMessage(), error);
		return null;
	}

}
