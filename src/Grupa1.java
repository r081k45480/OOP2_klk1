import java.io.Externalizable;
import java.util.LinkedList;
import java.util.List;

/**
 * U ulaznom fajlu "skladiste.csv" je data tabela u CSV formatu koja predstavlja
 * sadrzaj skladista jedne kompanije. Svaki proizvod se nalazi u zasebnom redu
 * fajla dok prvi red predstavlja zaglavlje. Vrednosti unutar redova su odvojene
 * zarezima. Sve dozvoljene kolone tabele su definisane nabrojivim tipom
 * "Kolone".
 * 
 * Implementirati tri prazna metoda koje se koriste u glavnom programu.
 * Funkcionalnost svakog metoda je opisana u komentaru iznad njega. Metodi ne
 * trebaju da razlikuju velika i mala slova ni u kom delu svoje funkcionalnosti.
 */

enum Kolone {
	ID, NAZIV, BOJA, KOLICINA, CENA;
}

public class Grupa1 {

	private static List<Kolone> usedColumns;

	/**
	 * Ucitava sadrzaj ulaznog fajla i vraca ga kao niz linija. Ako fajl ne
	 * postoji, vraca null.
	 * 
	 * Ovaj metod je vec implementiran i nije ga potrebno menjati.
	 */
	public static String ucitaj() {
		String fajl = Grupa1.class.getResource("skladiste.csv").toString();
		if (!Svetovid.testIn(fajl)) {
			return null;
		}
		return Svetovid.in(fajl).readAll();
	}

	/**
	 * Ovaj metod proverava da li se u tabeli u ulaznom fajlu nalaze samo
	 * dozvoljene kolone i ispisuje odgovarajucu poruku.
	 */
	public static void proveri(String sadrzaj) {
		usedColumns = new LinkedList<>();
		String[] lines = sadrzaj.split("\n");

		int brKolona = -1;
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			else {
				String[] kolone = line.trim().split(",", -1);
				if (brKolona == -1) {
					for (String kol : kolone) {
						try {
							Kolone kolona = getColumn(kol);
							usedColumns.add(kolona);
						} catch (IllegalArgumentException ei) {
							System.out.println("Nije validna kolona " + kol);
							return;
						}

					}
					brKolona = kolone.length;
				} else {
					if (kolone.length != brKolona)
						System.err.print("Ne poklapa se broj vrednosti i kolona u liniji: " + line+"\n");
				}
			}
		}
	}

	private static Kolone getColumn(String s) throws IllegalArgumentException {
		return Kolone.valueOf(s.toUpperCase());
	}

	/**
	 * Ovaj metod ispisuje podatke o proizvodu koji je prosledjen. U pretrazi
	 * proizvoda uvek koristiti vrednost u prvoj koloni. Prilikom ispisa
	 * podataka potrebno je nazanaciti kojoj koloni pripada koja vrednost.
	 */
	public static void ispisi(String sadrzaj, String proizvod) {
		String[] lines = sadrzaj.split("\n");
		
		boolean isFirstNotEmpty = true;
		boolean exists = false;
		
		for(String line :  lines){
			
			if(line.trim().isEmpty()) continue;
			
			if(isFirstNotEmpty){
				isFirstNotEmpty = false;
				continue;
			}
			
			//String[] vrednosti = line.split(",");
			
			//if(vrednosti[usedColumns.indexOf(Kolone.ID)].trim().equalsIgnoreCase(proizvod)){

			if(getValueOf(line, Kolone.ID).equalsIgnoreCase(proizvod)){
				printoutValues(line);
				exists = true;
				break;
			} 
		}
		if(!exists) {
			System.err.println("Ne postoji prozivad sa id-im " + proizvod);
		}
	}

	private static void printoutValues(String line) {
		for(Kolone kol : usedColumns)
			System.out.println(kol.name()+" : " + getValueOf(line, kol));//vrednosti[usedColumns.indexOf(kol)]);
	}
	
	private static String getValueOf(String line, Kolone kol){
		String[] vrednosti = line.split(",", -1);
		return vrednosti[usedColumns.indexOf(kol)].trim();
	}

	/**
	 * Ovaj metod pretvara podatke iz CSV formata u XML format. Za tacne detalje
	 * kako bi trebalo da izgleda sadrzaj u XML formatu, pogledati fajl
	 * "skladiste.xml". Obratiti paznju na uvlacenja i nove redove kako bi XML
	 * bio citljiv za ljude.
	 */
	public static String SKLADISTE_TAG_OPEN = "<Skladiste>";
	public static String SKLADISTE_TAG_CLOSE = "</Skladiste>";
	
	public static String PROIZVOD_TAG_OPEN = "<Proizvod>";
	public static String PROIZVOD_TAG_CLOSE = "</Proizvod>";
	
	public static String pretvori(String original) {
		StringBuffer sb = new StringBuffer();
		
		String[] lines = original.split("\n");
		int tabNo = 0;
		
		sb.append(SKLADISTE_TAG_OPEN+"\n");
		tabNo++;
		
		boolean isFirstNotEmpty = true;
		
		
		for(String line : lines){

			if(line.trim().isEmpty()) continue;
			
			if(isFirstNotEmpty){
				isFirstNotEmpty = false;
				continue;
			}
			
			sb.append(tabs(tabNo));
			sb.append(PROIZVOD_TAG_OPEN+"\n");
			tabNo++;

			for(Kolone k : usedColumns){
				String kolName = getCaptalizes(k.name());
				
				sb.append(tabs(tabNo));
				sb.append("<"+kolName+">");
				
				sb.append(getValueOf(line, k));
				
				sb.append("<\\"+kolName+">");

				sb.append("\n");
				
			}

			--tabNo;
			sb.append(tabs(tabNo));
			sb.append(PROIZVOD_TAG_CLOSE+"\n");
		}

		tabNo--;
		sb.append(tabs(tabNo));
		sb.append(SKLADISTE_TAG_CLOSE);
		
		return sb.toString();
	}

	private static String tabs(int tabNo) {
		StringBuilder sb = new StringBuilder("");
		for(int i = 0; i<tabNo; i++) sb.append("\t");
		return sb.toString();
	}

	private static String getCaptalizes(String name) {
		return name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
	}

	/**
	 * Glavni program je vec dat i nije ga potrebno menjati.
	 */
	public static void main(String[] args) {
		String poruka = "Unesite ID proizvoda koji zelite da prikazem [ENTER za kraj]:";
		String sadrzaj = ucitaj();
		proveri(sadrzaj);

		String proizvod = Svetovid.in.readLine(poruka);
		while (!proizvod.trim().isEmpty()) {
			ispisi(sadrzaj, proizvod);
			proizvod = Svetovid.in.readLine(poruka);
		}
		Svetovid.out.println();
		Svetovid.out.println(pretvori(sadrzaj));
	}
}
