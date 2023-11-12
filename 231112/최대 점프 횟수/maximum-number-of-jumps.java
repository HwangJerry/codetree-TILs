import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    private static int n;
    private static int[] arr;
    private static int[] dp;
    private static int ans = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        arr = new int[n];
        dp = new int[n];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
            dp[i] = 0;
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if ((j + arr[j]) >= i) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }
        for (int i = 1; i < n; i++) {
            if (dp[i] != 0) {
                ans = Math.max(dp[i], ans);
            } else {
                break;
            }
        }
        System.out.println(ans);
    }
}