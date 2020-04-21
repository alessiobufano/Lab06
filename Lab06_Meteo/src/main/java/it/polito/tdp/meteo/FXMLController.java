/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Mese;
import it.polito.tdp.meteo.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;
	private ObservableList<Mese> mesi = FXCollections.observableArrayList();

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Mese> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader


    @FXML
    void doCalcolaSequenza(ActionEvent event) {

    	this.txtResult.clear();
    	
    	Mese mese = this.boxMese.getValue();
    	if(mese==null)
    	{
    		this.txtResult.setText("Errore, selezionare un mese!");
    		return;
    	}

    	int num = mese.getNumero();
    	this.model.trovaSequenza(num);
    	
    	this.txtResult.setText("La sequenza ottimale di città da visitare dal 1° al 15 "+mese.getNome()+" è:\n");
    	for(String s : this.model.trovaSequenza(num))
    		this.txtResult.appendText(s+"\n");
    	this.txtResult.appendText("Il tutto con un costo complessivo di "+this.model.costoParziale(this.model.trovaSequenza(num))+"€");
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	
    	this.txtResult.clear();
    	
    	Mese mese = this.boxMese.getValue();
    	if(mese==null)
    	{
    		this.txtResult.setText("Errore, selezionare un mese!");
    		return;
    	}

    	int num = mese.getNumero();
    	this.txtResult.setText("Le umidità medie nelle varie località sono:\n");
    	for(String s : this.model.getUmiditaMedia(num).keySet())
    		this.txtResult.appendText(s+" "+this.model.getUmiditaMedia(num).get(s)+"%\n");
    		
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		this.mesi.addAll(this.model.getMesi());
		this.boxMese.setItems(mesi);
		this.boxMese.setValue(new Mese("",0));
	}
}

