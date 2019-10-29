package Compress;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.text.DecimalFormat;


public class Controller {
    public Button compress;
    public Button decompress;
    public Pane mainPane;
    public TextField compText;
    public TextField decText;
    public TextArea info;
    public TextField newName;
    private Stage stage;

    /**
     * event handler, compress the cheesed file/folder
     * if given a new file name, let it be the new compressed file name
     * if not given,default as new_yyFile.yy
     * print the time takes and compress efficiency
     * all info is printed on the textArea
     */
    public void compressClick() {
        long start = System.currentTimeMillis();
        if (!compText.getText() .equals("") && !decText.getText() .equals("")) {
            try {
                String source = decText.getText();
                if (!newName.getText().equals("")) source += "\\" + newName.getText() + ".yy";
                else source += "\\new_yyFile.yy";
                info.appendText("compressing...\n");
                long originBytes=(new File(compText.getText()).length());
                Compress.doCompress(compText.getText(), source);
                long compressedBytes=(new File(source).length());
                double efficiency=(double) compressedBytes/(double) originBytes;

                DecimalFormat df = new DecimalFormat("0.00");
                info.appendText("finish compress, time takes:  " + (System.currentTimeMillis() - start) + "ms\n");
                info.appendText("compress efficiency: "+(df.format(efficiency*100))+"%");
            } catch (Exception e) {
                info.appendText("ERROR: "+e.getMessage()+"\n");
            }
        }else info.appendText("Please choose path\n");
    }

    /**
     * event handler, decompress the cheesed compressed file
     * print the time takes
     */
    public void decompressClick() {
        long start = System.currentTimeMillis();
        if (!compText.getText() .equals("") && !decText.getText() .equals("")) {
            try {
                info.appendText("decompressing...\n");
                Decompress.doDecompress(compText.getText(), decText.getText());
                info.appendText("finish decompress, time takes:  " + (System.currentTimeMillis() - start) + "ms\n");
            } catch (Exception e) {
                info.appendText("ERROR: "+e.getMessage()+"\n");
            }
        }else info.appendText("Please choose path\n");
    }

    //choose the decompressed file path
    public void decChoose(ActionEvent actionEvent) {
        stage = (Stage) mainPane.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件");
        File selectedFolder = directoryChooser.showDialog(stage);
        if (selectedFolder != null) {
            String path = selectedFolder.getAbsolutePath();
            decText.clear();
            decText.appendText(path);
        }
    }

    //choose the file to be compressed
    public void comFileChoose(ActionEvent actionEvent) {
        stage = (Stage) mainPane.getScene().getWindow();
        FileChooser sourFileChooser = new FileChooser();
        sourFileChooser.setTitle("选择文件");
        File selectedFile = sourFileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            compText.clear();
            compText.appendText(path);
        }
    }

    //choose the folder to be compressed
    public void comFolderChoose(ActionEvent actionEvent) {
        stage = (Stage) mainPane.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件");
        File selectedFolder = directoryChooser.showDialog(stage);
        if (selectedFolder != null) {
            String path = selectedFolder.getAbsolutePath();
            compText.clear();
            compText.appendText(path);
        }
    }
}
