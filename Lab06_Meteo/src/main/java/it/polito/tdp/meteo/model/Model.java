package it.polito.tdp.meteo.model;

import java.util.*;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private List<Mese> mesi = new LinkedList<>();
	private MeteoDAO dao = new MeteoDAO();
	private List<String> localita = null;
	private List<List<Rilevamento>> rilevamenti = new LinkedList<>();
	
	private List<String> bestSequenza = null;
	private int bestCosto = 0;

	public Model() {
		this.setMesi();
		this.setLocalita();
	}

	public List<Mese> getMesi() {
		return mesi;
	}

	public void setMesi() {
		this.mesi.add(new Mese("Gennaio", 1));
		this.mesi.add(new Mese("Febbraio", 2));
		this.mesi.add(new Mese("Marzo", 3));
		this.mesi.add(new Mese("Aprile", 4));
		this.mesi.add(new Mese("Maggio", 5));
		this.mesi.add(new Mese("Giugno", 6));
		this.mesi.add(new Mese("Luglio", 7));
		this.mesi.add(new Mese("Agosto", 8));
		this.mesi.add(new Mese("Settembre", 9));
		this.mesi.add(new Mese("Ottobre", 10));
		this.mesi.add(new Mese("Novembre", 11));
		this.mesi.add(new Mese("Dicembre", 12));
	}
	
	public List<String> getLocalita() {
		return localita;
	}
	
	public void setLocalita() {
		this.localita = new LinkedList<>(dao.getAllLocalita());
	}
	
	public List<List<Rilevamento>> getRilevamenti() {
		return rilevamenti;
	}
	
	public void setRilevamenti(int mese) {
		this.rilevamenti.clear();
		for(int i=0; i<NUMERO_GIORNI_TOTALI; i++)
		{
			List<Rilevamento> list = new LinkedList<>(this.dao.getRilevamentiGiornalieri(mese, i+1));
			rilevamenti.add(list);
		}
	}
	
	public int getUmidita(int giorno, String loc) {
		int um = 0;
		List<Rilevamento> l = this.rilevamenti.get(giorno-1);
		for(Rilevamento r : l)
			if(r.getLocalita().equals(loc))
				um = r.getUmidita();
		return um;
	}

	public Map<String, Double> getUmiditaMedia(int mese) {
		Map<String, Double> map = new HashMap<>();
		for(String s : this.localita)
		{
			int somma = 0;
			for(Rilevamento r : this.getAllRilevamentiLocalitaMese(mese, s))
				somma += r.getUmidita();
			double umidita = somma/this.getAllRilevamentiLocalitaMese(mese, s).size();
			map.put(s, umidita);
		}
		return map;
	}
	
	public List<String> trovaSequenza(int mese) {
		
		this.setRilevamenti(mese);
		this.bestCosto = 0;
		this.bestSequenza = null;
		
		List<String> parziale = new LinkedList<>();
		this.cerca(parziale, 1);
		
		return bestSequenza;
	}
	
	private void cerca(List<String> parziale, int livello) {
		
		//casi terminali

		if(parziale.size()==NUMERO_GIORNI_TOTALI) 
		{
			if(this.soluzioneValida(parziale))
			{
				int costo = this.costoParziale(parziale);
				if(costo<bestCosto || this.bestSequenza==null)
				{
					this.bestSequenza = new LinkedList<>(parziale);
					this.bestCosto = costo;
				}
				return;
			}
		}
		
		if(livello>NUMERO_GIORNI_TOTALI)
			return;
		
		//caso intermedio
		
		for(String s : this.localita)
		{
				
			parziale.add(livello-1, s);
			this.cerca(parziale, (livello+1));
			parziale.remove(livello-1);
			
			/*
			
			//altra implementazione, RICORSIONE OTTIMIZZATA per rimuovere già da subito sequenze sbagliate
			
			if(!this.giorniConsecutivi(s, parziale, livello))
			{
				int cont=livello;
				while(!this.giorniConsecutivi(s, parziale, cont))
				{
					parziale.add(cont-1, s);
					cont++;
				}
				this.cerca(parziale, cont);
				for(int i=cont-1; i>=livello; i--)
					parziale.remove(i-1);
				break;
			}
			else
			{
				parziale.add(livello-1, s);
				this.cerca(parziale, livello+1);
				parziale.remove(livello-1);
			}	
			
			*/
		}
		
	}
	
	public int costoParziale(List<String> parziale) {
		
		int costo = 0;
		if(parziale.size()>0)
			costo = this.getUmidita(1, parziale.get(0));
		
		for(int i=1; i<parziale.size(); i++)
		{
			costo += this.getUmidita(i+1, parziale.get(i));
			if(parziale.size()>=2 && !parziale.get(i).equals(parziale.get(i-1)))
				costo += COST;
		}
		
		return costo;	
	}
	
	
	private int giorniLocalita(String s, List<String> parziale) {
		int cont=0;
		for(String p : parziale)
			if(s.equals(p))
				cont++;
		return cont;
	}
	

	private boolean soluzioneValida(List<String> parziale) {
		
		if(parziale.size()!=15)
			return false;
		
		for(String s : this.localita)
			if(this.giorniLocalita(s, parziale)>NUMERO_GIORNI_CITTA_MAX)
				return false;
		
		int uguali = 1;
		for(int i=1; i<parziale.size(); i++)
		{	
			if(parziale.get(i).equals(parziale.get(i-1)))
				uguali++;
			else
			{
				if(uguali>=NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && i!=(parziale.size()-1))
					uguali = 1;
				else
					return false;
			}
		}
		
		return true;
	}
	
	/*
	
	//funzione utile per l'altra implementazione della ricorsione

	private boolean giorniConsecutivi(String s, List<String> parziale, int livello) {
		int lastIndex = -1;
		if(parziale.size()>0)
			lastIndex = parziale.lastIndexOf(s);
		
		boolean ok = false;
		if(this.giorniLocalita(s, parziale)==NUMERO_GIORNI_CITTA_MAX)
			ok = true;
		
		if((livello-2)==lastIndex && parziale.size()>=NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && lastIndex!=-1)
		{
			ok = true;
			for(int i=1; i<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN; i++)
				if(!parziale.get(lastIndex-i).equals(s))
					ok = false;
		}
		return ok;
	}
	
	*/
	
	
	public List<Rilevamento> getAllRilevamenti() {
		return dao.getAllRilevamenti();
	}
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		return dao.getAllRilevamentiLocalitaMese(mese, localita);
	}
	
	public List<Rilevamento> getRilevamentiGiornalieri(int mese, int giorno) {
		return dao.getRilevamentiGiornalieri(mese, giorno);
	}

}
