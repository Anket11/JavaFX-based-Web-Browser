package marquee;

import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Marquee extends Pane {

	@FXML
	private Text text;

	public Marquee() {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Marquee.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@FXML
	private void initialize() {
		text.setManaged(false);
	}

	public Marquee setText(String value) {

		text.setText(value);

		return this;
	}

	public StringProperty textProperty() {
		return text.textProperty();
	}
}
