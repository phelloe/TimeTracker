package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeTracker extends Application {
    private static final int dayRollover = 86400;
    private static LocalDateTime startDate;
    private static int year = LocalDateTime.now().getYear();
    private static int month = LocalDateTime.now().getMonthValue();
    private static boolean gui = false;
    private static boolean csv = false;

    private LineChart<Number, Number> generateChart(ObservableList<TimeTrackerEntry> ttMap) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return LocalDateTime.ofEpochSecond(number.longValue() + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }

            @Override
            public Number fromString(String s) {
                return null;
            }
        });

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time");
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return LocalDateTime.ofEpochSecond(dayRollover - number.longValue(), 0, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm"));
            }

            @Override
            public Number fromString(String s) {
                return null;
            }
        });

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(true);
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Start Time");

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("End Time");
        ttMap.forEach(entry -> {
            series1.getData().add(new XYChart.Data<>(entry.getDate() - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), dayRollover - (entry.getStartTime() % dayRollover)));
            series2.getData().add(new XYChart.Data<>(entry.getDate() - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), dayRollover - (entry.getEndTime() % dayRollover)));
        });

        List<XYChart.Series<Number, Number>> seriesList = Arrays.asList(series1, series2);

        lineChart.getData().addAll(seriesList);
        return lineChart;
    }


    @Override
    public void start(Stage stage) {
        ObservableList<TimeTrackerEntry> ttMap = FXCollections.observableList(new ArrayList<>(LogParser.extracted(startDate).values()));
        SplitPane splitPane = new SplitPane();
        HBox hBox = new HBox();
        Label startDateLabel = new Label("Start Date:");
        Label copyHint = new Label("Click cell to copy entry to clipboard");

        startDateLabel.setPadding(new Insets(0, 10, 0, 10));
        copyHint.setPadding(new Insets(0, 10, 0, 10));

        hBox.setAlignment(Pos.BASELINE_LEFT);

        stage.setTitle("Time Tracker");
        BorderPane pane = new BorderPane();
        TableView<TimeTrackerEntry> tableView = new TableView<>();
        tableView.setItems(ttMap);
        TableColumn<TimeTrackerEntry, String> dateCol = new TableColumn<>("Date");
        TableColumn<TimeTrackerEntry, String> startTimeCol = new TableColumn<>("Start Time");
        TableColumn<TimeTrackerEntry, String> endTimeCol = new TableColumn<>("End Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
        tableView.getColumns().add(dateCol);
        tableView.getColumns().add(startTimeCol);
        tableView.getColumns().add(endTimeCol);

        DatePicker datePicker = new DatePicker(LocalDate.of(year, month, 1));
        datePicker.setOnAction(e -> {
            startDate = LocalDateTime.of(datePicker.getValue().getYear(), datePicker.getValue().getMonthValue(), datePicker.getValue().getDayOfMonth(), 0, 0);
            ttMap.setAll(FXCollections.observableList(new ArrayList<>(LogParser.extracted(startDate).values())));
            splitPane.getItems().set(1, generateChart(ttMap));
        });
        tableView.getSelectionModel().setCellSelectionEnabled(true);

        tableView.setOnMouseClicked(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            TimeTrackerEntry entry = tableView.getSelectionModel().getSelectedItem();
            if (tableView.getSelectionModel().getSelectedCells().size() > 0) {
                switch (tableView.getSelectionModel().getSelectedCells().get(0).getColumn()) {
                    case 1 -> clipboard.setContents(new StringSelection(entry.getStartTimeString()), null);
                    case 2 -> clipboard.setContents(new StringSelection(entry.getEndTimeString()), null);
                    default -> clipboard.setContents(new StringSelection(entry.getDateString()), null);
                }
            }
        });
        hBox.getChildren().addAll(startDateLabel, datePicker, copyHint);
        pane.setTop(hBox);
        splitPane.getItems().addAll(tableView, generateChart(ttMap));
        splitPane.setDividerPosition(0, 0.21);
        pane.setCenter(splitPane);
        stage.setScene(new Scene(pane, 1200, 800));
        stage.show();

    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-y" -> {
                    i++;
                    year = Integer.parseInt(args[i]);
                }
                case "-m" -> {
                    i++;
                    month = Integer.parseInt(args[i]);
                }
                case "-g" -> gui = true;
                case "-c" -> csv = true;
                default -> {
                    System.out.println("""
                            Time Tracker 1.0
                            Usage: TimeTracker [OPTION]...
                              -y <arg>    Year of start date\s
                              -m <arg>    Month of start date\s
                              -g          Start GUI\s
                              -c          Print as comma separated values""");
                    System.exit(1);
                }
            }
        }

        startDate = LocalDateTime.of(year, month, 1, 0, 0);

        if (gui) {
            launch(args);
        } else {
            if (csv) {
                System.out.println("Date,StartTime,EndTime");
                LogParser.extracted(startDate).forEach((a, b) -> System.out.println(b.toCSVString()));
            } else {
                LogParser.extracted(startDate).forEach((a, b) -> System.out.println(b.toString()));
            }
            System.exit(0);
        }
    }
}
