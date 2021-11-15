import java.io.*;
import java.lang.*;
import java.util.*;

public class LoadConfig {
	
	private int x,y;
	private int[][] matrice;
	private int nbBriques=0;
	private BufferedReader bf;
	
	public LoadConfig(){
		try {
			File file = new File("config.cfg");
			bf = new BufferedReader(new FileReader("config.cfg"));
		}
		catch(FileNotFoundException e){System.out.println("Erreur dans le fichier");}
	}
	
	public int getNbNiveau(){
		int i;
		try{
			bf = new BufferedReader(new FileReader("config.cfg"));
			i=Integer.parseInt(bf.readLine());
		}
		catch(IOException e) {return -1;}
		return i;
	}
	
	public void UnloadConfig() {
		try {
			bf.close();
		}
		catch(IOException e) {}
	}
	
	public int[][] getNiveau(int n) {
		int niv=0;
		String line;
		StringTokenizer st;
		try{
		bf = new BufferedReader(new FileReader("config.cfg"));}
		catch(IOException e){}
		try{
			if(n<=getNbNiveau()){
				do {
					if(bf.readLine().equals("####"))
						niv++;		
				}while(niv!=n);
				x=Integer.parseInt(bf.readLine());
				y=Integer.parseInt(bf.readLine());
				matrice = new int[x][y];
				for(int i=0;i<x;i++){
					line=bf.readLine();
					st= new StringTokenizer(line, " ");
					for(int j=0;j<y;j++){
						matrice[i][j]=Integer.parseInt(st.nextToken());
						if(matrice[i][j]==1)
							nbBriques++;
					}
				}	
			}
		}
		catch(IOException e){}
		return matrice;
	}
	
	public static void main(String argv[]){
		LoadConfig lc = new LoadConfig();
		System.out.println("Nombres de niveaux :"+lc.getNbNiveau());
	}
	
	public int getX(int niv){init(niv);return x;}
	public int getY(int niv){init(niv);return y;}
	
	public int getNbBriques(){return nbBriques;}
	
	private void init(int n){
		int niv=0;
		try{
		bf = new BufferedReader(new FileReader("config.cfg"));}
		catch(IOException e){}
		try{
			if(n<getNbNiveau()){
				do {
					if(bf.readLine().equals("####"))
						niv++;		
				}while(niv!=n);
				x=Integer.parseInt(bf.readLine());
				y=Integer.parseInt(bf.readLine());
			}
		}
		catch(IOException e){}
	}
}
