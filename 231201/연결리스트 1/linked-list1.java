import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        /*
        * 탐색은 오래걸리지만 : O(N)
        * 삭제, 삽입이 O(1)의 시간만 걸림
        * */
        List<Node> ll = new ArrayList();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s_init = br.readLine();
        Node cur = new Node(s_init);
        ll.add(cur);
        int n = Integer.parseInt(br.readLine());
        StringTokenizer st;
        int cmd;
        String s_value;
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            cmd = Integer.parseInt(st.nextToken());
            if (cmd == 1) {
                s_value = st.nextToken();
                Node node = new Node(s_value);
                cur.insert(node);
            } else if (cmd == 2) {
                s_value = st.nextToken();
                Node node = new Node(s_value);
                cur.append(node);
            } else if (cmd == 3 && cur.prev != null) {
                Node node = cur.prev;
                cur = node;
            } else if (cmd == 4 && cur.next != null) {
                Node node = cur.next;
                cur = node;
            }

            Node prev = cur.prev;
            Node next = cur.next;
            String prevString = "";
            String nextString = "";
            if (prev == null) {
                prevString = "(Null)";
            } else {
                prevString = prev.data;
            }

            if (next == null) {
                nextString = "(Null)";
            } else {
                nextString = next.data;
            }
            StringBuffer sb = new StringBuffer();
            String res = sb.append(prevString).append(" ").append(cur.data).append(" ").append(nextString).toString();
            System.out.println(res);
        }


    }


    private static class Node {
        private String data;
        private Node prev, next;

        private void insert(Node node) {
            node.prev = this.prev;
            node.next = this;
            this.prev = node;
        }

        private void append(Node node) {
            node.next = this.next;
            node.prev = this;
            this.next = node;
        }
        private Node(String data) {
            this.data = data;
            this.prev = this.next = null;
        }
    }

}