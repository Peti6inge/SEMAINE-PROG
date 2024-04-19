package fr.uga.miashs.dciss.chatservice.Interface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;

import fr.uga.miashs.dciss.chatservice.client.ClientMsg;

import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFormattedTextField;
import javax.swing.SpringLayout;
import java.awt.Color;

public class Interface1 {

	 JFrame frame;
	private JTextField TapeID;
	private JTextField TaperMDP;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface1 window = new Interface1();
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
	public Interface1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 459, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panelTOP = new JPanel();
		frame.getContentPane().add(panelTOP, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("WELCOME TO OUR CHAT APP");
		lblNewLabel.setBackground(new Color(240, 240, 240));
		panelTOP.add(lblNewLabel);
		
		JPanel panelCENTRE = new JPanel();
		frame.getContentPane().add(panelCENTRE, BorderLayout.CENTER);
		panelCENTRE.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panelCENTRE.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		panel.add(panelInfo, BorderLayout.CENTER);
		SpringLayout sl_panelInfo = new SpringLayout();
		panelInfo.setLayout(sl_panelInfo);
		
		TapeID = new JTextField();
		sl_panelInfo.putConstraint(SpringLayout.NORTH, TapeID, 96, SpringLayout.NORTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.EAST, TapeID, -119, SpringLayout.EAST, panelInfo);
		panelInfo.add(TapeID);
		TapeID.setColumns(10);
		
		TaperMDP = new JTextField();
		sl_panelInfo.putConstraint(SpringLayout.WEST, TaperMDP, 141, SpringLayout.WEST, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, TaperMDP, -212, SpringLayout.SOUTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.EAST, TaperMDP, -119, SpringLayout.EAST, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.WEST, TapeID, 0, SpringLayout.WEST, TaperMDP);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, TapeID, -39, SpringLayout.NORTH, TaperMDP);
		panelInfo.add(TaperMDP);
		TaperMDP.setColumns(10);
		
		JButton Cnn = new JButton("Se connecter ");
		sl_panelInfo.putConstraint(SpringLayout.NORTH, Cnn, 14, SpringLayout.SOUTH, TaperMDP);
		sl_panelInfo.putConstraint(SpringLayout.WEST, Cnn, 178, SpringLayout.WEST, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, Cnn, -163, SpringLayout.SOUTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.EAST, Cnn, -168, SpringLayout.EAST, panelInfo);
		panelInfo.add(Cnn);
		
		//**************************************
		
		Cnn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String userIdText = TapeID.getText();
		        String password = TaperMDP.getText();

		        try {
		            int userId = Integer.parseInt(userIdText); 
		            ClientMsg client = new ClientMsg(userId, "serverAddress", 1666);
		            
		            // Écouteur pour gérer la réponse du serveur
		            client.addConnectionListener(active -> {
		                if (active) {
		                    JOptionPane.showMessageDialog(frame, "Connexion réussie!");
		                } else {
		                    JOptionPane.showMessageDialog(frame, "Connexion échouée : ID ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
		                }
		            });
		            
		            // Tentative de démarrage de session
		            client.startSession(password);

		        } catch (NumberFormatException nfe) {
		            JOptionPane.showMessageDialog(frame, "L'ID doit être un nombre entier", "Erreur de format", JOptionPane.ERROR_MESSAGE);
		        } catch (Exception ex) {
		            JOptionPane.showMessageDialog(frame, "Erreur lors de la connexion au serveur: " + ex.getMessage(), "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});

	    // **************
	
		
		JButton Insc = new JButton("S'inscrire ");
		sl_panelInfo.putConstraint(SpringLayout.WEST, Insc, 127, SpringLayout.WEST, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, Insc, -55, SpringLayout.SOUTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.EAST, Insc, -97, SpringLayout.EAST, panelInfo);
		
		
		
		Insc.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // Ferme la fenêtre actuelle, si désiré
		        frame.setVisible(false);
		        frame.dispose();

		        // Ouvre la nouvelle fenêtre d'inscription
		        EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                try {
		                    InterfaceInscription window = new InterfaceInscription();
		                    window.frame.setVisible(true);
		                } catch (Exception ex) {
		                    ex.printStackTrace();
		                }
		            }
		        });
		    }
		});

		
		
		panelInfo.add(Insc);
		
		JLabel lblNewLabel_1 = new JLabel(" ID : ");
		sl_panelInfo.putConstraint(SpringLayout.WEST, lblNewLabel_1, 0, SpringLayout.WEST, TapeID);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -6, SpringLayout.NORTH, TapeID);
		panelInfo.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("MOT DE PASSE : ");
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, lblNewLabel_2, -238, SpringLayout.SOUTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.NORTH, TaperMDP, 6, SpringLayout.SOUTH, lblNewLabel_2);
		sl_panelInfo.putConstraint(SpringLayout.WEST, lblNewLabel_2, 0, SpringLayout.WEST, TapeID);
		panelInfo.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Pas encore de compte !");
		sl_panelInfo.putConstraint(SpringLayout.WEST, lblNewLabel_3, 178, SpringLayout.WEST, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.SOUTH, lblNewLabel_3, -108, SpringLayout.SOUTH, panelInfo);
		sl_panelInfo.putConstraint(SpringLayout.NORTH, Insc, 18, SpringLayout.SOUTH, lblNewLabel_3);
		lblNewLabel_3.setForeground(Color.GRAY);
		panelInfo.add(lblNewLabel_3);
	}
}


