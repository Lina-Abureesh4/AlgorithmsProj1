package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Main extends Application {

	private static String file_path = "dp.txt";
	private static File file = new File(file_path);
	private static int numberOfCities;
	private static String[] cities;
	private static String start;
	private static String end;
	private static Integer[][] dtable;
	private static Integer[][] reachedFrom;
	private static City[] paths;
	private static String Optimalpath = "";
	private static Button btnDiscover = new Button("discover now >>>");
	private static Button btnDptable = new Button("DP Table >>");
	private static Stage mapStage = new Stage();
	private static Stage tableStage = new Stage();
	private static Button btnMap = new Button("<< Discover Veridia");
	private static ComboBox<String> cboxFrom = new ComboBox<>();
	private static ComboBox<String> cboxTo = new ComboBox<>();
	private static Random rand;

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			// set the primary scene in the stage
			Scene scene = mainInterface(primaryStage);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			Scene dpScreen = dptable();
			Scene mapScreen = map();

			primaryStage.setFullScreen(true);
			primaryStage.setFullScreenExitHint("");
			tableStage.setFullScreenExitHint("");
			mapStage.setFullScreenExitHint("");

			btnDiscover.setOnAction(e -> {
				primaryStage.close();
				tableStage.close();
				mapStage.show();
			});

			btnDptable.setOnAction(e -> {
				primaryStage.close();
				mapStage.close();
				tableStage.show();
				tableStage.setFullScreen(true);
			});

			btnMap.setOnAction(e -> {
				primaryStage.close();
				mapStage.show();
				tableStage.close();
			});

			btnDiscover.setOnMouseMoved(e -> {
				btnDiscover.setFont(Font.font("Times New Roman", FontWeight.BOLD, 25));
			});

			btnDiscover.setOnMouseExited(e -> {
				btnDiscover.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 25));
			});

			btnDptable.setOnMouseMoved(e -> {
				btnDptable.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
			});

			btnDptable.setOnMouseExited(e -> {
				btnDptable.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
			});

			btnMap.setOnMouseMoved(e -> {
				btnMap.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
			});

			btnMap.setOnMouseExited(e -> {
				btnMap.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
			});

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		read_data();
		dtable = new Integer[numberOfCities][numberOfCities];
		reachedFrom = new Integer[numberOfCities][numberOfCities];
		fillDTable();
		launch(args);
	}

	public static void read_data() {
		try {
			Scanner sc = new Scanner(file);
			numberOfCities = sc.nextInt();
			sc.nextLine();
			cities = new String[numberOfCities];
			paths = new City[numberOfCities - 1];
			String[] c = sc.nextLine().split(",");
			start = c[0].trim();
			end = c[1].trim();
			int j = 0;
			while (sc.hasNext()) {
				String[] cityInfo = sc.nextLine().split(", ");
				int accessPointsNo = cityInfo.length - 1;
				String city = cityInfo[0];
				cities[j] = city;
				String[] accessPoints = new String[accessPointsNo];
				int[] petrolCosts = new int[accessPointsNo];
				int[] hotelCosts = new int[accessPointsNo];
				for (int i = 1; i < cityInfo.length; i++) {
					String[] info = cityInfo[i].replace("[", " ").replace("]", " ").trim().split(",");
					accessPoints[i - 1] = info[0];
					petrolCosts[i - 1] = Integer.parseInt(info[1]);
					hotelCosts[i - 1] = Integer.parseInt(info[2]);
				}
				City newCity = new City(city, accessPointsNo, accessPoints, hotelCosts, petrolCosts);
				paths[j] = newCity;
				j++;
			}

			cities[j] = paths[j - 1].getAccessPoint(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// fill dp table
	public static void fillDTable() {

		// initialize dtable by filling it with maxInt;
		for (int i = 0; i < dtable.length; i++)
			for (int j = 0; j < dtable[i].length; j++)
				dtable[i][j] = Integer.MAX_VALUE;

		for (int i = 0; i < dtable.length; i++)
			dtable[i][i] = 0;

		// fill the dp table with values as read from the file
		int startFrom = 1;
		for (int i = 0; i < dtable.length - 1; i++) {
			int numberOfConnections = paths[i].getNumberOfAccessPoints();
			int j = startFrom;
			for (; j < startFrom + numberOfConnections; j++) {
				dtable[i][j] = paths[i].getTotalCosts()[j - startFrom];
				reachedFrom[i][j] = i;
			}
			if (i + 1 == startFrom)
				startFrom = j;
		}

		// fill the rest of dp table by calculating the minimum paths' costs between cities
		startFrom = 1;
		int i = 0;
		int istart = 1;
		for (; i < dtable.length - 4; i++) {
			int numberOfConnections = paths[i].getNumberOfAccessPoints();
			int jConnections = paths[startFrom].getNumberOfAccessPoints();
			int j = startFrom;
			while (startFrom + numberOfConnections + jConnections <= dtable.length) {
				for (int k = startFrom + numberOfConnections; k < startFrom + numberOfConnections + jConnections; k++) {
					j = startFrom;
					for (; j < startFrom + numberOfConnections; j++) {
						int cost = dtable[i][j] + dtable[j][k];
						if (cost < dtable[i][k]) {
							dtable[i][k] = cost;
							reachedFrom[i][k] = j;
						}
					}
				}
				startFrom += numberOfConnections;
				numberOfConnections = jConnections;
				try {
					jConnections = paths[startFrom].getNumberOfAccessPoints();
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}

			if (i + 1 == istart)
				istart += paths[i].getNumberOfAccessPoints();

			startFrom = istart;
		}
	}

	public static void printArray(City[] arr) {
		for (int i = 0; i < arr.length; i++)
			System.out.println(arr[i] + " ");
	}

	public static void printArray(String[] arr) {
		for (int i = 0; i < arr.length; i++)
			System.out.print(arr[i] + ", ");
		System.out.println();
	}

	public static void printArray2(Integer[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++)
				System.out.print(arr[i][j] + " ");
			System.out.println();
		}
	}

	public static void printArray2(int[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++)
				System.out.print(arr[i][j] + " ");
			System.out.println();
		}
	}

	// this method gets the optimal path between two cities
	public static void getOptimalPath(String start, String end) {
		int startInt = findCityIndex(start);
		int endInt = findCityIndex(end);

		if (startInt != -1 && endInt != -1 && !start.equalsIgnoreCase(end)
				&& dtable[startInt][endInt] != Integer.MAX_VALUE)
			getOptimalPath(startInt, endInt);
	}

	// helper recursive method
	public static void getOptimalPath(int start, int end) {
		Optimalpath = "";
		if (start == end) {
			Optimalpath += cities[start];
			return;
		}

		getOptimalPath(start, reachedFrom[start][end]);
		Optimalpath += ", " + cities[end];
	}

	// this method returns the minimum cost between two cities
	public static int getOptimalPathCost(String start, String end) {
		int startInt = findCityIndex(start);
		int endInt = findCityIndex(end);

		if (startInt == -1 || endInt == -1 || dtable[startInt][endInt] == Integer.MAX_VALUE) {
			return -1;
		}

		return dtable[startInt][endInt];
	}

	// this method gets an alternative path between two cities
	public static void getAlternativePaths(String start, String end, String[] alternativePaths,
			int[] alternativeCosts) {

		rand = new Random();
		int startInt = findCityIndex(start);
		int endInt = findCityIndex(end);

		if (startInt != -1 && endInt != -1 && !start.equalsIgnoreCase(end)
				&& dtable[startInt][endInt] != Integer.MAX_VALUE) {

			for (int i = 0; i < alternativePaths.length; i++) {
				alternativePaths[i] = start;
				alternativeCosts[i] = 0;
				getAlternativePath(i, startInt, endInt, alternativePaths, alternativeCosts);
			}
		}
	}

	// helper recursive method
	public static void getAlternativePath(int index, int start, int end, String[] alternativePaths,
			int[] alternativeCosts) {

		if (paths[start].hasConnectionWith(cities[end])) {
			alternativePaths[index] += ", " + cities[end];
			alternativeCosts[index] += paths[start].getTotalCostWithCity(cities[end]);
			return;
		}

		int nextVal = rand.nextInt(0, paths[start].getNumberOfAccessPoints());
		alternativePaths[index] += ", " + paths[start].getAccessPoints()[nextVal];
		alternativeCosts[index] += paths[start].getTotalCosts()[nextVal];
		getAlternativePath(index, findCityIndex(paths[start].getAccessPoints()[nextVal]), end, alternativePaths,
				alternativeCosts);
	}

	// find the index of a given city in the cities array
	public static int findCityIndex(String city) {
		for (int i = 0; i < cities.length; i++) {
			if (cities[i].equals(city))
				return i;
		}
		return -1;
	}

	// JavaFX
	public Scene mainInterface(Stage stage) {

		// root
		StackPane rootPane = new StackPane();

		// set the main image interface
		Image img = new Image("main interface.jpg");
		ImageView maniImgView = new ImageView(img);

		// fit height and width of image
		maniImgView.fitHeightProperty().bind(rootPane.heightProperty());
		maniImgView.fitWidthProperty().bind(rootPane.widthProperty());

		// set title
		VBox box = new VBox(50);
		box.setAlignment(Pos.CENTER);

		Label lblTitle = new Label("Path Finder");
		lblTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70));
		lblTitle.setTextFill(Color.WHITE);

		HBox btnBox = new HBox();
		btnBox.setAlignment(Pos.BASELINE_RIGHT);
		btnBox.setPadding(new Insets(0, 0, 50, 0));

		btnDiscover.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 25));
		btnDiscover.setPrefSize(340, 50);
		btnDiscover.setStyle("-fx-background-color: transparent;");
		btnDiscover.setTextFill(Color.WHITE);
		btnBox.getChildren().add(btnDiscover);

		Label lblDesc = new Label("Discover the most cost-effective routes between the cities of Veridia");
		lblDesc.setFont(Font.font("Lucida Calligraphy", FontWeight.BOLD, 40));
		lblDesc.setTextFill(Color.WHITE);
		box.getChildren().addAll(lblTitle, lblDesc);

		BorderPane topPane = new BorderPane();
		topPane.setCenter(box);
		topPane.setBottom(btnBox);

		// put the mainImg and the borderPane in the stack pane
		rootPane.getChildren().addAll(maniImgView, topPane);

		// create scene
		Scene scene = new Scene(rootPane, stage.getWidth(), stage.getHeight());
		scene.setFill(Color.WHITE);

		rootPane.setPrefHeight(scene.getHeight());
		rootPane.setPrefWidth(scene.getWidth());

		return scene;
	}

	public static Scene map() {

		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: white;");

		// create scene
		Scene scene = new Scene(pane, mapStage.getWidth(), mapStage.getHeight());
		scene.setFill(Color.WHITE);

		Image pathsImg = new Image("veridia.jpeg");
		ImageView pathsImgView = new ImageView(pathsImg);
		StackPane imgPane = new StackPane();
		imgPane.getChildren().add(pathsImgView);
		imgPane.setMinSize(300, 650);

		// fit height and width of image
		pathsImgView.fitHeightProperty().bind(imgPane.heightProperty().divide(1.1));
		pathsImgView.fitWidthProperty().bind(imgPane.widthProperty().divide(1.1));

		HBox headingBox = new HBox();
		headingBox.setAlignment(Pos.CENTER);
		headingBox.setPadding(new Insets(10, 0, 0, 0));

		Label lblHeading = new Label("Veridia's Routs Discovery");
		lblHeading.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 40));
		lblHeading.setTextFill(Color.GREEN);
		headingBox.getChildren().add(lblHeading);
		pane.setTop(headingBox);

		HBox destBox = new HBox(5);

		Label lblFrom = new Label("From: ");
		lblFrom.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));
		Label lblTo = new Label("To: ");
		lblTo.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		// paths and costs:
		Label lblOptimalPath = new Label("Optimal Path: ");
		lblOptimalPath.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));

		Label lblOptimalPath2 = new Label();
		lblOptimalPath2.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lblOptimalPathCost = new Label("Cost: ");
		lblOptimalPathCost.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lblAlternativePaths = new Label("Alternative Paths: ");
		lblAlternativePaths.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));

		Label lbl1Path = new Label();
		lbl1Path.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lbl2Path = new Label();
		lbl2Path.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lbl3Path = new Label();
		lbl3Path.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lblCost1 = new Label();
		lblCost1.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lblCost2 = new Label();
		lblCost2.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		Label lblCost3 = new Label();
		lblCost3.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));

		VBox pathsBox = new VBox(10);
		pathsBox.setAlignment(Pos.BASELINE_LEFT);
		pathsBox.setPadding(new Insets(30, 10, 10, 10));

		pane.setRight(pathsBox);

		VBox imgBox = new VBox(20);
		imgBox.setAlignment(Pos.CENTER);
		imgBox.getChildren().add(imgPane);

		if (start != null && end != null && dtable[findCityIndex(start)][findCityIndex(end)] != Integer.MAX_VALUE) {
			pathsBox.getChildren().clear();
			System.out.println(getOptimalPathCost(start, end));
			getOptimalPath(start, end);
			System.out.println(Optimalpath);
			pathsBox.getChildren().addAll(lblOptimalPath, lblOptimalPath2, lblOptimalPathCost, lblAlternativePaths,
					lbl1Path);

			lblOptimalPath2.setText(Optimalpath);
			lblOptimalPathCost.setText("Cost: " + getOptimalPathCost(start, end));

			if (findCityIndex(start) < paths.length && paths[findCityIndex(start)].hasConnectionWith(end)) {
				lbl1Path.setText("There are no other solutions");
				lbl1Path.setTextFill(Color.RED);

				pathsBox.getChildren().clear();
				pathsBox.getChildren().addAll(lblOptimalPath, lblOptimalPath2, lblOptimalPathCost, lblAlternativePaths,
						lbl1Path);

			} else {
				lbl1Path.setTextFill(Color.BLACK);
				String[] alternativePaths = new String[3];
				int[] alternativeCosts = new int[3];

				getAlternativePaths(start, end, alternativePaths, alternativeCosts);

				lbl1Path.setText("1. " + alternativePaths[0]);
				lbl2Path.setText("2. " + alternativePaths[1]);

				lblCost1.setText("Cost: " + alternativeCosts[0]);
				lblCost2.setText("Cost: " + alternativeCosts[1]);

				if (alternativePaths[0].split(",").length > 3) {

					pathsBox.getChildren().clear();
					pathsBox.getChildren().addAll(lblOptimalPath, lblOptimalPath2, lblOptimalPathCost,
							lblAlternativePaths, lbl1Path, lblCost1, lbl2Path, lblCost2, lbl3Path, lblCost3);
					getAlternativePaths(start, end, alternativePaths, alternativeCosts);

					lbl3Path.setText("3. " + alternativePaths[2]);

					lblCost3.setText("Cost: " + alternativeCosts[2]);
				} else {
					pathsBox.getChildren().clear();
					pathsBox.getChildren().addAll(lblOptimalPath, lblOptimalPath2, lblOptimalPathCost,
							lblAlternativePaths, lbl1Path, lblCost1, lbl2Path, lblCost2);
					lbl3Path.setText("");

					lblCost3.setText("");
				}
			}
			pathsBox.setPadding(new Insets(90, 50, 0, 0));

		} else {
			lblOptimalPath2.setText("There are no paths between " + start + " and " + end);
			lblOptimalPath2.setTextFill(Color.RED);
			lblOptimalPath2.setAlignment(Pos.CENTER);
			lblOptimalPath2.setTextAlignment(TextAlignment.CENTER);
			imgBox.getChildren().add(lblOptimalPath2);
		}

		pane.setCenter(imgBox);

		pane.setPrefHeight(scene.getHeight());
		pane.setPrefWidth(scene.getWidth());

		mapStage.setFullScreen(true);
		mapStage.setScene(scene);

		btnDptable.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
		btnDptable.setStyle("-fx-background-color: transparent;");
		HBox goDPBox = new HBox();
		goDPBox.getChildren().add(btnDptable);
		goDPBox.setAlignment(Pos.BASELINE_RIGHT);
		goDPBox.setPadding(new Insets(0, 20, 30, 0));

		pane.setBottom(goDPBox);

		return scene;
	}

	public static Scene dptable() {

		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: white;");

		// create scene
		Scene scene = new Scene(pane, tableStage.getWidth(), tableStage.getHeight());
		tableStage.setScene(scene);
		tableStage.setFullScreen(true);
		scene.setFill(Color.WHITE);

		btnMap.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
		btnMap.setStyle("-fx-background-color: transparent;");
		HBox goMapBox = new HBox();
		goMapBox.getChildren().add(btnMap);
		goMapBox.setAlignment(Pos.BASELINE_LEFT);
		goMapBox.setPadding(new Insets(0, 0, 10, 0));
		pane.setBottom(goMapBox);

		// Heading
		Label lblHeading = new Label("Dynamic Programming Table: ");
		lblHeading.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 30));
		lblHeading.setTextFill(Color.GREEN);
		HBox headingBox = new HBox();
		headingBox.getChildren().add(lblHeading);
		headingBox.setAlignment(Pos.CENTER);
		headingBox.setPadding(new Insets(50, 0, 20, 0));
		pane.setTop(headingBox);

		TableView<ObservableList<Integer>> tableView = createTableView(dtable);
		pane.setCenter(tableView);

		return scene;
	}

	private static ObservableList<ObservableList<Integer>> buildData(Integer[][] dataArray) {
		ObservableList<ObservableList<Integer>> data = FXCollections.observableArrayList();

		for (Integer[] row : dataArray) {
			data.add(FXCollections.observableArrayList(row));
		}

		return data;
	}

	private static TableView<ObservableList<Integer>> createTableView(Integer[][] dataArray) {
		TableView<ObservableList<Integer>> tableView = new TableView<>();
		tableView.setItems(buildData(dataArray));

		tableView.setRowFactory(i -> {
			TableRow<ObservableList<Integer>> row = new TableRow<>();
			row.setMinHeight(47);
			row.setDisable(true);
			return row;
		});

		final TableColumn<ObservableList<Integer>, String> column1 = new TableColumn<>();
		column1.setStyle(
				"-fx-alignment: center; -fx-posture: bold; -fx-background-color: white; -fx-border-color: transparent");

		column1.setMinWidth(100);
		column1.setCellValueFactory(cellData -> {
			int rowIndex = cellData.getTableView().getItems().indexOf(cellData.getValue());
			if (rowIndex >= 0 && rowIndex < cities.length)
				return new SimpleStringProperty(cities[rowIndex]);
			else
				return new SimpleStringProperty("");
		});

		column1.setStyle(
				"-fx-font-weight: bold; -fx-alignment: center; -fx-background-color: white; -fx-border-color: transparent;");
		column1.setSortable(false);

		tableView.getColumns().add(column1);
		for (int i = 0; i < cities.length; i++) {
			final int curCol = i;
			final TableColumn<ObservableList<Integer>, Integer> column = new TableColumn<>(cities[i]);
			column.setCellValueFactory(param -> (param.getValue().get(curCol).compareTo(Integer.MAX_VALUE)) != 0
					? new ReadOnlyObjectWrapper<>(param.getValue().get(curCol))
					: null);
			column.setMinWidth(100);
			column.setStyle("-fx-alignment: center; -fx-background-color: white; -fx-border-color: transparent");
			tableView.getColumns().add(column);
			column.setSortable(false);
		}
		tableView.setMaxWidth(1515);
		tableView.setMaxHeight(2200);

		return tableView;
	}

}
