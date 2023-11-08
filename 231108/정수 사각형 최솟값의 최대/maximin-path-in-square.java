import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    private static int n;
    private static int res;
    private static int[][] arr;
    private static int[][] dp;
    private static Queue<Integer> pq = new PriorityQueue<>();
    public static void main(String[] args) throws IOException {
        /*
        * 일정 패턴으로 이동
        * 그렇게 이동했을 때, 경로의 값들을 min으로 계속 비교 -> int res에 담기
        * res를 pq에 담아서 최대값 출력
        * */

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        arr = new int[n][n];
        dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        dp[0][0] = arr[0][0];

        // init
        for (int i = 1; i < n; i++) {
            dp[0][i] = Math.min(dp[0][i - 1], arr[0][i]);
        }
        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.min(dp[i - 1][0], arr[i][0]);
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                if (i == n - 1 && j == n - 1) {
                    break;
                }
                dp[i][j] = Math.min(dp[i][j - 1], dp[i - 1][j]);
            }
        }
        dp[n - 1][n - 1] = Math.max(dp[n - 2][n - 1], dp[n - 1][n - 2]);
        System.out.println(dp[n-1][n-1]);
    }
}