package fr.uga.miashs.dciss.chatservice.Interface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class ListeUtilisateurs {

	 JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ListeUtilisateurs window = new ListeUtilisateurs();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ListeUtilisateurs() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 498, 280);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Vous êtes connecté !");
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		JPanel panel_9 = new JPanel();
		panel_1.add(panel_9);
		SpringLayout sl_panel_9 = new SpringLayout();
		panel_9.setLayout(sl_panel_9);
		
		textField = new JTextField();
		sl_panel_9.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.NORTH, panel_9);
		sl_panel_9.putConstraint(SpringLayout.WEST, textField, 108, SpringLayout.WEST, panel_9);
		sl_panel_9.putConstraint(SpringLayout.SOUTH, textField, -6, SpringLayout.SOUTH, panel_9);
		sl_panel_9.putConstraint(SpringLayout.EAST, textField, 0, SpringLayout.EAST, panel_9);
		panel_9.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton_6 = new JButton("New ID");
		sl_panel_9.putConstraint(SpringLayout.SOUTH, btnNewButton_6, -5, SpringLayout.SOUTH, panel_9);
		btnNewButton_6.setFont(new Font("Sitka Subheading", Font.PLAIN, 9));
		sl_panel_9.putConstraint(SpringLayout.NORTH, btnNewButton_6, 0, SpringLayout.NORTH, textField);
		sl_panel_9.putConstraint(SpringLayout.WEST, btnNewButton_6, 10, SpringLayout.WEST, panel_9);
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_9.add(btnNewButton_6);
		
		textField_1 = new JTextField();
		sl_panel_9.putConstraint(SpringLayout.WEST, textField_1, 82, SpringLayout.WEST, panel_9);
		sl_panel_9.putConstraint(SpringLayout.EAST, textField_1, -6, SpringLayout.WEST, textField);
		sl_panel_9.putConstraint(SpringLayout.EAST, btnNewButton_6, -6, SpringLayout.WEST, textField_1);
		sl_panel_9.putConstraint(SpringLayout.NORTH, textField_1, 5, SpringLayout.NORTH, panel_9);
		sl_panel_9.putConstraint(SpringLayout.SOUTH, textField_1, 0, SpringLayout.SOUTH, textField);
		panel_9.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel_10 = new JPanel();
		panel_1.add(panel_10);
		
		JButton btnNewButton_5 = new JButton("Envoyer !");
		panel_10.add(btnNewButton_5);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new GridLayout(5, 1, 0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4);
		
		JButton btnNewButton = new JButton("1");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_4.add(btnNewButton);
		
		JPanel panel_5 = new JPanel();
		panel_2.add(panel_5);
		
		JButton btnNewButton_1 = new JButton("2");
		panel_5.add(btnNewButton_1);
		
		JPanel panel_7 = new JPanel();
		panel_2.add(panel_7);
		
		JButton btnNewButton_2 = new JButton("3");
		panel_7.add(btnNewButton_2);
		
		JPanel panel_8 = new JPanel();
		panel_2.add(panel_8);
		
		JButton btnNewButton_4 = new JButton("4");
		panel_8.add(btnNewButton_4);
		
		JPanel panel_6 = new JPanel();
		panel_2.add(panel_6);
		
		JButton btnNewButton_3 = new JButton("5");
		panel_6.add(btnNewButton_3);
		
		JPanel panel_3 = new JPanel();
		frame.getContentPane().add(panel_3, BorderLayout.CENTER);
		SpringLayout sl_panel_3 = new SpringLayout();
		panel_3.setLayout(sl_panel_3);
		
		JTextPane textPane = new JTextPane();
		sl_panel_3.putConstraint(SpringLayout.NORTH, textPane, 10, SpringLayout.NORTH, panel_3);
		sl_panel_3.putConstraint(SpringLayout.WEST, textPane, 22, SpringLayout.WEST, panel_3);
		sl_panel_3.putConstraint(SpringLayout.SOUTH, textPane, -10, SpringLayout.SOUTH, panel_3);
		sl_panel_3.putConstraint(SpringLayout.EAST, textPane, -22, SpringLayout.EAST, panel_3);
		panel_3.add(textPane);
	}
}
