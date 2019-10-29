package Compress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import java.util.Queue;


public class HuffmanTree {

    /**
     * create a huffman tree and return the root
     * @param nodes the ArrayList of each byte's Node
     * @return the root of the tree.  if no element, return null
     */
    static <T> Node<T> createTree(ArrayList<Node<T>> nodes) {
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            Node<T> left = nodes.get(nodes.size() - 1);
            Node<T> right = nodes.get(nodes.size() - 2);
            Node<T> parent = new Node<T>('-', left.getWeight()
                    + right.getWeight());
            parent.setLeft(left);
            parent.setRight(right);
            nodes.remove(left);
            nodes.remove(right);
            nodes.add(parent);
        }
        if (nodes.size() == 0) return null;
        return nodes.get(0);
    }




    /**
     * get a List of the huffman tree Node by pre-order
     * @param root the root of the huffman Tree
     * @return the list List of the huffman tree Node by pre-order
     */
    static <T> ArrayList<Node<T>> huffList(Node<T> root) {
        ArrayList<Node<T>> list = new ArrayList<Node<T>>();
        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> pNode = queue.poll();
            list.add(pNode);
            if (pNode.getLeft() != null) {
                queue.add(pNode.getLeft());
            }
            if (pNode.getRight() != null) {
                queue.add(pNode.getRight());
            }
        }
        return list;
    }



    /**
     * encode the leaf of the huffman tree,using recursion
     * @param node the element of the tree
     * @param str the huffman code
     */
    public static void encode(Node node, String str) {
        if (node.getLeft() != null) {
            String temp = str + "0";
            encode(node.getLeft(), temp);
        }
        if (node.getRight() != null) {
            String temp = str + "1";
            encode(node.getRight(), temp);
        }
        if (node.getLeft() == null && node.getRight() == null) {
            node.setEncode(str);
        }
    }

    /**
     * get the code string array
     * for each origin byte, it's huffCode is codes[byte+128]
     * @param huffTreeList the huffmanTree's list
     * @return the string array of codes,
     */
    public static StringBuilder[] getCode(ArrayList<Node<String>> huffTreeList) {
        StringBuilder[] codes = new StringBuilder[256];
        for (int i = 0; i < 256; i++) {
            for (Node<String> o : huffTreeList) {
                if (o.getLeft() == null && o.getRight() == null && ((byte) o.getData() + 128) == i) {
                    codes[i] = new StringBuilder(o.getEncode());
                    break;
                }
            }
        }
        return codes;
    }
}


class Node<T> implements Comparable<Node<T>> {
    private char data;
    private int weight;
    private Node<T> left;
    private Node<T> right;
    private String encode;

    Node(char data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    Node(Node node) {
        this.data = node.getData();
        this.weight = node.getWeight();
    }

    @Override  //print the detail info
    public String toString() {
        return "[data: " + this.data + " weight: " + this.weight + " encode: " + this.encode + "]";
    }

    @Override  //descend
    public int compareTo(Node<T> o) {
        if (o.weight > this.weight) {
            return 1;
        } else if (o.weight < this.weight) {
            return -1;
        }
        return 0;
    }


    public char getData() {
        return data;
    }

    public void setData(char data) {
        this.data = data;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Node<T> getLeft() {
        return left;
    }

    public void setLeft(Node<T> left) {
        this.left = left;
    }

    public Node<T> getRight() {
        return right;
    }

    public void setRight(Node<T> right) {
        this.right = right;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }
}

