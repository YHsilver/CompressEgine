package Compress;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import static Compress.HuffmanTree.encode;

public class Decompress {

    /**
     * do decompress
     * @param originPath the path of the file to be decompressed
     * @param newPath the new file path
     * @throws IOException
     */
    public static void doDecompress(String originPath, String newPath) throws IOException {
        File file =new File(originPath);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        decompress(fis, dis, newPath);
        fis.close();
    }

    /**
     * deCompress the file compressed by this
     * @param fis the fileInputStream of the compressed file
     * @param dis the dataOutputStream of the new file
     * @param newPath the  path of the file decompressed file
     * @throws IOException
     */
    private static void decompress(FileInputStream fis, DataInputStream dis, String newPath) throws IOException {
        byte fileType = dis.readByte();
        if (fileType == 0) {//dir
            byte nameByteLength = dis.readByte();
            byte[] nameByte = new byte[nameByteLength];
            for (int i = 0; i < nameByteLength; i++) {
                nameByte[i] = dis.readByte();
            }
            byte childNum = dis.readByte();
            String name = new String(nameByte, StandardCharsets.UTF_8);
            File parent = new File(newPath + "\\" + name);
            parent.mkdir();
            for (int i = 0; i < childNum; i++) {
                decompress(fis, dis, parent.getAbsolutePath());
            }
        } else if (fileType == 1) {
            decompressSingleFile(fis, dis, newPath);
        } else {
            System.out.println("error:" + fileType);
        }
    }

    /**
     * read the tree stored in the compressed file
     *
     * @param fis     the compressed file inputStream
     * @param newPath the path of each decompressed file
     * @throws IOException
     */
    private static void decompressSingleFile(FileInputStream fis, DataInputStream dis, String newPath) throws IOException {

        byte nameByteLength = dis.readByte();
        byte[] nameByte = new byte[nameByteLength];
        for (int i = 0; i < nameByteLength; i++) {
            nameByte[i] = dis.readByte();
        }

        String name = new String(nameByte, StandardCharsets.UTF_8);


        FileOutputStream fos = new FileOutputStream(newPath + "\\" + name);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int count = dis.readInt();

        //the empty file
        if (count == 0) {
            fos.close();
            return;
        }

        //read the tree
        ArrayList<Node<String>> weightList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            weightList.add(new Node<String>((char) dis.readByte(), dis.readInt()));
        }


        //read the last byte's valid bits and the body's total bytes
        byte lastValid = dis.readByte();
        int totalBytes = dis.readInt();

        //rebuild the huffTree
        Node<String> root = HuffmanTree.createTree(weightList);
        ArrayList<Node<String>> huffTreeList = HuffmanTree.huffList(root);
        if (huffTreeList.size() != 1)
            encode(root, "");
        else encode(root, "0");

        int intChar = 0;
        int readBytes = 0;
        byte[] readBuffer = new byte[totalBytes];

        tag:
        while ((intChar = fis.read(readBuffer)) != 0) {
            for (int i = 0; i < intChar && readBytes < totalBytes; i++) {
                readBytes++;

                int j = 0;

                if (readBytes == totalBytes) {
                    j += 8 - lastValid;
                }

                for (; j < 8; j++) {
                    if ((readBuffer[i] & 1 << (7 - j)) != 0 && huffTreeList.size() != 1) {//d第j位为1
                        root = root.getRight();
                    } else if (huffTreeList.size() != 1) root = root.getLeft();


                    if (root.getRight() == null && root.getLeft() == null) {
//                      System.out.println("222"+root.getData());
                        bos.write(root.getData());
                        root = huffTreeList.get(0);

//                        System.out.println(root);
                    }
                }
                if (readBytes == totalBytes) {
                    break tag;
                }
            }
        }

        bos.close();
        fos.close();


    }

}
