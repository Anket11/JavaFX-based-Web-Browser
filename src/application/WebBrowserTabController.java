package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import marquee.Marquee;

public class WebBrowserTabController extends StackPane {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private Button backwardButton;
	
	@FXML
	private Button forwardButton;
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private Button goButton;
	
	@FXML
	private Button addOrRemoveBookMark;
	
	@FXML
	private Button reloadButton;
	
	@FXML
	private ComboBox<String> searchEngineComboBox;
	
	@FXML
	private WebView webView;
	
	@FXML
	private VBox errorPane;
	
	@FXML
	private JFXButton tryAgain;
	
	WebEngine webEngine;
	
	private WebHistory history;
	private ObservableList<WebHistory.Entry> historyEntryList;
	
	private final Tab tab;
	private String firstWebSite;
	
	private final WebBrowserController webBrowserController;

	public WebBrowserTabController(WebBrowserController webBrowserController, Tab tab, String firstWebSite) {
		this.webBrowserController = webBrowserController;
		this.tab = tab;
		this.firstWebSite = firstWebSite;
		this.tab.setContent(this);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WebBrowserTabController.fxml"));
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

		webEngine = webView.getEngine();

		webEngine.setCreatePopupHandler(l -> webBrowserController.createAndAddNewTab().getWebView().getEngine());
		
		setHistory(webEngine.getHistory());
		historyEntryList = getHistory().getEntries();
		SimpleListProperty<Entry> list = new SimpleListProperty<>(historyEntryList);

		tab.setTooltip(new Tooltip(""));
		tab.getTooltip().textProperty().bind(webEngine.titleProperty());
		
		StackPane stack = new StackPane();

		ProgressBar indicator = new ProgressBar();
		indicator.progressProperty().bind(webEngine.getLoadWorker().progressProperty());
		indicator.visibleProperty().bind(webEngine.getLoadWorker().runningProperty());
		indicator.setMaxSize(30, 11);
		
		// text
		Text text = new Text();
		text.setStyle("-fx-font-size:70%;");
		text.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100.00).asString("%.02f %%"));
		
		Marquee marquee = new Marquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		
		stack.getChildren().addAll(indicator, text);
		stack.setManaged(false);
		stack.setVisible(false);
		
		indicator.visibleProperty().addListener(l -> {
			if (indicator.isVisible()) {
				stack.setManaged(true);
				stack.setVisible(true);
			} else {
				stack.setManaged(false);
				stack.setVisible(false);
			}
		});

		HBox hBox = new HBox();
		hBox.getChildren().addAll(stack,marquee);
		tab.setGraphic(hBox);

		webEngine.getLoadWorker().runningProperty().addListener((observable , oldValue , newValue) -> {
			
			if (!newValue)
				searchBar.textProperty().unbind();
			else
				searchBar.textProperty().bind(webEngine.locationProperty());
		});
		searchBar.setOnAction(a -> loadWebSite(searchBar.getText()));
		searchBar.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue)
				Platform.runLater(() -> searchBar.selectAll());
			
		});

		goButton.setOnAction(searchBar.getOnAction());

		reloadButton.setOnAction(a -> reloadWebSite());

		backwardButton.setOnAction(a -> goBack());
		backwardButton.disableProperty().bind(getHistory().currentIndexProperty().isEqualTo(0));
		backwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE)
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() - 1).getUrl()).getTab());
		});

		forwardButton.setOnAction(a -> goForward());
		forwardButton.disableProperty().bind(getHistory().currentIndexProperty().greaterThanOrEqualTo(list.sizeProperty().subtract(1)));
		forwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() + 1).getUrl()).getTab());
		});

		searchEngineComboBox.getItems().addAll("Google");
		searchEngineComboBox.getSelectionModel().select(1);
		
		loadWebSite(firstWebSite);
	}
	
	public String getSearchEngineSearchUrl(String searchProvider) {
		//Find
		return "https://www.google.com/search?q=";
		// switch (searchProvider.toLowerCase()) {
		// 	case "bing":
		// 		return "http://www.bing.com/search?q=";
		// 	case "duckduckgo":
		// 		return "https://duckduckgo.com/?q=";
		// 	case "yahoo":
		// 		return "https://search.yahoo.com/search?p=";
		// 	default: //then google
		// 		return "https://www.google.com/search?q=";
		// }
	}

	public String getSearchEngineHomeUrl(String searchProvider) {
		//Find
		return "https://www.google.com/search?q=";
		// switch (searchProvider.toLowerCase()) {
		// 	case "bing":
		// 		return "http://www.bing.com";
		// 	case "duckduckgo":
		// 		return "https://duckduckgo.com";
		// 	case "yahoo":
		// 		return "https://search.yahoo.com";
		// 	default: //then google
		// 		return "https://www.google.com";
		// }
	}
	
	public void loadWebSite(String webSite) {

		String load = !new UrlValidator().isValid(webSite) ? null : webSite;
		
		try {
			webEngine.load(
					load != null ? load : getSearchEngineSearchUrl(searchEngineComboBox.getSelectionModel().getSelectedItem()) + URLEncoder.encode(searchBar.getText(), "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void loadDefaultWebSite() {
		webEngine.load(getSearchEngineHomeUrl(searchEngineComboBox.getSelectionModel().getSelectedItem()));
	}

	public void reloadWebSite() {
		if (!getHistory().getEntries().isEmpty())
			webEngine.reload();
		else
			loadDefaultWebSite();
	}

	public void goBack() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() > 0 ? -1 : 0);
		//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
	}

	public void goForward() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() < historyEntryList.size() - 1 ? 1 : 0);
		//System.out.println(history.getCurrentIndex() + "," + historyEntryList.size())
	}

	public WebView getWebView() {
		return webView;
	}

	public Tab getTab() {
		return tab;
	}
	
	public VBox getErrorPane() {
		return errorPane;
	}

	public WebHistory getHistory() {
		return history;
	}

	public void setHistory(WebHistory history) {
		this.history = history;
	}
	
}
