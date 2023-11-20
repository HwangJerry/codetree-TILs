import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    private static int n, m;
    private static int[][] arr;
    private static int[][] dp;

    private static List<Pair> li = new ArrayList<>();
    private static Queue<Integer> pq = new PriorityQueue<>();
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        arr = new int[n][m];
        dp = new int[n][m];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < m; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // init
        dp[0][0] = 1;
        int ans = 0;
        pq.add(-1);

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                if (arr[i][j] > arr[0][0]) {
                    dp[i][j] = 2;
                    li.add(new Pair(i, j));
                    pq.add(-dp[i][j]);
                }
            }
        }

        li.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                if (o1.y == o2.y) {
                    return o1.x - o2.x;
                }
                return o1.y - o2.y;
            }
        });

//        li.forEach(i -> System.out.print(i.y + " " + i.x + "\n"));

        for (Pair p : li) {
            int x = p.x;
            int y = p.y;
            for (int i = y+1; i < n; i++) {
                for (int j = x+1; j < n; j++) {
                    if (arr[i][j] > arr[y][x]) {
                        dp[i][j] = Math.max(dp[i][j], dp[y][x] + 1);
                        pq.add(-dp[i][j]);
                    }
                }
            }
        }
        // for (int i = 0; i < n; i++) {
            // for (int j = 0; j < n; j++) {
                // System.out.print(dp[i][j] + " ");
            // }
            // System.out.println();
        // }
        System.out.println(-pq.poll());
    }
    private static class Pair {
        private int x, y;

        private Pair(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }
}