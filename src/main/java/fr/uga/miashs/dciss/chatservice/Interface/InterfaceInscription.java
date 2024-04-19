package fr.uga.miashs.dciss.chatservice.Interface;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import fr.uga.miashs.dciss.chatservice.client.ClientMsg;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
public class InterfaceInscription {

	JFrame frame;
	private JTextField textmdp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfaceInscription window = new InterfaceInscription();
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
	public InterfaceInscription() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.CENTER);
		SpringLayout sl_panel_2 = new SpringLayout();
		panel_2.setLayout(sl_panel_2);
		
		JLabel lblNewLabel = new JLabel("Créez votre compte gratuitement !");
		sl_panel_2.putConstraint(SpringLayout.NORTH, lblNewLabel, 35, SpringLayout.NORTH, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, lblNewLabel, -353, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, lblNewLabel, 68, SpringLayout.NORTH, panel_2);
		sl_panel_2.putConstraint(SpringLayout.EAST, lblNewLabel, -76, SpringLayout.EAST, panel_2);
		lblNewLabel.setForeground(Color.GRAY);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Allons-y");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		sl_panel_2.putConstraint(SpringLayout.WEST, lblNewLabel_1, 191, SpringLayout.WEST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -6, SpringLayout.NORTH, lblNewLabel);
		panel_2.add(lblNewLabel_1);
		
		textmdp = new JTextField();
		sl_panel_2.putConstraint(SpringLayout.NORTH, textmdp, 43, SpringLayout.SOUTH, lblNewLabel);
		sl_panel_2.putConstraint(SpringLayout.WEST, textmdp, 129, SpringLayout.WEST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, textmdp, 63, SpringLayout.SOUTH, lblNewLabel);
		sl_panel_2.putConstraint(SpringLayout.EAST, textmdp, -113, SpringLayout.EAST, panel_2);
		panel_2.add(textmdp);
		textmdp.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Choisir votre Mot de Passe : ");
		sl_panel_2.putConstraint(SpringLayout.WEST, lblNewLabel_2, 0, SpringLayout.WEST, textmdp);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, lblNewLabel_2, -6, SpringLayout.NORTH, textmdp);
		panel_2.add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Valider");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				
				
				///********************************************************
				
				 String motDePasse = textmdp.getText().trim(); 
				 if (motDePasse.isEmpty()) {
			            JOptionPane.showMessageDialog(frame, "Veuillez entrer un mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
			        } 
				 else  {
					 ClientMsg client = new ClientMsg("localhost", 1666); 
			         try {
						client.startSession(motDePasse);
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					 JOptionPane.showMessageDialog(frame, "Votre identifiant est : " + client.getIdentifier() + ". Sauvegardez-le !");
		             
				        frame.dispose();
		                 
		                
		                
				     // Lancement de la fenêtre de chat
		                EventQueue.invokeLater(new Runnable() {
		                    public void run() {
		                        try {
		                            ListeUtilisateurs chatWindow = new ListeUtilisateurs();
		                            chatWindow.frame.setVisible(true);
		                        }catch (Exception ex) {
		                            ex.printStackTrace();
		                        }
		                    }
		                });
		               
				 }
				
				 
				//*****************************************************
			}
		});
		sl_panel_2.putConstraint(SpringLayout.NORTH, btnNewButton, 20, SpringLayout.SOUTH, textmdp);
		sl_panel_2.putConstraint(SpringLayout.WEST, btnNewButton, 179, SpringLayout.WEST, panel_2);
		panel_2.add(btnNewButton);
		
		
	}
}


