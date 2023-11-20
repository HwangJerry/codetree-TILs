import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    private static int n, m;
    private static int[][] arr;
    private static int[][] dp;

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
            dp[i][0] = 0;
        }
        for (int i = 1; i < m; i++) {
            dp[0][i] = 0;
        }

        for (int i = 1; i < n; i++) {
            if (arr[i][1] > arr[0][0]) {
                dp[i][1] = 2;
            }
        }
        for (int i = 1; i < m; i++) {
            if (arr[1][i] > arr[0][0]) {
                dp[1][i] = 2;
            }
        }

        // tabulation
        for (int i = 2; i < n; i++) {
            for (int j = 2; j < m; j++) {
                for (int k = 1; k < i; k++) {
                    for (int l = 1; l < j; l++) {
                        if (arr[i][j] > arr[k][l]) {
                            if(dp[k][l] == 0)
                                continue;

                            dp[i][j] = Math.max(dp[i][j], dp[k][l] + 1);
                            pq.add(-dp[i][j]);
                        }
                    }
                }
            }
        }

        System.out.println(-pq.poll());
    }
}