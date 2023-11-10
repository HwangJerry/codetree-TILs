import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;

public class Main {
    private static int n;
    private static int[][] arr;
    private static int[][] dp;

    public static void main(String[] args) throws IOException {
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
        for (int i = 1; i < n; i++) {
            dp[0][i] = Math.max(arr[0][i], dp[0][i - 1]);
            dp[i][0] = Math.max(arr[i][0], dp[i - 1][0]);
        }
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(Math.max(arr[i][j], dp[i - 1][j]), Math.max(arr[i][j], dp[i][j - 1]));
            }
        }
        System.out.println(dp[n - 1][n - 1]);

    }
}