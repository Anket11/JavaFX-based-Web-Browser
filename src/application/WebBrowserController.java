package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class WebBrowserController extends StackPane {
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private JFXButton addTab;
	
	public WebBrowserController() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WebBrowserController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}

	@FXML
	private void initialize() {
		
		tabPane.getTabs().clear();
		createAndAddNewTab();
		
		addTab.setOnAction(a -> createAndAddNewTab());
	}
	
	public WebBrowserTabController createAndAddNewTab(String... webSite) {
		
		WebBrowserTabController webBrowserTab = createNewTab(webSite);
		
		tabPane.getTabs().add(webBrowserTab.getTab());
		
		return webBrowserTab;
	}
	
	public WebBrowserTabController createNewTab(String... webSite) {
		
	
		Tab tab = new Tab("");
		WebBrowserTabController webBrowserTab = new WebBrowserTabController(this, tab, webSite.length == 0 ? null : webSite[0]);
		tab.setOnCloseRequest(c -> {
			
			if (tabPane.getTabs().size() == 1)
				createAndAddNewTab();

			webBrowserTab.webEngine.load("about:blank");

			
		});
		
		return webBrowserTab;
	}
	
	public TabPane getTabPane() {
		return tabPane;
	}
	
}
