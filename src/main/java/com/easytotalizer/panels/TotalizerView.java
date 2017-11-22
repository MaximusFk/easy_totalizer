package com.easytotalizer.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.utils.Convert;

import com.easytotalizer.contracts.EasyTotalizer;
import com.easytotalizer.contracts.EasyTotalizer.BetMadeEventResponse;

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
	
	private static final String COLUMN_INDEX = "#";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_BETS = "Bets";

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
				COLUMN_INDEX,
				COLUMN_NAME,
				COLUMN_BETS
			},
			0
		) {
			Class[] columnTypes = new Class[] {
				Long.class,
				String.class,
				Long.class
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
		try {
			this.easyTotalizerContract.isValid();
			loadContractInfo();
			
		} catch (IOException e1) {
			showErrorDialog(e1);
		}
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
		this.registerEventListeners();
	}
	
	private void updateContractInfo() {
		this.easyTotalizerContract.bank().sendAsync().thenAccept(this::setBank).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.isClosed().sendAsync().thenAccept(this::setClosed).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.payAmount().sendAsync().thenAccept(this::setPayAmount).exceptionally(this::showErrorDialog);
		this.easyTotalizerContract.organizer().sendAsync().thenAccept(this::setOrganizerAddress).exceptionally(this::showErrorDialog);
		this.updateVariants();
		this.setAddress(contractAddress);
	}
	
	private void loadVariants(BigInteger count) {
		long countVariants = count.longValue();
		for(long i = 0; i < countVariants; ++i) {
			try {
				Tuple2<byte[], BigInteger> variant = this.easyTotalizerContract.variants(BigInteger.valueOf(i)).send();
				addVariant(variant, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateVariants() {
		this.easyTotalizerContract.getVariantsCount().sendAsync().thenAccept(this::updateVariants).exceptionally(this::showErrorDialog);
	}
	
	private void updateVariants(BigInteger count) {
		long countVariants = count.longValue();
		for(int i = 0; i < countVariants; ++i) {
			try {
				Tuple2<byte[], BigInteger> variant = this.easyTotalizerContract.variants(BigInteger.valueOf(i)).send();
				setVariantValue(variant.getValue2(), i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void registerEventListeners() {
		this.easyTotalizerContract.closedEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
			.subscribe(responce -> {
				log.log(Level.INFO, responce.toString());
				updateContractInfo();
			});
	}
	
	private void addVariant(Tuple2<byte[], BigInteger> variant, long variantIndex) {
		String name = new String(variant.getValue1());
		Long bets = variant.getValue2().longValue();
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		model.addRow(new Object[] { variantIndex, name, bets });
	}
	
	private void setVariantValue(BigInteger bets, int row) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		model.setValueAt(bets.longValue(), row, model.findColumn(COLUMN_BETS));
	}
	
	private void setVariantValue(Long bets, int row) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		model.setValueAt(bets, row, model.findColumn(COLUMN_BETS));
	}
	
	private void setVariantValueByIndex(Long bets, Long index) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		final int indexColumn = model.findColumn(COLUMN_INDEX);
		final int valueColumn = model.findColumn(COLUMN_BETS);
		for(int row = 0; row < model.getRowCount(); ++row) {
			if(model.getValueAt(row, indexColumn).equals(index)) {
				model.setValueAt(bets, row, valueColumn);
			}
		}
	}
	
	private Long getVariantValueByIndex(Long index) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		final int indexColumn = model.findColumn(COLUMN_INDEX);
		final int valueColumn = model.findColumn(COLUMN_BETS);
		for(int row = 0; row < model.getRowCount(); ++row) {
			if(model.getValueAt(row, indexColumn).equals(index)) {
				return (Long) model.getValueAt(row, valueColumn);
			}
		}
		return 0L;
	}
	
	private Long getVariantValue(int row) {
		DefaultTableModel model = (DefaultTableModel) this.variantTable.getModel();
		return (Long) model.getValueAt(row, model.findColumn(COLUMN_BETS));
	}
	
	private void setTitle(String title) {
		this.title.setText(title);
	}
	
	private void setDescription(String desc) {
		this.description.setText(desc);
	}
	
	private void setMinimumBet(BigInteger betWei) {
		this.minimumBetWei = betWei;
		this.minimumBet.setText(Convert.fromWei(betWei.toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setPercent(BigInteger percent) {
		this.percent.setText(percent.toString() + " %");
	}
	
	private void setBank(BigInteger bank) {
		this.bankField.setText(Convert.fromWei(bank.toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setClosed(Boolean isClosed) {
		this.isClosedField.setText(isClosed ? "Yes" : "No");
		btnTakeReward.setEnabled(isClosed);
	}
	
	private void setPayAmount(BigInteger payAmount) {
		this.payAmountField.setText(Convert.fromWei(payAmount.toString(), Convert.Unit.ETHER).toPlainString() + " eth");
	}
	
	private void setOrganizerAddress(String organizerAddress) {
		btnClose.setVisible(organizerAddress.equals(credentials.getAddress()));
	}
	
	private void setAddress(String address) {
		this.addressField.setText(address);
	}
	
	private void bet(BigInteger variantIndex) {
		if(this.minimumBetWei != null && !this.minimumBetWei.equals(BigInteger.ZERO)) {
			this.easyTotalizerContract.bet(variantIndex, this.minimumBetWei).sendAsync().thenAccept(transaction -> {
				for(BetMadeEventResponse response : easyTotalizerContract.getBetMadeEvents(transaction)) {
					Long oldValue = getVariantValueByIndex(response._variant.longValue());
					setVariantValueByIndex(++oldValue, response._variant.longValue());
					log.log(Level.INFO, response.toString());
				}
			}).exceptionally(this::showErrorDialog);
		}
	}
	
	private void close(BigInteger variantIndex) {
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
		Long variantIndex = (Long) model.getValueAt(selectedRow, model.findColumn(COLUMN_INDEX));
		close(BigInteger.valueOf(variantIndex));
	}
	
	private void betBySelectedTableRow() {
		int selectedRow = variantTable.getSelectedRow();
		DefaultTableModel model = (DefaultTableModel) variantTable.getModel();
		Long variantIndex = (Long) model.getValueAt(selectedRow, model.findColumn(COLUMN_INDEX));
		bet(BigInteger.valueOf(variantIndex));
	}
	
	private Void showErrorDialog(Throwable error) {
		JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		log.log(Level.SEVERE, error.getMessage(), error);
		return null;
	}
}
