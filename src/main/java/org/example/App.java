package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {

    private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase().contains("win");
    private static final boolean IS_MAC = System.getProperty("os.name", "").toLowerCase().contains("mac");

    // Tab 1 Controls (Downloader)
    private TextField linkField;
    private RadioButton rbVideo;
    private RadioButton rbAudio;
    private RadioButton rbScript;
    private RadioButton rbImage;
    private Button downloadButton;
    private ProgressBar progressBar;
    private Label statusLabel;
    private TextArea logArea;

    // Tab 2 Controls (Trimmer)
    private TextField filePathField;
    private Button browseButton;
    private TextField startTimeField;
    private TextField endTimeField;
    private CheckBox trimToMp4CheckBox;
    private Button trimButton;
    private Label trimStatusLabel;
    private TextArea trimLogArea;

    // Tab 3 Controls (Converter)
    private TextField convFilePathField;
    private Button convBrowseButton;
    private RadioButton rbConvMp4;
    private RadioButton rbConvMp3;
    private RadioButton rbConvWebm;
    private RadioButton rbConvWav;
    private Button convButton;
    private ProgressBar convProgressBar;
    private Label convStatusLabel;
    private TextArea convLogArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MediaSuite Pro - Media Downloader, Cutter & Converter");

        // Main layout root
        VBox root = new VBox();
        root.getStyleClass().add("root");

        // Header section
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #11111b; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        Label headerTitle = new Label("🚀 MediaSuite Pro");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cba6f7;");
        Label headerSub = new Label("  |  Downloader, Trimmer & Format Converter");
        headerSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #bac2de;");

        header.getChildren().addAll(headerTitle, headerSub);

        // TabPane setup
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ==================== TAB 1: DOWNLOADER ====================
        Tab tabDownload = new Tab("Media Downloader");
        ScrollPane scrollDownload = new ScrollPane();
        scrollDownload.setFitToWidth(true);

        VBox dlContainer = new VBox(20);
        dlContainer.getStyleClass().add("container");

        VBox dlCard = new VBox(15);
        dlCard.getStyleClass().add("card");

        Label dlCardTitle = new Label("Media Download Panel");
        dlCardTitle.getStyleClass().add("title-label");

        Label urlLabel = new Label("Media or Web Page URL:");
        urlLabel.getStyleClass().add("form-label");

        linkField = new TextField();
        linkField.setPromptText("Paste link here (e.g. YouTube, Twitter/X, Bluesky, web page)...");

        Label typeLabel = new Label("Select Download Type:");
        typeLabel.getStyleClass().add("form-label");

        ToggleGroup typeGroup = new ToggleGroup();
        rbVideo = new RadioButton("Video (MP4)");
        rbVideo.setToggleGroup(typeGroup);
        rbVideo.setSelected(true);

        rbAudio = new RadioButton("Audio (MP3)");
        rbAudio.setToggleGroup(typeGroup);

        rbScript = new RadioButton("Script / Transcript (.srt & .txt)");
        rbScript.setToggleGroup(typeGroup);

        rbImage = new RadioButton("Images (Web Page)");
        rbImage.setToggleGroup(typeGroup);

        HBox typeBox = new HBox(15, rbVideo, rbAudio, rbScript, rbImage);
        typeBox.setPadding(new Insets(5, 0, 5, 0));

        downloadButton = new Button("Start Download");
        downloadButton.setMaxWidth(Double.MAX_VALUE);

        progressBar = new ProgressBar(0.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        Label statusTitle = new Label("Status:");
        statusTitle.getStyleClass().add("form-label");
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox statusBox = new HBox(10, statusTitle, statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPromptText("Download and processing logs will stream here...");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        dlCard.getChildren().addAll(
                dlCardTitle,
                urlLabel, linkField,
                typeLabel, typeBox,
                downloadButton,
                progressBar,
                statusBox,
                logArea
        );
        dlContainer.getChildren().add(dlCard);
        scrollDownload.setContent(dlContainer);
        tabDownload.setContent(scrollDownload);

        // ==================== TAB 2: CUTTER/TRIMMER ====================
        Tab tabCut = new Tab("Media Cutter (Trimmer)");
        ScrollPane scrollCut = new ScrollPane();
        scrollCut.setFitToWidth(true);

        VBox cutContainer = new VBox(20);
        cutContainer.getStyleClass().add("container");

        VBox cutCard = new VBox(15);
        cutCard.getStyleClass().add("card");

        Label cutCardTitle = new Label("Media Cutting & Trimming Panel");
        cutCardTitle.getStyleClass().add("title-label");

        Label fileLabel = new Label("Select Media File:");
        fileLabel.getStyleClass().add("form-label");

        filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setPromptText("Choose a video or audio file from your local storage...");

        browseButton = new Button("Browse File");
        browseButton.getStyleClass().add("button-secondary");

        HBox fileSelectBox = new HBox(10, filePathField, browseButton);
        HBox.setHgrow(filePathField, Priority.ALWAYS);

        GridPane timesGrid = new GridPane();
        timesGrid.setHgap(15);
        timesGrid.setVgap(5);
        timesGrid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        timesGrid.getColumnConstraints().addAll(col1, col2);

        Label startLabel = new Label("From (Start Time):");
        startLabel.getStyleClass().add("form-label");
        startTimeField = new TextField("00:00:00");
        startTimeField.setPromptText("hh:mm:ss (e.g. 00:01:30) or seconds");

        Label endLabel = new Label("To (End Time):");
        endLabel.getStyleClass().add("form-label");
        endTimeField = new TextField("00:00:00");
        endTimeField.setPromptText("hh:mm:ss (e.g. 00:02:15) or seconds");

        timesGrid.add(startLabel, 0, 0);
        timesGrid.add(startTimeField, 0, 1);
        timesGrid.add(endLabel, 1, 0);
        timesGrid.add(endTimeField, 1, 1);

        trimToMp4CheckBox = new CheckBox("Convert output to universal MP4 format (.mp4)");
        trimToMp4CheckBox.setSelected(true);
        trimToMp4CheckBox.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px;");

        trimButton = new Button("Start Trimming");
        trimButton.setMaxWidth(Double.MAX_VALUE);

        Label trimStatusTitle = new Label("Status:");
        trimStatusTitle.getStyleClass().add("form-label");
        trimStatusLabel = new Label("Ready");
        trimStatusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox trimStatusBox = new HBox(10, trimStatusTitle, trimStatusLabel);
        trimStatusBox.setAlignment(Pos.CENTER_LEFT);

        trimLogArea = new TextArea();
        trimLogArea.setEditable(false);
        trimLogArea.setWrapText(true);
        trimLogArea.setPromptText("FFmpeg trimming logs will stream here...");
        VBox.setVgrow(trimLogArea, Priority.ALWAYS);

        cutCard.getChildren().addAll(
                cutCardTitle,
                fileLabel, fileSelectBox,
                timesGrid,
                trimToMp4CheckBox,
                trimButton,
                trimStatusBox,
                trimLogArea
        );
        cutContainer.getChildren().add(cutCard);
        scrollCut.setContent(cutContainer);
        tabCut.setContent(scrollCut);

        // ==================== TAB 3: FORMAT CONVERTER ====================
        Tab tabConvert = new Tab("Format Converter");
        ScrollPane scrollConvert = new ScrollPane();
        scrollConvert.setFitToWidth(true);

        VBox convContainer = new VBox(20);
        convContainer.getStyleClass().add("container");

        VBox convCard = new VBox(15);
        convCard.getStyleClass().add("card");

        Label convCardTitle = new Label("Media Format Conversion Panel");
        convCardTitle.getStyleClass().add("title-label");

        Label convFileLabel = new Label("Select Media File (MKV, WebM, AVI, MOV, MP4, MP3, etc.):");
        convFileLabel.getStyleClass().add("form-label");

        convFilePathField = new TextField();
        convFilePathField.setEditable(false);
        convFilePathField.setPromptText("Choose a media file to convert...");

        convBrowseButton = new Button("Browse File");
        convBrowseButton.getStyleClass().add("button-secondary");

        HBox convFileSelectBox = new HBox(10, convFilePathField, convBrowseButton);
        HBox.setHgrow(convFilePathField, Priority.ALWAYS);

        Label convTargetLabel = new Label("Select Target Format:");
        convTargetLabel.getStyleClass().add("form-label");

        ToggleGroup convGroup = new ToggleGroup();
        rbConvMp4 = new RadioButton("MP4 Video (H.264/AAC - Web Compatible)");
        rbConvMp4.setToggleGroup(convGroup);
        rbConvMp4.setSelected(true);

        rbConvMp3 = new RadioButton("MP3 Audio (320 kbps)");
        rbConvMp3.setToggleGroup(convGroup);

        rbConvWebm = new RadioButton("WebM Video");
        rbConvWebm.setToggleGroup(convGroup);

        rbConvWav = new RadioButton("WAV Audio (Lossless)");
        rbConvWav.setToggleGroup(convGroup);

        HBox convTypeBox = new HBox(15, rbConvMp4, rbConvMp3, rbConvWebm, rbConvWav);
        convTypeBox.setPadding(new Insets(5, 0, 5, 0));

        convButton = new Button("Start Conversion");
        convButton.setMaxWidth(Double.MAX_VALUE);

        convProgressBar = new ProgressBar(0.0);
        convProgressBar.setMaxWidth(Double.MAX_VALUE);

        Label convStatusTitle = new Label("Status:");
        convStatusTitle.getStyleClass().add("form-label");
        convStatusLabel = new Label("Ready");
        convStatusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox convStatusBox = new HBox(10, convStatusTitle, convStatusLabel);
        convStatusBox.setAlignment(Pos.CENTER_LEFT);

        convLogArea = new TextArea();
        convLogArea.setEditable(false);
        convLogArea.setWrapText(true);
        convLogArea.setPromptText("FFmpeg conversion logs will stream here...");
        VBox.setVgrow(convLogArea, Priority.ALWAYS);

        convCard.getChildren().addAll(
                convCardTitle,
                convFileLabel, convFileSelectBox,
                convTargetLabel, convTypeBox,
                convButton,
                convProgressBar,
                convStatusBox,
                convLogArea
        );
        convContainer.getChildren().add(convCard);
        scrollConvert.setContent(convContainer);
        tabConvert.setContent(scrollConvert);

        // Add tabs to tabPane
        tabPane.getTabs().addAll(tabDownload, tabCut, tabConvert);

        root.getChildren().addAll(header, tabPane);

        Scene scene = new Scene(root, 780, 700);

        // Load CSS resource
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Failed to load CSS stylesheet: " + ex.getMessage());
        }

        // ==================== EVENT HANDLERS ====================

        // Tab 1 Download Action
        downloadButton.setOnAction(e -> {
            String link = linkField.getText().trim();
            if (link.isEmpty()) {
                showError("Error", "Please enter a valid media URL.");
                return;
            }

            boolean isVideo = rbVideo.isSelected();
            boolean isAudio = rbAudio.isSelected();
            boolean isScript = rbScript.isSelected();
            boolean isImage = rbImage.isSelected();

            setDownloadUIState(true);
            progressBar.setProgress(0);
            logArea.clear();
            statusLabel.setText("Starting process...");

            Task<Void> downloadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    if (isImage) {
                        downloadImages(link);
                    } else if (isAudio) {
                        downloadAudio(link);
                    } else if (isScript) {
                        downloadTranscript(link);
                    } else {
                        downloadVideo(link);
                    }
                    return null;
                }
            };

            downloadTask.setOnFailed(event -> {
                Throwable ex = downloadTask.getException();
                logArea.appendText("\n[ERROR] An unexpected error occurred: " + ex.getMessage() + "\n");
                statusLabel.setText("Error occurred.");
                setDownloadUIState(false);
            });

            downloadTask.setOnSucceeded(event -> {
                setDownloadUIState(false);
            });

            new Thread(downloadTask).start();
        });

        // Tab 2 File Selector Action
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Media File to Cut");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Media Files", "*.mp4", "*.webm", "*.mkv", "*.avi", "*.mov", "*.mp3", "*.wav", "*.m4a", "*.flac", "*.ogg"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Tab 2 Trim Action
        trimButton.setOnAction(e -> {
            String filePath = filePathField.getText().trim();
            if (filePath.isEmpty()) {
                showError("Error", "Please select a media file to trim.");
                return;
            }

            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (startTime.isEmpty() || endTime.isEmpty()) {
                showError("Error", "Start and end time codes cannot be empty.");
                return;
            }

            double startSec, endSec;
            try {
                startSec = timeToSeconds(startTime);
                endSec = timeToSeconds(endTime);
            } catch (Exception ex) {
                showError("Error", "Invalid time format. Example formats:\n- 00:01:30 (hh:mm:ss)\n- 90 (seconds)");
                return;
            }

            if (startSec < 0) startSec = 0;
            if (endSec <= startSec) {
                showError("Error", "End time (" + endTime + ") must be greater than start time (" + startTime + ").");
                return;
            }

            setTrimUIState(true);
            trimLogArea.clear();
            trimStatusLabel.setText("Starting process...");

            double finalStartSec = startSec;
            double finalEndSec = endSec;

            Task<Void> trimTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    runTrim(filePath, finalStartSec, finalEndSec);
                    return null;
                }
            };

            trimTask.setOnFailed(event -> {
                Throwable ex = trimTask.getException();
                trimLogArea.appendText("\n[ERROR] An unexpected error occurred: " + ex.getMessage() + "\n");
                trimStatusLabel.setText("Error occurred.");
                setTrimUIState(false);
            });

            trimTask.setOnSucceeded(event -> {
                setTrimUIState(false);
            });

            new Thread(trimTask).start();
        });

        // Tab 3 File Selector Action
        convBrowseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Media File to Convert");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Media Files", "*.mkv", "*.webm", "*.mp4", "*.avi", "*.mov", "*.flv", "*.ts", "*.m4v", "*.wmv", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg"),
                    new FileChooser.ExtensionFilter("Video Files", "*.mkv", "*.webm", "*.mp4", "*.avi", "*.mov", "*.flv", "*.ts", "*.m4v", "*.wmv"),
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                convFilePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Tab 3 Convert Action
        convButton.setOnAction(e -> {
            String filePath = convFilePathField.getText().trim();
            if (filePath.isEmpty()) {
                showError("Error", "Please select a media file to convert.");
                return;
            }

            String targetFormat = "mp4";
            if (rbConvMp3.isSelected()) targetFormat = "mp3";
            else if (rbConvWebm.isSelected()) targetFormat = "webm";
            else if (rbConvWav.isSelected()) targetFormat = "wav";

            final String finalTargetFormat = targetFormat;
            setConvertUIState(true);
            convLogArea.clear();
            convStatusLabel.setText("Starting process...");
            convProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

            Task<Void> convTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    runConvert(filePath, finalTargetFormat);
                    return null;
                }
            };

            convTask.setOnFailed(event -> {
                Throwable ex = convTask.getException();
                convLogArea.appendText("\n[ERROR] An unexpected error occurred: " + ex.getMessage() + "\n");
                convStatusLabel.setText("Error occurred.");
                convProgressBar.setProgress(0);
                setConvertUIState(false);
            });

            convTask.setOnSucceeded(event -> {
                setConvertUIState(false);
            });

            new Thread(convTask).start();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setDownloadUIState(boolean running) {
        Platform.runLater(() -> {
            linkField.setDisable(running);
            rbVideo.setDisable(running);
            rbAudio.setDisable(running);
            rbScript.setDisable(running);
            rbImage.setDisable(running);
            downloadButton.setDisable(running);
        });
    }

    private void setTrimUIState(boolean running) {
        Platform.runLater(() -> {
            browseButton.setDisable(running);
            startTimeField.setDisable(running);
            endTimeField.setDisable(running);
            trimToMp4CheckBox.setDisable(running);
            trimButton.setDisable(running);
        });
    }

    private void setConvertUIState(boolean running) {
        Platform.runLater(() -> {
            convBrowseButton.setDisable(running);
            rbConvMp4.setDisable(running);
            rbConvMp3.setDisable(running);
            rbConvWebm.setDisable(running);
            rbConvWav.setDisable(running);
            convButton.setDisable(running);
        });
    }

    // ==================== DOWNLOADER METHODS ====================

    private void downloadVideo(String link) throws Exception {
        Path downloadDir = getDownloadDirectory();
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp");

        if (!isExecutableAvailable(ytDlpPath)) {
            throw new FileNotFoundException(getMissingBinaryMessage("yt-dlp"));
        }

        Path ffmpegPath = resolveExecutablePath("ffmpeg");
        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("Downloading (Video)...");
            logArea.appendText("Starting video download: " + normalizedLink + "\n");
        });

        List<String> cmd = new ArrayList<>();
        cmd.add(ytDlpPath.toString());
        if (isExecutableAvailable(ffmpegPath)) {
            cmd.add("--ffmpeg-location");
            cmd.add(ffmpegPath.getParent() != null ? ffmpegPath.getParent().toString() : ffmpegPath.toString());
        }
        cmd.add("--merge-output-format");
        cmd.add("mp4");
        cmd.add("--remux-video");
        cmd.add("mp4");
        cmd.add("-P");
        cmd.add(downloadDir.toString());
        cmd.add(normalizedLink);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                if (line.contains("[download]") && line.contains("%")) {
                    Pattern p = Pattern.compile("(\\d+(\\.\\d+)?)%");
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        try {
                            double percent = Double.parseDouble(m.group(1));
                            Platform.runLater(() -> progressBar.setProgress(percent / 100.0));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
            }
        }

        int exitCode = process.waitFor();
        Platform.runLater(() -> {
            if (exitCode == 0) {
                statusLabel.setText("Success! Video downloaded.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[SUCCESS] Video download completed!\nSaved to: '" + downloadDir.toAbsolutePath() + "'\n");
            } else {
                statusLabel.setText("Failed! Download error.");
                progressBar.setProgress(0);
                logArea.appendText("\n[ERROR] yt-dlp exited with error code: " + exitCode + "\n");
            }
        });
    }

    private void downloadAudio(String link) throws Exception {
        Path downloadDir = getDownloadDirectory();
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp");

        if (!isExecutableAvailable(ytDlpPath)) {
            throw new FileNotFoundException(getMissingBinaryMessage("yt-dlp"));
        }

        Path ffmpegPath = resolveExecutablePath("ffmpeg");
        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("Downloading (Audio/MP3)...");
            logArea.appendText("Starting audio extraction: " + normalizedLink + "\n");
        });

        List<String> cmd = new ArrayList<>();
        cmd.add(ytDlpPath.toString());
        if (isExecutableAvailable(ffmpegPath)) {
            cmd.add("--ffmpeg-location");
            cmd.add(ffmpegPath.getParent() != null ? ffmpegPath.getParent().toString() : ffmpegPath.toString());
        }
        cmd.add("--extract-audio");
        cmd.add("--audio-format");
        cmd.add("mp3");
        cmd.add("--audio-quality");
        cmd.add("0");
        cmd.add("-P");
        cmd.add(downloadDir.toString());
        cmd.add(normalizedLink);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                if (line.contains("[download]") && line.contains("%")) {
                    Pattern p = Pattern.compile("(\\d+(\\.\\d+)?)%");
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        try {
                            double percent = Double.parseDouble(m.group(1));
                            Platform.runLater(() -> progressBar.setProgress(percent / 100.0));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
            }
        }

        int exitCode = process.waitFor();
        Platform.runLater(() -> {
            if (exitCode == 0) {
                statusLabel.setText("Success! MP3 downloaded.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[SUCCESS] Audio download and conversion completed!\nSaved to: '" + downloadDir.toAbsolutePath() + "'\n");
            } else {
                statusLabel.setText("Failed! Audio download error.");
                progressBar.setProgress(0);
                logArea.appendText("\n[ERROR] yt-dlp exited with error code: " + exitCode + "\n");
            }
        });
    }

    private void downloadTranscript(String link) throws Exception {
        Path downloadDir = getDownloadDirectory().resolve("transcripts");
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp");

        if (!isExecutableAvailable(ytDlpPath)) {
            throw new FileNotFoundException(getMissingBinaryMessage("yt-dlp"));
        }

        Path ffmpegPath = resolveExecutablePath("ffmpeg");
        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("Extracting Script/Transcript...");
            logArea.appendText("Starting transcript download: " + normalizedLink + "\n");
        });

        List<String> cmd = new ArrayList<>();
        cmd.add(ytDlpPath.toString());
        if (isExecutableAvailable(ffmpegPath)) {
            cmd.add("--ffmpeg-location");
            cmd.add(ffmpegPath.getParent() != null ? ffmpegPath.getParent().toString() : ffmpegPath.toString());
        }
        cmd.add("--skip-download");
        cmd.add("--write-subs");
        cmd.add("--write-auto-subs");
        cmd.add("--sub-langs");
        cmd.add("tr,en,.*-orig");
        cmd.add("--convert-subs");
        cmd.add("srt");
        cmd.add("--ignore-errors");
        cmd.add("-o");
        cmd.add("%(playlist_index&{:02d} - |)s%(title)s [%(id)s].%(ext)s");
        cmd.add("-P");
        cmd.add(downloadDir.toString());
        cmd.add(normalizedLink);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                if (line.contains("[download]") && line.contains("%")) {
                    Pattern p = Pattern.compile("(\\d+(\\.\\d+)?)%");
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        try {
                            double percent = Double.parseDouble(m.group(1));
                            Platform.runLater(() -> progressBar.setProgress(percent / 100.0));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
            }
        }

        int exitCode = process.waitFor();

        // Convert downloaded .srt files to clean .txt files
        int convertedCount = 0;
        try (var stream = Files.list(downloadDir)) {
            List<Path> srtFiles = stream.filter(p -> p.toString().toLowerCase().endsWith(".srt")).toList();
            for (Path srt : srtFiles) {
                if (convertSrtToCleanText(srt)) {
                    convertedCount++;
                }
            }
        } catch (Exception ex) {
            Platform.runLater(() -> logArea.appendText("[WARNING] Text conversion notice: " + ex.getMessage() + "\n"));
        }

        final int totalConverted = convertedCount;
        Platform.runLater(() -> {
            if (exitCode == 0 || totalConverted > 0) {
                statusLabel.setText("Success! Script(s) downloaded.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[SUCCESS] Script / Transcript extraction completed!\n"
                        + "Subtitle (.srt) and clean text (.txt) files saved to: '" + downloadDir.toAbsolutePath() + "'\n");
            } else {
                statusLabel.setText("Failed! No transcript found.");
                progressBar.setProgress(0);
                logArea.appendText("\n[ERROR] yt-dlp exited with code: " + exitCode + " or no subtitles were available.\n");
            }
        });
    }

    private boolean convertSrtToCleanText(Path srtFile) {
        try {
            String fileName = srtFile.getFileName().toString();
            int dot = fileName.lastIndexOf('.');
            String baseName = (dot > 0) ? fileName.substring(0, dot) : fileName;
            Path txtPath = srtFile.getParent().resolve(baseName + ".txt");

            if (Files.exists(txtPath) && Files.getLastModifiedTime(txtPath).toMillis() >= Files.getLastModifiedTime(srtFile).toMillis()) {
                return false;
            }

            List<String> lines = Files.readAllLines(srtFile, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            String lastLine = "";

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                if (trimmed.matches("^\\d+$")) continue;
                if (trimmed.contains("-->")) continue;
                trimmed = trimmed.replaceAll("<[^>]*>", "").trim();
                if (trimmed.isEmpty()) continue;

                if (!trimmed.equalsIgnoreCase(lastLine)) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(trimmed);
                    lastLine = trimmed;
                }
            }

            Files.writeString(txtPath, sb.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void downloadImages(String url) throws Exception {
        Path downloadDir = getDownloadDirectory().resolve("images");
        Files.createDirectories(downloadDir);

        Platform.runLater(() -> {
            statusLabel.setText("Connecting to web page...");
            logArea.appendText("Connecting via Jsoup: " + url + "\n");
        });

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(15000)
                .get();

        Elements images = doc.select("img");
        int total = images.size();
        Platform.runLater(() -> logArea.appendText("Found images count: " + total + "\n"));

        if (total == 0) {
            Platform.runLater(() -> {
                statusLabel.setText("No images found.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[NOTICE] No downloadable images found on the page.\n");
            });
            return;
        }

        int count = 0;
        for (Element img : images) {
            String imgUrl = img.absUrl("src");
            if (!imgUrl.isEmpty()) {
                count++;
                final int currentCount = count;
                final String finalImgUrl = imgUrl;
                Platform.runLater(() -> logArea.appendText("[" + currentCount + "/" + total + "] Downloading: " + finalImgUrl + "\n"));

                try {
                    downloadFile(imgUrl, downloadDir.resolve("image_" + currentCount + ".jpg").toString());
                } catch (IOException e) {
                    Platform.runLater(() -> logArea.appendText("[ERROR] Failed to download: " + finalImgUrl + " - " + e.getMessage() + "\n"));
                }

                final double progress = (double) currentCount / total;
                Platform.runLater(() -> progressBar.setProgress(progress));
            }
        }

        final int finalCount = count;
        Platform.runLater(() -> {
            statusLabel.setText("Success! Images downloaded.");
            logArea.appendText("\n[SUCCESS] Total " + finalCount + " images saved to: '" + downloadDir.toAbsolutePath() + "'\n");
        });
    }

    private static void downloadFile(String fileUrl, String fileName) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    private String normalizeYouTubeLink(String link) {
        String shortsPrefix = "https://www.youtube.com/shorts/";
        if (link != null && link.startsWith(shortsPrefix)) {
            String videoId = link.substring(shortsPrefix.length());
            int queryIndex = videoId.indexOf('?');
            if (queryIndex >= 0) {
                videoId = videoId.substring(0, queryIndex);
            }

            int slashIndex = videoId.indexOf('/');
            if (slashIndex >= 0) {
                videoId = videoId.substring(0, slashIndex);
            }

            return "https://www.youtube.com/watch?v=" + videoId;
        }
        return link;
    }

    // ==================== TRIMMER METHODS ====================

    private void runTrim(String filePath, double startSeconds, double endSeconds) throws Exception {
        Path inputPath = Paths.get(filePath);
        if (!Files.exists(inputPath) || Files.isDirectory(inputPath)) {
            throw new FileNotFoundException("Selected media file not found: " + filePath);
        }

        double duration = endSeconds - startSeconds;

        Path parent = inputPath.getParent();
        if (parent == null) parent = Paths.get(".");

        String baseName = inputPath.getFileName().toString();
        int dot = baseName.lastIndexOf('.');
        String nameNoExt = (dot > 0) ? baseName.substring(0, dot) : baseName;
        String ext = (dot > 0) ? baseName.substring(dot) : "";

        boolean convertToMp4 = trimToMp4CheckBox != null && trimToMp4CheckBox.isSelected();
        boolean isAudioOnly = ext.equalsIgnoreCase(".mp3") || ext.equalsIgnoreCase(".wav") || ext.equalsIgnoreCase(".flac")
                || ext.equalsIgnoreCase(".ogg") || ext.equalsIgnoreCase(".m4a") || ext.equalsIgnoreCase(".aac");

        String targetExt = (convertToMp4 && !isAudioOnly) ? ".mp4" : ext;
        String outName = nameNoExt + "_trimmed" + targetExt;
        Path outPath = parent.resolve(outName);

        if (Files.exists(outPath)) {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            outName = nameNoExt + "_trimmed_" + ts + targetExt;
            outPath = parent.resolve(outName);
        }

        Path ffmpegPath = resolveExecutablePath("ffmpeg");
        if (!isExecutableAvailable(ffmpegPath)) {
            throw new FileNotFoundException(getMissingBinaryMessage("ffmpeg"));
        }

        List<String> cmd;
        if (convertToMp4 && !isAudioOnly && !ext.equalsIgnoreCase(".mp4")) {
            // Re-encode video to universal web-compatible MP4 (H.264 / AAC)
            cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-ss", String.valueOf(startSeconds),
                    "-i", inputPath.toString(),
                    "-t", String.valueOf(duration),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "22",
                    "-c:a", "aac",
                    "-b:a", "192k",
                    "-movflags", "+faststart",
                    outPath.toString()
            );
        } else {
            // Fast direct stream copy
            cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-ss", String.valueOf(startSeconds),
                    "-i", inputPath.toString(),
                    "-t", String.valueOf(duration),
                    "-c", "copy",
                    outPath.toString()
            );
        }

        Platform.runLater(() -> {
            trimStatusLabel.setText("Trimming media...");
            trimLogArea.appendText("Starting FFmpeg...\nCommand: " + String.join(" ", cmd) + "\n\n");
        });

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> trimLogArea.appendText(finalLine + "\n"));
            }
        }

        int exitCode = process.waitFor();
        Path finalOutPath = outPath;
        Platform.runLater(() -> {
            if (exitCode == 0) {
                trimStatusLabel.setText("Success! Media trimmed.");
                trimLogArea.appendText("\n[SUCCESS] Media trimmed successfully!\nSaved to: " + finalOutPath.toAbsolutePath() + "\n");
            } else {
                trimStatusLabel.setText("Failed! Trimming error.");
                trimLogArea.appendText("\n[ERROR] FFmpeg exited with code: " + exitCode + "\n");
            }
        });
    }

    // ==================== CONVERTER METHODS ====================

    private void runConvert(String filePath, String targetFormat) throws Exception {
        Path inputPath = Paths.get(filePath);
        if (!Files.exists(inputPath) || Files.isDirectory(inputPath)) {
            throw new FileNotFoundException("Selected media file not found: " + filePath);
        }

        Path parent = inputPath.getParent();
        if (parent == null) parent = Paths.get(".");

        String baseName = inputPath.getFileName().toString();
        int dot = baseName.lastIndexOf('.');
        String nameNoExt = (dot > 0) ? baseName.substring(0, dot) : baseName;

        String outName = nameNoExt + "_converted." + targetFormat.toLowerCase();
        Path outPath = parent.resolve(outName);

        if (Files.exists(outPath)) {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            outName = nameNoExt + "_converted_" + ts + "." + targetFormat.toLowerCase();
            outPath = parent.resolve(outName);
        }

        Path ffmpegPath = resolveExecutablePath("ffmpeg");
        if (!isExecutableAvailable(ffmpegPath)) {
            throw new FileNotFoundException(getMissingBinaryMessage("ffmpeg"));
        }

        List<String> cmd;
        switch (targetFormat.toLowerCase()) {
            case "mp4" -> cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-i", inputPath.toString(),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", "22",
                    "-c:a", "aac",
                    "-b:a", "192k",
                    "-movflags", "+faststart",
                    outPath.toString()
            );
            case "mp3" -> cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-i", inputPath.toString(),
                    "-vn",
                    "-acodec", "libmp3lame",
                    "-b:a", "320k",
                    outPath.toString()
            );
            case "webm" -> cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-i", inputPath.toString(),
                    "-c:v", "libvpx-vp9",
                    "-crf", "30",
                    "-b:v", "0",
                    "-c:a", "libopus",
                    outPath.toString()
            );
            case "wav" -> cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-i", inputPath.toString(),
                    "-vn",
                    outPath.toString()
            );
            default -> throw new IllegalArgumentException("Unsupported target format: " + targetFormat);
        }

        Platform.runLater(() -> {
            convStatusLabel.setText("Converting (" + targetFormat.toUpperCase() + ")...");
            convProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            convLogArea.appendText("Starting FFmpeg...\nCommand: " + String.join(" ", cmd) + "\n\n");
        });

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> convLogArea.appendText(finalLine + "\n"));
            }
        }

        int exitCode = process.waitFor();
        Path finalOutPath = outPath;
        Platform.runLater(() -> {
            if (exitCode == 0) {
                convStatusLabel.setText("Success! Conversion completed.");
                convProgressBar.setProgress(1.0);
                convLogArea.appendText("\n[SUCCESS] Media converted successfully!\nSaved to: " + finalOutPath.toAbsolutePath() + "\n");
            } else {
                convStatusLabel.setText("Failed! Conversion error.");
                convProgressBar.setProgress(0);
                convLogArea.appendText("\n[ERROR] FFmpeg exited with code: " + exitCode + "\n");
            }
        });
    }

    private double timeToSeconds(String time) {
        if (time == null || time.isBlank()) return 0;
        String t = time.trim();
        if (t.matches("^\\d+(\\.\\d+)?$")) {
            return Double.parseDouble(t);
        }
        String[] parts = t.split(":");
        int len = parts.length;
        double seconds = 0;
        try {
            if (len == 3) { // HH:MM:SS
                seconds += Integer.parseInt(parts[0]) * 3600;
                seconds += Integer.parseInt(parts[1]) * 60;
                seconds += Double.parseDouble(parts[2]);
            } else if (len == 2) { // MM:SS
                seconds += Integer.parseInt(parts[0]) * 60;
                seconds += Double.parseDouble(parts[1]);
            } else if (len == 1) { // SS
                seconds += Double.parseDouble(parts[0]);
            } else {
                throw new IllegalArgumentException("Invalid time format: " + time);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Could not parse time format: " + time, ex);
        }
        return seconds;
    }

    private Path resolveExecutablePath(String baseBinaryName) {
        String cleanName = baseBinaryName.endsWith(".exe")
                ? baseBinaryName.substring(0, baseBinaryName.length() - 4)
                : baseBinaryName;
        String binaryName = IS_WINDOWS ? (cleanName + ".exe") : cleanName;

        // 1. Check current working directory
        Path localPath = Paths.get(binaryName).toAbsolutePath().normalize();
        if (Files.isRegularFile(localPath)) {
            ensureExecutable(localPath);
            return localPath;
        }

        // 2. Check application / JAR directory and its parent
        try {
            File codeSource = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File jarDir = codeSource.isDirectory() ? codeSource : codeSource.getParentFile();
            if (jarDir != null) {
                Path jarPath = jarDir.toPath().resolve(binaryName).toAbsolutePath().normalize();
                if (Files.isRegularFile(jarPath)) {
                    ensureExecutable(jarPath);
                    return jarPath;
                }
                if (jarDir.getParentFile() != null) {
                    Path rootPath = jarDir.getParentFile().toPath().resolve(binaryName).toAbsolutePath().normalize();
                    if (Files.isRegularFile(rootPath)) {
                        ensureExecutable(rootPath);
                        return rootPath;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // 3. Check system PATH directories
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String[] dirs = pathEnv.split(Pattern.quote(File.pathSeparator));
            for (String dir : dirs) {
                if (!dir.isBlank()) {
                    Path p = Paths.get(dir.trim(), binaryName);
                    if (Files.isRegularFile(p)) {
                        ensureExecutable(p);
                        return p.toAbsolutePath().normalize();
                    }
                }
            }
        }

        // 4. Check common Unix / macOS directories
        if (!IS_WINDOWS) {
            String userHome = System.getProperty("user.home", "");
            String[] commonUnixPaths = {
                    "/opt/homebrew/bin",
                    "/usr/local/bin",
                    "/usr/bin",
                    "/bin",
                    userHome + "/.local/bin"
            };
            for (String dir : commonUnixPaths) {
                if (!dir.isBlank()) {
                    Path p = Paths.get(dir, binaryName);
                    if (Files.isRegularFile(p)) {
                        ensureExecutable(p);
                        return p.toAbsolutePath().normalize();
                    }
                }
            }
        }

        // Fallback: return default resolved path (or command name for PATH lookup)
        return localPath;
    }

    private boolean isExecutableAvailable(Path path) {
        if (path == null) return false;
        if (Files.isRegularFile(path)) {
            return !IS_WINDOWS || path.toFile().canExecute();
        }
        try {
            Process p = new ProcessBuilder(List.of(path.toString(), "--version")).start();
            p.destroy();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureExecutable(Path path) {
        if (!IS_WINDOWS && Files.exists(path)) {
            File f = path.toFile();
            if (!f.canExecute()) {
                f.setExecutable(true, false);
            }
        }
    }

    private String getMissingBinaryMessage(String binaryName) {
        String ext = IS_WINDOWS ? ".exe" : "";
        String installHint = IS_WINDOWS
                ? "Please place " + binaryName + ext + " in the application directory or add it to system PATH."
                : (IS_MAC
                ? "Please install via Homebrew ('brew install " + binaryName + "') or place it in the application directory."
                : "Please install via your package manager ('sudo apt install " + binaryName + "') or place it in the application directory.");
        return "'" + binaryName + ext + "' was not found.\n" + installHint;
    }

    private Path getDownloadDirectory() {
        try {
            File jarFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parentDir = jarFile.getParentFile().getParentFile();
            if (parentDir != null && parentDir.exists()) {
                Path dir = parentDir.toPath().resolve("downloads");
                Files.createDirectories(dir);
                return dir;
            }
        } catch (Exception e) {
            // fallback
        }
        try {
            Path dir = Paths.get("downloads").toAbsolutePath().normalize();
            Files.createDirectories(dir);
            return dir;
        } catch (Exception e) {
            // user home fallback
            String userHome = System.getProperty("user.home", ".");
            return Paths.get(userHome, "Downloads", "MediaSuitePro");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
