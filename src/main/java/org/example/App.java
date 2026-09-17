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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {

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
        primaryStage.setTitle("MediaSuite Pro - Downloader, Cutter & Converter");

        // Main layout VBox
        VBox root = new VBox();
        root.getStyleClass().add("root");

        // Header section
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #11111b; -fx-border-color: #313244; -fx-border-width: 0 0 1 0;");

        Label headerTitle = new Label("🚀 MediaSuite Pro");
        headerTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cba6f7;");
        Label headerSub = new Label("  |  İndirici, Tıraşlayıcı & Format Dönüştürücü");
        headerSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #bac2de;");

        header.getChildren().addAll(headerTitle, headerSub);

        // TabPane setup
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ==================== TAB 1: DOWNLOADER ====================
        Tab tabDownload = new Tab("Medya İndirici");
        ScrollPane scrollDownload = new ScrollPane();
        scrollDownload.setFitToWidth(true);
        
        VBox dlContainer = new VBox(20);
        dlContainer.getStyleClass().add("container");

        VBox dlCard = new VBox(15);
        dlCard.getStyleClass().add("card");

        Label dlCardTitle = new Label("Medya İndirme Paneli");
        dlCardTitle.getStyleClass().add("title-label");

        Label urlLabel = new Label("Medya veya Sayfa Linki:");
        urlLabel.getStyleClass().add("form-label");

        linkField = new TextField();
        linkField.setPromptText("İndirmek istediğiniz linki yapıştırın (örn. YouTube, Twitter, Bluesky vb.)...");

        Label typeLabel = new Label("İndirme Türü Seçin:");
        typeLabel.getStyleClass().add("form-label");

        ToggleGroup typeGroup = new ToggleGroup();
        rbVideo = new RadioButton("Video İndir (MP4)");
        rbVideo.setToggleGroup(typeGroup);
        rbVideo.setSelected(true);

        rbAudio = new RadioButton("Ses İndir (MP3)");
        rbAudio.setToggleGroup(typeGroup);

        rbScript = new RadioButton("Script / Transkript (.srt & .txt)");
        rbScript.setToggleGroup(typeGroup);

        rbImage = new RadioButton("Resimleri İndir (Web Sayfası)");
        rbImage.setToggleGroup(typeGroup);

        HBox typeBox = new HBox(15, rbVideo, rbAudio, rbScript, rbImage);
        typeBox.setPadding(new Insets(5, 0, 5, 0));

        downloadButton = new Button("İndirmeyi Başlat");
        downloadButton.setMaxWidth(Double.MAX_VALUE);

        progressBar = new ProgressBar(0.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        Label statusTitle = new Label("Durum:");
        statusTitle.getStyleClass().add("form-label");
        statusLabel = new Label("Hazır");
        statusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox statusBox = new HBox(10, statusTitle, statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPromptText("İndirme ve dönüştürme logları burada akacaktır...");
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
        Tab tabCut = new Tab("Medya Kesici (Tıraşlama)");
        ScrollPane scrollCut = new ScrollPane();
        scrollCut.setFitToWidth(true);

        VBox cutContainer = new VBox(20);
        cutContainer.getStyleClass().add("container");

        VBox cutCard = new VBox(15);
        cutCard.getStyleClass().add("card");

        Label cutCardTitle = new Label("Medya Kesme & Tıraşlama Paneli");
        cutCardTitle.getStyleClass().add("title-label");

        Label fileLabel = new Label("Dosya Seçin:");
        fileLabel.getStyleClass().add("form-label");

        filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setPromptText("Makinenizdeki bir video/ses dosyasını seçin...");
        
        browseButton = new Button("Dosya Seç");
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

        Label startLabel = new Label("Den İtibaren (Başlangıç):");
        startLabel.getStyleClass().add("form-label");
        startTimeField = new TextField("00:00:00");
        startTimeField.setPromptText("saat:dakika:saniye (örn. 00:01:30)");

        Label endLabel = new Label("E Kadar (Bitiş):");
        endLabel.getStyleClass().add("form-label");
        endTimeField = new TextField("00:00:00");
        endTimeField.setPromptText("saat:dakika:saniye (örn. 00:02:15)");

        timesGrid.add(startLabel, 0, 0);
        timesGrid.add(startTimeField, 0, 1);
        timesGrid.add(endLabel, 1, 0);
        timesGrid.add(endTimeField, 1, 1);

        trimToMp4CheckBox = new CheckBox("Çıktıyı Evrensel MP4 Formatına Dönüştür (.mp4)");
        trimToMp4CheckBox.setSelected(true);
        trimToMp4CheckBox.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 13px;");

        trimButton = new Button("Tıraşlamayı Başlat");
        trimButton.setMaxWidth(Double.MAX_VALUE);

        Label trimStatusTitle = new Label("Durum:");
        trimStatusTitle.getStyleClass().add("form-label");
        trimStatusLabel = new Label("Hazır");
        trimStatusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox trimStatusBox = new HBox(10, trimStatusTitle, trimStatusLabel);
        trimStatusBox.setAlignment(Pos.CENTER_LEFT);

        trimLogArea = new TextArea();
        trimLogArea.setEditable(false);
        trimLogArea.setWrapText(true);
        trimLogArea.setPromptText("FFmpeg tıraşlama logları burada akacaktır...");
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
        Tab tabConvert = new Tab("Format Dönüştürücü");
        ScrollPane scrollConvert = new ScrollPane();
        scrollConvert.setFitToWidth(true);

        VBox convContainer = new VBox(20);
        convContainer.getStyleClass().add("container");

        VBox convCard = new VBox(15);
        convCard.getStyleClass().add("card");

        Label convCardTitle = new Label("Medya Format Dönüştürme Paneli");
        convCardTitle.getStyleClass().add("title-label");

        Label convFileLabel = new Label("Dönüştürülecek Medya Dosyası (MKV, WebM, AVI, MOV, MP4, MP3 vb.):");
        convFileLabel.getStyleClass().add("form-label");

        convFilePathField = new TextField();
        convFilePathField.setEditable(false);
        convFilePathField.setPromptText("Makinenizdeki dönüştürülecek bir medya dosyasını seçin...");

        convBrowseButton = new Button("Dosya Seç");
        convBrowseButton.getStyleClass().add("button-secondary");

        HBox convFileSelectBox = new HBox(10, convFilePathField, convBrowseButton);
        HBox.setHgrow(convFilePathField, Priority.ALWAYS);

        Label convTargetLabel = new Label("Hedef Format Seçin:");
        convTargetLabel.getStyleClass().add("form-label");

        ToggleGroup convGroup = new ToggleGroup();
        rbConvMp4 = new RadioButton("MP4 Video (H.264/AAC - İnternet Uyumlu)");
        rbConvMp4.setToggleGroup(convGroup);
        rbConvMp4.setSelected(true);

        rbConvMp3 = new RadioButton("MP3 Ses (320 kbps)");
        rbConvMp3.setToggleGroup(convGroup);

        rbConvWebm = new RadioButton("WebM Video");
        rbConvWebm.setToggleGroup(convGroup);

        rbConvWav = new RadioButton("WAV Ses (Kayıpsız)");
        rbConvWav.setToggleGroup(convGroup);

        HBox convTypeBox = new HBox(15, rbConvMp4, rbConvMp3, rbConvWebm, rbConvWav);
        convTypeBox.setPadding(new Insets(5, 0, 5, 0));

        convButton = new Button("Dönüştürmeyi Başlat");
        convButton.setMaxWidth(Double.MAX_VALUE);

        convProgressBar = new ProgressBar(0.0);
        convProgressBar.setMaxWidth(Double.MAX_VALUE);

        Label convStatusTitle = new Label("Durum:");
        convStatusTitle.getStyleClass().add("form-label");
        convStatusLabel = new Label("Hazır");
        convStatusLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-weight: bold;");

        HBox convStatusBox = new HBox(10, convStatusTitle, convStatusLabel);
        convStatusBox.setAlignment(Pos.CENTER_LEFT);

        convLogArea = new TextArea();
        convLogArea.setEditable(false);
        convLogArea.setWrapText(true);
        convLogArea.setPromptText("FFmpeg dönüştürme logları burada akacaktır...");
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

        Scene scene = new Scene(root, 750, 680);
        
        // Load external CSS resource
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("CSS dosyası yüklenemedi: " + ex.getMessage());
        }

        // ==================== EVENTS ====================
        
        // Tab 1 Download Action
        downloadButton.setOnAction(e -> {
            String link = linkField.getText().trim();
            if (link.isEmpty()) {
                showError("Hata", "Lütfen geçerli bir medya linki girin.");
                return;
            }

            boolean isVideo = rbVideo.isSelected();
            boolean isAudio = rbAudio.isSelected();
            boolean isScript = rbScript.isSelected();
            boolean isImage = rbImage.isSelected();

            setDownloadUIState(true);
            progressBar.setProgress(0);
            logArea.clear();
            statusLabel.setText("İşlem başlatıldı...");

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
                logArea.appendText("\n[HATA] Beklenmedik bir hata oluştu: " + ex.getMessage() + "\n");
                statusLabel.setText("Hata oluştu.");
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
            fileChooser.setTitle("Kesilecek Medya Dosyasını Seçin");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Medya Dosyaları", "*.mp4", "*.webm", "*.mkv", "*.avi", "*.mp3", "*.wav", "*.m4a"),
                    new FileChooser.ExtensionFilter("Tüm Dosyalar", "*.*")
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
                showError("Hata", "Lütfen tıraşlanacak bir medya dosyası seçin.");
                return;
            }

            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (startTime.isEmpty() || endTime.isEmpty()) {
                showError("Hata", "Başlangıç ve bitiş zaman kodları boş olamaz.");
                return;
            }

            double startSec, endSec;
            try {
                startSec = timeToSeconds(startTime);
                endSec = timeToSeconds(endTime);
            } catch (Exception ex) {
                showError("Hata", "Zaman formatı geçersiz. Örnek formatlar:\n- 00:01:30 (saat:dakika:saniye)\n- 90 (saniye)");
                return;
            }

            if (startSec < 0) startSec = 0;
            if (endSec <= startSec) {
                showError("Hata", "Bitiş zamanı (" + endTime + "), başlangıç zamanından (" + startTime + ") büyük olmalıdır.");
                return;
            }

            setTrimUIState(true);
            trimLogArea.clear();
            trimStatusLabel.setText("İşlem başlatıldı...");

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
                trimLogArea.appendText("\n[HATA] Beklenmedik bir hata oluştu: " + ex.getMessage() + "\n");
                trimStatusLabel.setText("Hata oluştu.");
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
            fileChooser.setTitle("Dönüştürülecek Medya Dosyasını Seçin");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Tüm Medya Dosyaları", "*.mkv", "*.webm", "*.mp4", "*.avi", "*.mov", "*.flv", "*.ts", "*.m4v", "*.wmv", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg"),
                    new FileChooser.ExtensionFilter("Video Dosyaları", "*.mkv", "*.webm", "*.mp4", "*.avi", "*.mov", "*.flv", "*.ts", "*.m4v", "*.wmv"),
                    new FileChooser.ExtensionFilter("Ses Dosyaları", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg"),
                    new FileChooser.ExtensionFilter("Tüm Dosyalar", "*.*")
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
                showError("Hata", "Lütfen dönüştürülecek bir medya dosyası seçin.");
                return;
            }

            String targetFormat = "mp4";
            if (rbConvMp3.isSelected()) targetFormat = "mp3";
            else if (rbConvWebm.isSelected()) targetFormat = "webm";
            else if (rbConvWav.isSelected()) targetFormat = "wav";

            final String finalTargetFormat = targetFormat;
            setConvertUIState(true);
            convLogArea.clear();
            convStatusLabel.setText("İşlem başlatıldı...");
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
                convLogArea.appendText("\n[HATA] Beklenmedik bir hata oluştu: " + ex.getMessage() + "\n");
                convStatusLabel.setText("Hata oluştu.");
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

    // Downloader helper methods
    private void downloadVideo(String link) throws Exception {
        Path downloadDir = getDownloadDirectory();
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp.exe");

        if (!Files.exists(ytDlpPath)) {
            throw new FileNotFoundException("yt-dlp.exe dosyası bulunamadı. Lütfen program veya uygulama klasöründe olduğundan emin olun.");
        }

        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("İndiriliyor (Video)...");
            logArea.appendText("İndirme işlemi başlatılıyor: " + normalizedLink + "\n");
        });

        ProcessBuilder pb = new ProcessBuilder(
                List.of(
                        ytDlpPath.toString(),
                        "--merge-output-format", "mp4",
                        "--remux-video", "mp4",
                        "-P", downloadDir.toString(),
                        normalizedLink
                )
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                // Parse progress from yt-dlp output
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
                statusLabel.setText("Başarılı! Video indirildi.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[BAŞARILI] Video indirme tamamlandı!\nDosya '" + downloadDir.toAbsolutePath() + "' klasörüne kaydedildi.\n");
            } else {
                statusLabel.setText("Hata! İndirme başarısız.");
                progressBar.setProgress(0);
                logArea.appendText("\n[HATA] yt-dlp hata kodu döndürdü: " + exitCode + "\n");
            }
        });
    }

    private void downloadAudio(String link) throws Exception {
        Path downloadDir = getDownloadDirectory();
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp.exe");

        if (!Files.exists(ytDlpPath)) {
            throw new FileNotFoundException("yt-dlp.exe dosyası bulunamadı. Lütfen program veya uygulama klasöründe olduğundan emin olun.");
        }

        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("İndiriliyor (Ses/MP3)...");
            logArea.appendText("İndirme ve MP3 dönüştürme başlatılıyor: " + normalizedLink + "\n");
        });

        ProcessBuilder pb = new ProcessBuilder(
                List.of(
                        ytDlpPath.toString(),
                        "--extract-audio",
                        "--audio-format", "mp3",
                        "--audio-quality", "0",
                        "-P", downloadDir.toString(),
                        normalizedLink
                )
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                // Parse progress from yt-dlp output
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
                statusLabel.setText("Başarılı! MP3 indirildi.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[BAŞARILI] MP3 indirme ve dönüştürme tamamlandı!\nDosya '" + downloadDir.toAbsolutePath() + "' klasörüne kaydedildi.\n");
            } else {
                statusLabel.setText("Hata! İndirme başarısız.");
                progressBar.setProgress(0);
                logArea.appendText("\n[HATA] yt-dlp hata kodu döndürdü: " + exitCode + "\n");
            }
        });
    }

    private void downloadImages(String url) throws Exception {
        Path downloadDir = getDownloadDirectory().resolve("resim_indirilenler");
        Files.createDirectories(downloadDir);

        Platform.runLater(() -> {
            statusLabel.setText("Sayfaya bağlanılıyor...");
            logArea.appendText("JSoup ile bağlanılıyor: " + url + "\n");
        });

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(15000)
                .get();

        Elements images = doc.select("img");
        int total = images.size();
        Platform.runLater(() -> logArea.appendText("Bulunan resim sayısı: " + total + "\n"));

        if (total == 0) {
            Platform.runLater(() -> {
                statusLabel.setText("Resim bulunamadı.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[UYARI] Sayfada indirilecek resim (img etiketi) bulunamadı.\n");
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
                Platform.runLater(() -> logArea.appendText("[" + currentCount + "/" + total + "] İndiriliyor: " + finalImgUrl + "\n"));

                try {
                    downloadFile(imgUrl, downloadDir.resolve("resim_" + currentCount + ".jpg").toString());
                } catch (IOException e) {
                    Platform.runLater(() -> logArea.appendText("[HATA] İndirilemedi: " + finalImgUrl + " - " + e.getMessage() + "\n"));
                }

                final double progress = (double) currentCount / total;
                Platform.runLater(() -> progressBar.setProgress(progress));
            }
        }

        final int finalCount = count;
        Platform.runLater(() -> {
            statusLabel.setText("Başarılı! Resimler indirildi.");
            logArea.appendText("\n[BAŞARILI] Toplam " + finalCount + " resim '" + downloadDir.toAbsolutePath() + "' klasörüne indirildi.\n");
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

    private void downloadTranscript(String link) throws Exception {
        Path downloadDir = getDownloadDirectory().resolve("transcripts");
        Files.createDirectories(downloadDir);
        Path ytDlpPath = resolveExecutablePath("yt-dlp.exe");

        if (!Files.exists(ytDlpPath)) {
            throw new FileNotFoundException("yt-dlp.exe dosyası bulunamadı. Lütfen program veya uygulama klasöründe olduğundan emin olun.");
        }

        String normalizedLink = normalizeYouTubeLink(link);
        Platform.runLater(() -> {
            statusLabel.setText("Script/Transkript çekiliyor...");
            logArea.appendText("Transkript indirme başlatılıyor: " + normalizedLink + "\n");
        });

        ProcessBuilder pb = new ProcessBuilder(
                List.of(
                        ytDlpPath.toString(),
                        "--skip-download",
                        "--write-subs",
                        "--write-auto-subs",
                        "--sub-langs", "tr,en,.*-orig",
                        "--convert-subs", "srt",
                        "--ignore-errors",
                        "-o", "%(playlist_index&{:02d} - |)s%(title)s [%(id)s].%(ext)s",
                        "-P", downloadDir.toString(),
                        normalizedLink
                )
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String finalLine = line;
                Platform.runLater(() -> logArea.appendText(finalLine + "\n"));

                // Parse progress
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
            Platform.runLater(() -> logArea.appendText("[UYARI] Metin dönüştürme uyarısı: " + ex.getMessage() + "\n"));
        }

        final int totalConverted = convertedCount;
        Platform.runLater(() -> {
            if (exitCode == 0 || totalConverted > 0) {
                statusLabel.setText("Başarılı! Script(ler) indirildi.");
                progressBar.setProgress(1.0);
                logArea.appendText("\n[BAŞARILI] Script / Transkript çekme tamamlandı!\n"
                        + "Altyazı (.srt) ve temiz metin (.txt) dosyaları '" + downloadDir.toAbsolutePath() + "' klasörüne kaydedildi.\n");
            } else {
                statusLabel.setText("Hata! Script indirilemedi.");
                progressBar.setProgress(0);
                logArea.appendText("\n[HATA] yt-dlp hata kodu döndürdü veya videoda altyazı/transkript bulunamadı. Çıkış kodu: " + exitCode + "\n");
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

    // Trimmer helper methods
    private void runTrim(String dosyaYolu, double saniyedenItibaren, double saniyeyeKadar) throws Exception {
        Path inputPath = Paths.get(dosyaYolu);
        if (!Files.exists(inputPath) || Files.isDirectory(inputPath)) {
            throw new FileNotFoundException("Seçilen medya dosyası bulunamadı: " + dosyaYolu);
        }

        double duration = saniyeyeKadar - saniyedenItibaren;

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

        Path ffmpegPath = resolveExecutablePath("ffmpeg.exe");
        if (!Files.exists(ffmpegPath)) {
            throw new FileNotFoundException("ffmpeg.exe dosyası bulunamadı. Lütfen program veya uygulama klasöründe olduğundan emin olun.");
        }

        List<String> cmd;
        if (convertToMp4 && !isAudioOnly && !ext.equalsIgnoreCase(".mp4")) {
            // Re-encode video to universal web-compatible MP4 (H.264 / AAC)
            cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-ss", String.valueOf(saniyedenItibaren),
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
                    "-ss", String.valueOf(saniyedenItibaren),
                    "-i", inputPath.toString(),
                    "-t", String.valueOf(duration),
                    "-c", "copy",
                    outPath.toString()
            );
        }

        Platform.runLater(() -> {
            trimStatusLabel.setText("Medya tıraşlanıyor...");
            trimLogArea.appendText("FFmpeg Başlatılıyor...\nKomut: " + String.join(" ", cmd) + "\n\n");
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
                trimStatusLabel.setText("Başarılı! Tıraşlama tamamlandı.");
                trimLogArea.appendText("\n[BAŞARILI] Medya başarıyla tıraşlandı!\nYeni Dosya: " + finalOutPath.toAbsolutePath() + "\n");
            } else {
                trimStatusLabel.setText("Hata! İşlem başarısız.");
                trimLogArea.appendText("\n[HATA] FFmpeg hata kodu döndürdü: " + exitCode + "\n");
            }
        });
    }

    // Converter helper methods
    private void runConvert(String dosyaYolu, String targetFormat) throws Exception {
        Path inputPath = Paths.get(dosyaYolu);
        if (!Files.exists(inputPath) || Files.isDirectory(inputPath)) {
            throw new FileNotFoundException("Seçilen medya dosyası bulunamadı: " + dosyaYolu);
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

        Path ffmpegPath = resolveExecutablePath("ffmpeg.exe");
        if (!Files.exists(ffmpegPath)) {
            throw new FileNotFoundException("ffmpeg.exe dosyası bulunamadı. Lütfen program veya uygulama klasöründe olduğundan emin olun.");
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
            default -> throw new IllegalArgumentException("Desteklenmeyen hedef format: " + targetFormat);
        }

        Platform.runLater(() -> {
            convStatusLabel.setText("Dönüştürülüyor (" + targetFormat.toUpperCase() + ")...");
            convProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            convLogArea.appendText("FFmpeg Başlatılıyor...\nKomut: " + String.join(" ", cmd) + "\n\n");
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
                convStatusLabel.setText("Başarılı! Dönüştürme tamamlandı.");
                convProgressBar.setProgress(1.0);
                convLogArea.appendText("\n[BAŞARILI] Dosya başarıyla dönüştürüldü!\nYeni Dosya: " + finalOutPath.toAbsolutePath() + "\n");
            } else {
                convStatusLabel.setText("Hata! İşlem başarısız.");
                convProgressBar.setProgress(0);
                convLogArea.appendText("\n[HATA] FFmpeg hata kodu döndürdü: " + exitCode + "\n");
            }
        });
    }

    private double timeToSeconds(String time) {
        if (time == null || time.isBlank()) return 0;
        String t = time.trim();
        // If it's only digits, treat as seconds
        if (t.matches("^\\d+$")) {
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
                throw new IllegalArgumentException("Geçersiz zaman formatı: " + time);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Zaman kodu sayıya dönüştürülemedi: " + time, ex);
        }
        return seconds;
    }

    private Path resolveExecutablePath(String filename) {
        // 1. Check current working directory
        Path path = Paths.get(filename).toAbsolutePath().normalize();
        if (Files.exists(path)) {
            return path;
        }
        // 2. Check next to the jar/exe file
        try {
            String jarDir = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            Path jarPath = Paths.get(jarDir, filename).toAbsolutePath().normalize();
            if (Files.exists(jarPath)) {
                return jarPath;
            }
        } catch (Exception e) {
            // ignore
        }
        // 3. Fallback
        return path;
    }

    private Path getDownloadDirectory() {
        try {
            File jarFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // jarFile.getParentFile() is AppDir/app/
            // jarFile.getParentFile().getParentFile() is AppDir
            File parentDir = jarFile.getParentFile().getParentFile();
            if (parentDir != null && parentDir.exists()) {
                return parentDir.toPath().resolve("downloads");
            }
        } catch (Exception e) {
            // fallback
        }
        return Paths.get("downloads").toAbsolutePath().normalize();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
