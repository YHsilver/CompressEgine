package Compress;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HuffWeight {
    public static final int BUFFER_SIZE = 1 << 18;
    int weight[] = new int[256];
    File file;
    int count;

    //store the data(byte) and weight
    ArrayList<Node<String>> list;


    HuffWeight(File file) throws Exception {
        list = new ArrayList<>();
        this.file = file;
        if (!file.exists()) {
            throw new Exception("file not exist");
        }
        countChar();
    }

    /**
     * count each byte's  frequency
     * @throws IOException
     */
    public void countChar() throws IOException {
        int intchar;
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[BUFFER_SIZE];
        while ((intchar = fis.read(bytes)) != -1) {
            for (int i = 0; i < intchar; i++) {
                int tmp = bytes[i] & 0xff;
                weight[tmp]++;
            }
        }
        fis.close();
        for (int i = 0; i < 256; i++) {
            if (weight[i] != 0) {
                count++;
                Node<String> node = new Node((char) i, weight[i]);
                list.add(node);
            }
        }
    }
}
