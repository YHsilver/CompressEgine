package Compress;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Compress.HuffmanTree.encode;
import static Compress.HuffmanTree.getCode;

public class Compress {

    /**
     * static method for other class to call
     * @param originPath the path of the file to be compressed
     * @param newPath    the new file full path
     * @throws Exception
     */
    public static void doCompress(String originPath, String newPath) throws Exception {
        File file = new File(originPath);
        File outF = new File(newPath);
        Compress(file, outF);
    }

    /**
     * Compress a file or a file folder
     *
     * @param file the file to be compressed
     * @param outF the new compressed file
     * @throws Exception
     */
    private static void Compress(File file, File outF) throws Exception {
        if (outF.exists()) outF.delete();
        if (file.isFile()) {
            compressSingleFile(file, outF);
        } else if (file.isDirectory()) {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outF, true));

            //write the 0 representing the file folder
            dos.writeByte(0);

            //write the file folder name
            String name = file.getName();
            byte[] nameByte = name.getBytes("utf-8");
            dos.writeByte(nameByte.length);
            dos.write(nameByte);

            //write the folder's children number
            File files[] = file.listFiles();
            dos.writeByte(files.length);

            for (int i = 0; i < files.length; i++) {
                Compress(files[i], outF);
            }
        } else throw new Exception("can't find file");
    }


    /**
     * compress the Single file
     * for each file ,the first byte is 1, presenting this is a single file
     *
     * @param file the file to be compressed
     * @param outF the new compress file's
     * @throws IOException
     */
    private static void compressSingleFile(File file, File outF) throws Exception {

        HuffWeight huffWeight = new HuffWeight(file);
        //get deep copy of the huffWeight.list
        ArrayList<Node<String>> copy = new ArrayList<>();
        for (int i = 0; i < huffWeight.list.size(); i++) {
            copy.add(new Node(huffWeight.list.get(i)));
        }

        //create and encode the huffTree's leaves
        Node<String> root = HuffmanTree.createTree(copy);
        //for empty file ,just store the name,  and store the count 0 to indicate this is an empty file
        if (root == null) {
            String name = file.getName();
            byte[] nameByte = name.getBytes("utf-8");

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outF, true));
            dos.writeByte(1);
            dos.writeByte(nameByte.length);
            dos.write(nameByte);
            dos.writeInt(0);
            return;
        }
        if (huffWeight.list.size() != 1)
            encode(root, "");
        else encode(root, "0");
        //get the huffTree's array
        ArrayList<Node<String>> huffTreeList = HuffmanTree.huffList(root);
        //each byte's huffCode array
        StringBuilder[] codes = getCode(huffTreeList);
        // create the new compress file
        //define the file input  and output stream
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(outF, true);
        DataOutputStream dos = new DataOutputStream(fos);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

        /*
        the file header info:
        filename(1byte for the length and [length]byte for the name)
        huffmanTree(1int for the count of the kinds of byte; 1byte for the origin byte; 4byte for each weight)
        the last byte's valid bits (1byte)
        the total bytes of the new file (1int)
        */
        //the file type 1 represent a single file
        dos.writeByte(1);
        // get and write the original file name
        String name = file.getName();
        byte[] nameByte = name.getBytes(StandardCharsets.UTF_8);
        dos.writeByte(nameByte.length);
        dos.write(nameByte);

        //the huffTree
        dos.writeInt(huffWeight.count);
        for (int i = 0; i < huffWeight.list.size(); i++) {
            dos.writeByte(huffWeight.list.get(i).getData());
            dos.writeInt(huffWeight.list.get(i).getWeight());
        }

        //the last byte info
        int totalLength = 0;

        for (Node<String> o : huffTreeList) {
            if (o.getLeft() == null && o.getRight() == null) {
                totalLength += o.getWeight() * o.getEncode().length();

            }
        }
        byte lastValid = (byte) (totalLength % 8) == 0 ? 8 : (byte) (totalLength % 8);
        dos.writeByte(lastValid);
        dos.writeInt((int) Math.ceil((double) totalLength / 8));

        //write the body
        byte[] readBuffer = new byte[1 << 10];
        int intchar;
        int index = 0;

        StringBuilder code = new StringBuilder();
        while ((intchar = fis.read(readBuffer)) != -1) {
            for (int i = 0; i < intchar; i++) {
                int tmp = readBuffer[i] + 128;
                code.append(codes[tmp]);
                while ((index + 1) * 8 <= code.length()) {
                    bos.write((byte) Integer.parseInt(code.substring(index * 8, (index + 1) * 8), 2));
                    index++;
                }
            }
        }

        if (((code.length() % 8)) != 0) {
            bos.write((byte) Integer.parseInt(code.substring((index) * 8), 2));
        }

        bos.close();
        fos.close();
        fis.close();
    }
}
