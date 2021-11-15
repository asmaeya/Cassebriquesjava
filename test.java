import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.awt.image.* ;
import java.util.*; 
import javax.swing.*;
import java.applet.AudioClip;
import java.applet.*;
import java.net.URL;
import java.net.MalformedURLException ;


public class CasseBriquePanel extends JPanel implements Runnable, KeyListener,MouseListener,
									MouseMotionListener {

	private AudioClip  beep;
	private int nbBriques;
	private float fps;	
	private int pxlCount;	
	private boolean count=false;								
	private int tabReference[][]; //matrice calque de reference pour les briques
	private Brique tabBriks[][]; //la multiplication de ces 2 i,j nbres vaut nbBriks
	private Date inf,sup; //temps inf et sup entre un deplt de pixel pour compter les fps
	private int rectX=200; //coordonner X du rectangle
	private int bouleX=240,bouleY=540; //coordonner x,y de la boule	
	private int timeToSleep=4; //temps de repos du thread boule default=5
	private int nbLB=1,nbCB=1; //nbre ligne et colonne de briques par default
	private int deplX=1,deplY=1; //deplacement de la boule
	private int defilement=10; //vitesse de defilement de la barre
	private Color fond = new Color(102,153,255); //couleur de fond a raffraichir
	private boolean gauche=false,bas=false; // pour les deplacement de la boule
	private boolean endOfGame=false; //est ce la fin du jeu?
	private Thread boule; // un pti thread pour ma boule
	private boolean action=false; //action de la boule
	
	
	private CasseBriquePanel(){
		super();
		boule = new Thread(this,"boule");
		
		tabReference = new int[100][100];
		tabBriks = new Brique[100][100];
		
		addMouseMotionListener(this);
		addMouseListener(this);
	
		addKeyListener(this);
		
		boule.setPriority(Thread.MIN_PRIORITY);
		boule.start(); //on demarre le thread
		init(); //initialise le tableau de brique par rapport a matrice de ref
	}
	
	public CasseBriquePanel(int[][] reference,int nbLigne,int nbColonne){
		this();
		nbLB=nbLigne;
		nbCB=nbColonne;
		tabReference=reference;
		init();
	}
	
	public void setNbBriques(int nbB){nbBriques=nbB;}
	
	public void init(){
		//creation de briques en fonction de la matrice d'origine 
		
		int x=(800-(nbCB*60))/2,y=50;
		for(int i=0;i<nbLB;i++){
			for(int j=0;j<nbCB;j++){
				if(tabReference[i][j]==1) //seulement si valeur = 1 dans tabReference sinon pas de brique
					tabBriks[i][j]=new Brique(x,y);
				x+=60;
			}
			x=(800-(nbCB*60))/2;
			y+=15;
		}
	}
	
	private void actionPerformed(ActionEvent e) {}
	
	public void paintComponent(Graphics backBuffer) 
	{
		backBuffer.setColor(fond); // Couleur de dessin en blanc
    	backBuffer.fillRect(0,0,getWidth(), getHeight()); // On efface le bufffer
    	backBuffer.setColor(Color.red);
		backBuffer.fillOval(bouleX,bouleY,10,10);
	    backBuffer.setColor(Color.blue);
	    backBuffer.fill3DRect(rectX,550,80,10,true);  
	    backBuffer.setColor(new Color(255,102,51));
	    for(int i=0;i<nbLB;i++){
	    	for(int j=0;j<nbCB;j++){
	    		if(tabBriks[i][j]!=null)
	    			backBuffer.fill3DRect(tabBriks[i][j].getX(),tabBriks[i][j].getY(),tabBriks[i][j].getW(),tabBriks[i][j].getH(),true);
	    	}
	    }
	    if(endOfGame){
	    	Font f = new Font("Courier",Font.ITALIC + Font.BOLD,25);
    		backBuffer.setFont(f);
    		backBuffer.setColor(new Color(51,0,102));
	    	backBuffer.drawString("Perdu !!!",180,180); 
	    }
	}

	/*Action de KeyListener*/
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyPressed(KeyEvent e){}

  	public void run()
  	{
		while(true){
			try{
				boule.sleep(timeToSleep); //pause pour permettre au CPU de souffler
			}
			catch (InterruptedException e) {}
			//routine de changement de direction de la boule
			if(action){
				if((bouleX<rectX-10 && bouleY>=545 && bas==true) || (bouleX>rectX+90 && bouleY>=545 && bas==true))
					endOfGame=true; //ta foiré la balle
				if(bouleY<=20)
					bas=true;
				if(bouleY>=545){
					
					bas=false;
					if(((bouleX<=(rectX+10)) && (bouleX>rectX-9)) || ((bouleX>=(rectX+65)) && (bouleX<=rectX+90)))
						deplX=3;
					else{ 
						if( ( (bouleX<=(rectX+30)) && (bouleX>(rectX+10)) ) || ( (bouleX>=(rectX+50)) && (bouleX<=rectX+70)))
							deplX=2;
					
						else deplX=1;
					}
				}
				if(bouleX<=0)
					gauche=false; 
				if(bouleX>=790)
					gauche=true;
				
				if(gauche) bouleX-=deplX; 
				else bouleX+=deplX;
				if(bas) bouleY+=deplY;
				else bouleY-=deplY;
				/*effacement des briques au fur et a mesure*/
				for(int i=nbLB-1;i>=0;i--){
					for(int j=0;j<nbCB;j++){
						//System.out.println("i:"+i+" j:"+j+" nbCB:"+nbCB+" nbLB:"+nbLB+" indice:"+((j+1)+(i*nbCB)));
						if(tabBriks[i][j]!=null){
							if( (bouleX>=tabBriks[i][j].getX()) && ( (bouleX<=(tabBriks[i][j].getX()+tabBriks[i][j].getW()))) ){
								if((bouleY>=tabBriks[i][j].getY()) && (bouleY<=(tabBriks[i][j].getY()+tabBriks[i][j].getH()))){
									if( (bouleY>tabBriks[i][j].getY()) && (bouleY<(tabBriks[i][j].getY()+tabBriks[i][j].getH())) )
										gauche=!gauche;
									else
										bas=!bas; //change de direction 
									tabBriks[i][j]=null;//efface la brique de la memoire
									nbBriques--;	
								}		
							}		
						}
					}
				}
				if(!count){
					inf = new Date();
					count=!count;
				}
				if((new Date().getTime()-inf.getTime())>1000){ //calcul des fps
					fps = pxlCount;
					inf = new Date();
					pxlCount=0;
				}
				else pxlCount++;
			}
			else bouleX=rectX+40;
			repaint();
			//routine de gestion boule en dehors de la barre
			if(endOfGame)
				action=false;
		}
	
	}
	
	public float getFps() {return fps;}
	public int getNbBriques(){return nbBriques;}
 	/*Action de MouseMotionListener */ 	
 	public void mouseMoved(MouseEvent e){rectX=e.getX();}
 	public void mouseDragged(MouseEvent e){} 	
 	public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) { action=!action;}
}

class Brique {
	
	private int[] coord;
	
	public Brique() {
		coord = new int[4];
		coord[2]=60;
		coord[3]=15;
	}
	
	public Brique(int x,int y) {
		this();
		coord[0]=x; 
		coord[1]=y; 
	}
	
	public int[] getCoord(){
		return coord; 
	}
	public int getX(){return coord[0];}
	public int getY(){return coord[1];}
	public int getH(){return coord[3];}
	public int getW(){return coord[2];}
}
