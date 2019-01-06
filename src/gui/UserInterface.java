package gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.Configurations;
import main.UserSetupManager;
import scraper.CertificateInstaller;
import scraper.GradeScrapper;
import utils.GraphicUtils;
import utils.ResourcePaths;
import utils.Utils;

public class UserInterface extends Application {
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(RootController.class.getResource("root.fxml"));
			BorderPane root = loader.load();

			Region screen = new Region();
			screen.setStyle("-fx-background-color: rgba(211, 211, 211, .7)");
			screen.setVisible(true);

			ImageView loadingIcon = new ImageView();
			loadingIcon.setImage(new Image(UserInterface.class.getResource("/loading.gif").toExternalForm()));

			StackPane blocker = new StackPane();
			blocker.getChildren().addAll(root, screen, loadingIcon);

			Scene scene = new Scene(blocker, 600, 400);

			stage.setTitle("ParentCONNECTxp");
			stage.getIcons().add(new Image("icon.png"));
			stage.setMaximized(true);
			stage.setScene(scene);

			stage.show();

			GraphicUtils.displayFilledRows((TableView<?>) root.lookup("#table"));
			
			ArrayList<Region> regions = new ArrayList<Region>();
			for (Node n : root.getChildren()) {
				regions.add((Region) n);
			}

			ArrayList<DoubleProperty> horizontalRatios = GraphicUtils.calculateHorizontalRatios(regions, root);
			root.widthProperty().addListener((observer, oldValue, newValue) -> {
				GraphicUtils.maintainHorizontalRatiosOnResize(horizontalRatios, regions, root);
			});

			ArrayList<DoubleProperty> verticalRatios = GraphicUtils.calculateVerticalRatios(regions, root);
			root.heightProperty().addListener((observer, oldValue, newValue) -> {
				GraphicUtils.maintainVerticalRatiosOnResize(verticalRatios, regions, root);
			});

			Thread fetcher = new Thread() {
				public void run() {
					try {
						fetchGrades("2nd MP");
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			fetcher.start();

			Thread checker = new Thread() {
				public void run() {
					while (true) {
						if(fetcher.isAlive()) {
							try {
								Thread.sleep(10);
							}catch (Exception e) {
								e.printStackTrace();
							}
						}else {
							Platform.runLater(new Runnable() {
								@SuppressWarnings("unchecked")
								public void run() {
									blocker.getChildren().remove(1, 3);
									((TableView<DisplayableClassScore>) blocker.getChildren().get(0).lookup("#table")).refresh();
								}
							});

							break;
						}
					}
				}
			};

			checker.start();
		}catch (Exception e) {
			e.printStackTrace();

			stop();
		}
	}

	private void fetchGrades(String timeframe) {
		try {
			if(!new File(ResourcePaths.CONFIG_PATH).isFile()) {
				new UserSetupManager();

				CertificateInstaller certificateInstaller = new CertificateInstaller();
				if(!certificateInstaller.getKeyStore().containsAlias("parentconnect.aacps.org")) {
					certificateInstaller.install("parentconnect.aacps.org", 443);
				}
			}else {
				Configurations.readConfigurations();
			}

			ArrayList<String> parameters = new ArrayList<String>();

			Files.lines(Paths.get(ResourcePaths.CONFIG_PATH)).forEach(parameters::add);

			GradeScrapper scrapper = new GradeScrapper(Configurations.getSingleConfiguration("firstname"), Configurations.getSingleConfiguration("middlename"), Configurations.getSingleConfiguration("lastname"), Configurations.getSingleConfiguration("username"), Configurations.getSingleConfiguration("password"), Configurations.getSingleConfiguration("chromedriverpath"), Configurations.getMultiConfiguration("seleniumoptions").toArray(new String[0]));
			
			Utils.updateAssignmentsFile(scrapper.getAssignments(timeframe));
			Utils.updateClassScoresFile(scrapper.getClassScores());

			scrapper.terminate();
		}catch (Exception e) {
			e.printStackTrace();

			stop();
		}
	}

	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		UserInterface.launch(args);
	}
}
