package com.easytotalizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.bouncycastle.util.Arrays;
import org.web3j.tuples.generated.Tuple5;

public class CreateTotalizer extends JDialog {
	
	private static final Logger log = Logger.getLogger(CreateTotalizer.class.getName());
	
	private JTextField titleField;
	private JTextField percentField;
	private JTextField minBetField;
	private JTable variantsTable;
	private JTextField descriptionField;
	
	private Tuple5<String, String, BigInteger, BigInteger, List<byte[]>> contractData;

	/**
	 * Create the dialog.
	 */
	public CreateTotalizer() {
		initialize();
	}
	
	public CreateTotalizer(JFrame parent) {
		super(parent);
		initialize();
	}
	
	public Tuple5<String, String, BigInteger, BigInteger, List<byte[]>> getContractData() {
		return this.contractData;
	}
	
	private void initialize() {
		setTitle("Creating totalizer contract");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 450, 280);
		getContentPane().setLayout(new BorderLayout());
		{
			Box verticalBox = Box.createVerticalBox();
			getContentPane().add(verticalBox, BorderLayout.CENTER);
			{
				Box titleBox = Box.createHorizontalBox();
				titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
				verticalBox.add(titleBox);
				{
					JLabel lblTitle = new JLabel("Title: ");
					titleBox.add(lblTitle);
				}
				{
					titleField = new JTextField();
					titleBox.add(titleField);
					titleField.setColumns(10);
				}
			}
			{
				Box descriptionBox = Box.createHorizontalBox();
				descriptionBox.setAlignmentX(Component.LEFT_ALIGNMENT);
				verticalBox.add(descriptionBox);
				{
					JLabel lblDescription = new JLabel("Description: ");
					descriptionBox.add(lblDescription);
				}
				{
					descriptionField = new JTextField();
					descriptionBox.add(descriptionField);
					descriptionField.setColumns(10);
				}
			}
			{
				Box percentBox = Box.createHorizontalBox();
				percentBox.setAlignmentX(Component.LEFT_ALIGNMENT);
				verticalBox.add(percentBox);
				{
					JLabel lblYourPercent = new JLabel("Your percent: ");
					percentBox.add(lblYourPercent);
				}
				{
					percentField = new JTextField();
					percentBox.add(percentField);
					percentField.setColumns(10);
				}
				{
					JLabel label = new JLabel("%");
					percentBox.add(label);
				}
			}
			{
				Box minBetBox = Box.createHorizontalBox();
				minBetBox.setAlignmentX(Component.LEFT_ALIGNMENT);
				verticalBox.add(minBetBox);
				{
					JLabel lblMinimumBet = new JLabel("Minimum bet: ");
					minBetBox.add(lblMinimumBet);
				}
				{
					minBetField = new JTextField();
					minBetBox.add(minBetField);
					minBetField.setColumns(10);
				}
				{
					JLabel lblEth = new JLabel("eth");
					minBetBox.add(lblEth);
				}
			}
			{
				JButton addVariantButton = new JButton("+");
				addVariantButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						DefaultTableModel model = (DefaultTableModel) variantsTable.getModel();
						model.setRowCount(model.getRowCount() + 1);
					}
				});
				verticalBox.add(addVariantButton);
			}
			{
				variantsTable = new JTable();
				variantsTable.setAlignmentX(Component.LEFT_ALIGNMENT);
				variantsTable.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"Name"
					}
				));
				variantsTable.setRowSelectionAllowed(false);
				JScrollPane scrollPane = new JScrollPane(variantsTable);
				scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
				verticalBox.add(scrollPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						save();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						cancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private void save() {
		try {
			String title = titleField.getText(); 
			String descr = descriptionField.getText();
			BigInteger minBet = new BigInteger(minBetField.getText());
			BigInteger percent = new BigInteger(percentField.getText());
			DefaultTableModel model = (DefaultTableModel) variantsTable.getModel();
			final int rowCount = model.getRowCount();
			List<byte[]> variants = new ArrayList<>();
			for(int row = 0; row < rowCount; ++row) {
				String name = (String) model.getValueAt(row, 0);
				byte[] bytesName = Arrays.copyOf(name.getBytes(), 32);
				variants.add(bytesName);
			}
			this.contractData = new Tuple5<>(
						title,
						descr,
						minBet,
						percent,
						variants
					);
			dispose();
		}catch (Exception e) {
			openErrorDialog(e);
			e.printStackTrace();
		}
	}
	
	private void cancel() {
		dispose();
	}
	
	private void openErrorDialog(Throwable error) {
		JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		log.log(Level.SEVERE, error.getMessage(), error);
	}

}
