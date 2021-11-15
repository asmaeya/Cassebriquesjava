import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*; 
import java.lang.*;
import java.awt.image.* ;
import javax.swing.event.*;

public class CasseBrique extends JFrame implements Runnable,WindowListener{
	
	private static JPanel p;
	private static JMenuBar menu;
	private static JMenu game,option,aide;	
	private static JMenuItem nouv,quit,apropos,niveaux;
	private static JLabel barreEtat=new JLabel(" ");
	private static JOptionPane jop;
	private static Thread affichage; //un thread pour l'affichage
	private static Color fond = new Color(102,153,255); //couleur de fond a raffraichir
	private static CasseBriquePanel cbp;
	private static ImageDeFond back = new ImageDeFond();
	private static LoadConfig lc;
	protected static int niveau=1; //niveau de depart
	
	public CasseBrique(){
		menu = new JMenuBar();
		game = new JMenu("Jeux");
		option = new JMenu("Options");
		aide = new JMenu("Aide");
		nouv = new JMenuItem("Nouveau");
		apropos = new JMenuItem("A Propos");
		niveaux = new JMenuItem("Niveau");
		p = new JPanel();
		affichage = new Thread(this,"Affichage");
		
		setSize(800,630);
		setLocation(30,30);  
		setVisible(true);
		setBackground(fond);
		setTitle("Casse Brique      \t\t- Spawnrider -\t\t   ");
		setJMenuBar(menu); 
		setResizable(false);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(barreEtat,BorderLayout.SOUTH);
		
		addWindowListener(this); //Action sur les boutons de fermeture Frame
		
		menu.setVisible(true);
		menu.add(game);
		menu.add(option);
		menu.add(aide);
		game.add(nouv);
		aide.add(apropos);
		option.add(niveaux);
		
		nouv.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				lc = new LoadConfig();
				cbp = new CasseBriquePanel(lc.getNiveau(niveau),lc.getX(niveau),lc.getY(niveau));
				cbp.setNbBriques(lc.getNbBriques());
				lc.UnloadConfig();
            	p.removeAll();//enleve l'ancienne partie
            	p.add(cbp); //ajoute le nouveau jeu
            	if(!affichage.isAlive())
            		affichage.start();
            	show(); //affiche le nouveau jeu
            }
        });
		apropos.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(getContentPane(),"Casse - Brique\nVersion 1.2\nSpawnrider Te@m\nYohann CIURLIK\nmel:alphaspawnrider@hotmail.com\nRight To Copy - 2004","A Propos",JOptionPane.INFORMATION_MESSAGE);

            }
        });
		niveaux.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	new Slider();
            	
            }
        });
		p.setLayout(new GridLayout(1,1,15,15));
		p.add(back);
		getContentPane().add(p,BorderLayout.CENTER);
		setEtat("Casse Brique v1.2 By Spawnrider 21/10/2004");
		
		show();
	}
	
	public void run(){
		while(true){
			try{
				affichage.sleep(10); //pause pour permettre au CPU de souffler
			}
			catch (InterruptedException e) {}
			setEtat("Fps : "+cbp.getFps()+"  Nombres de Briques restantes : "+cbp.getNbBriques()+"             Niveau :"+niveau);
			if(cbp.getNbBriques()==0){
				niveau++;
				if(niveau<=lc.getNbNiveau()){
					
					lc = new LoadConfig();
					cbp = new CasseBriquePanel(lc.getNiveau(niveau),lc.getX(niveau),lc.getY(niveau));
					cbp.setNbBriques(lc.getNbBriques());
					lc.UnloadConfig();
            		p.removeAll();//enleve l'ancienne partie
            		p.add(cbp); //ajoute le nouveau jeu
            		JOptionPane.showMessageDialog(getContentPane(),"Bravo !!!\n\nVous passez au niveau :"+niveau+ " sur "+lc.getNbNiveau(),"NIVEAU ATTEINT",JOptionPane.INFORMATION_MESSAGE);
            	}
            	else {
            		JOptionPane.showMessageDialog(getContentPane(),"Bravo !!!\n\nVous avez reussi les "+lc.getNbNiveau()+" niveaux\nA Bientôt\nMerci d'avoir tester ce programme\n\nYohann C.","FIN DU JEU",JOptionPane.INFORMATION_MESSAGE);
            		setVisible(false); //ferme la fenetre quitte le programme
   					dispose(); 
    				System.exit(0);
            	}
			}
		}
	}
	
	public static void main(String argv[]) {
		CasseBrique cb = new CasseBrique();
	}
	
	public void setEtat(String etat)
	{
		barreEtat.setText(etat);
	}
	
	/*Action de WindowsListener*/
	public void windowClosing(WindowEvent e) {
    	setVisible(false); //ferme la fenetre quitte le programme
   		dispose(); 
    	System.exit(0); 
    }
	public void windowOpened(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
}
class ImageDeFond extends JPanel {
	
	private Image fond;
	
	public ImageDeFond(){
		Toolkit kit = Toolkit.getDefaultToolkit();
		fond = kit.getImage("fond.jpg");
	}
    
    public void paintComponent(Graphics g) { 
        super.paintComponent(g); 
         
        //Draw image at centered in the middle of the panel 
        g.drawImage(fond, 0, 0, this); 
    } 
}


class Slider extends JFrame implements ChangeListener{
	
	public Slider() {
		super("Niveau");
		LoadConfig lc = new LoadConfig();
		JSlider niveau = new JSlider(JSlider.HORIZONTAL,1,lc.getNbNiveau(),1);
		JLabel title = new JLabel("Redemarrer une nouvelle partie pour prendre en compte le changement de niveau.");
		niveau.setMajorTickSpacing(1);
		niveau.setMinorTickSpacing(1);
		niveau.setPaintTicks(false);
		niveau.setPaintLabels(true);
		JPanel p = new JPanel(new BorderLayout());
		p.add(title,BorderLayout.NORTH);
		p.add(niveau,BorderLayout.SOUTH);
		niveau.addChangeListener(this);
		setResizable(false);
		setContentPane(p);
		pack();
		setVisible(true);
		
	}
	
	public void stateChanged(ChangeEvent e) {
    	JSlider source = (JSlider)e.getSource();
    	int niv = (int)source.getValue();
    	if (!source.getValueIsAdjusting()) { 
	        CasseBrique.niveau = niv;
    }
}



}