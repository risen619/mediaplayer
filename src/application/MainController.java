package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable
{
	private static String fileRegex = "^\\w:\\\\[\\w\\W]*\\.\\w*$";
	private static String siteRegex = "^(http|https):\\/\\/[\\w\\W]*$";
	
	@FXML private MediaView mvScreen;
	@FXML private Button bPlay;
	@FXML private Button bAdd;
	@FXML private ListView<String> lvList;
	@FXML private TextField tfLocation;

	private Stage stage;
	private MediaPlayer mp = null;
	private FileChooser fc = new FileChooser();
	private boolean locationChanged = true;
	
	private ArrayList<String> playlist = new ArrayList<String>();
	private ObservableList<String> observablePlaylist;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try (BufferedReader br = new BufferedReader(new FileReader("playlist")))
		{
		    String line;
		    while ((line = br.readLine()) != null)
		    {
		       playlist.add(line.trim());
		    }
		} catch (IOException e) { }
		observablePlaylist = FXCollections.observableList(playlist);
		lvList.setItems(observablePlaylist);
		lvList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				tfLocation.setText(newValue);
				locationChanged = true;
			}	
		});
	}

	public void setStage(Stage s) { stage = s; }
	
	private void setMedia(String location)
	{
		if(location.matches(fileRegex))
			location = "file:///" + location.replaceAll("\\\\", "/");
		System.out.println("Parsed location: " + location);
		if(mp != null)
			mp.stop();
		mp = new MediaPlayer(new Media(location));
		mvScreen.setMediaPlayer(mp);
	}
	
	private boolean isLocationValid(String l)
	{
		return l.matches(siteRegex) || l.matches(fileRegex);
	}
	
	public void bPlayOnAction(ActionEvent e) throws InterruptedException
	{
		if(mp == null || locationChanged)
		{
			String location = tfLocation.getText();
			if(isLocationValid(location))
			{
				System.out.println("Location valid: " + location);
				setMedia(location);
				locationChanged = false;
			}
			else return;
		}
		Status status = mp.getStatus();
		System.out.println(status);
				 
		if (status == Status.UNKNOWN  || status == Status.HALTED)
		{
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mp.play();
				}
			});
			t.start();
		}
		        
		if(status == Status.PAUSED || status == Status.READY  || status == Status.STOPPED)
			mp.play();
		else mp.pause(); 
	}
	
	public void tfLocationOnAction(ActionEvent e)
	{
		locationChanged = true;
		bPlay.setDisable(!isLocationValid(tfLocation.getText()));
		bAdd.setDisable(!isLocationValid(tfLocation.getText()));
	}
	
	private void savePlaylist()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream("playlist");
			for(String s : playlist)
				fos.write((s + "\r\n").getBytes());
			fos.close();
			observablePlaylist = FXCollections.observableList(playlist);
			lvList.setItems(observablePlaylist);
		}
		catch (IOException e) { }
	}
	
	public void bAddOnAction(ActionEvent e)
	{
		if(!isLocationValid(tfLocation.getText())) return;
		playlist.add(tfLocation.getText());
		savePlaylist();
	}
	
	public void miOpenOnAction(ActionEvent e)
	{
		File file = fc.showOpenDialog(stage);
		if(file != null)
		{
			tfLocation.setText(file.getAbsolutePath());
			locationChanged = true;
		}
	}
	
}
