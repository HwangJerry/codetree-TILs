import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    private static final int LIST_SIZE = 100000;
    private static Node[] linkedList = new Node[LIST_SIZE];
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        for (int i = 1; i <= n; i++) {
            linkedList[i] = new Node(i);
        }

        st = new StringTokenizer(br.readLine());
        int q = Integer.parseInt(st.nextToken());

        for (int i = 0; i < q; i++) {
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            int idx = Integer.parseInt(st.nextToken());
            if (cmd == 1) {
                pop(idx);
            } else if (cmd == 2) {
                int jdx = Integer.parseInt(st.nextToken());
                insert(idx, jdx);
            } else if (cmd == 3) {
                int jdx = Integer.parseInt(st.nextToken());
                append(idx, jdx);
            } else if (cmd == 4) {
                Node node = linkedList[idx];
                if (node.prev != null) {
                    System.out.print(node.prev.data + " ");
                } else {
                    System.out.print("0 ");
                }
                if (node.next != null) {
                    System.out.println(node.next.data);
                } else {
                    System.out.println("0");
                }
            }
        }
        for (int i = 1; i <= n; i++) {
            Node node = linkedList[i];
            if (node.next == null) {
                System.out.print("0 ");
            } else {
                System.out.print(node.next.data + " ");
            }
        }

    }

    private static void pop(int idx) {
        Node node = linkedList[idx];
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        node.prev = null;
        node.next = null;
    }

    private static void insert(int idx, int jdx) {
        Node inode = linkedList[idx];
        Node jnode = linkedList[jdx];
        if (inode.prev != null) {
            inode.prev.next = jnode;
        }

        jnode.prev = inode.prev;
        jnode.next = inode;
        inode.prev = jnode;
    }

    private static void append(int idx, int jdx) {
        Node inode = linkedList[idx];
        Node jnode = linkedList[jdx];
        if (inode.next != null) {
            inode.next.prev = jnode;
        }
        jnode.next = inode.next;
        jnode.prev = inode;
        inode.next = jnode;
    }

    private static class Node {
        private int data;
        private Node prev, next;

        private Node(int data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }
}