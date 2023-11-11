import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    private static int n;
    private static int[] arr;
    private static int[] dp;

    private static Queue<Integer> pq = new PriorityQueue<>();

    public static void main(String[] args) throws IOException {
        /*
        * 중요한 것은, 숫자가 연속될 필요는 없다는 것
        * */
        init();

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                    pq.add(-dp[i]);
                }
            }
        }
        System.out.println(-pq.poll());


    }

    private static void init() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        arr = new int[n];
        dp = new int[n];

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        dp[0] = 1;
        for (int i = 1; i < n; i++) {
            dp[i] = Integer.MIN_VALUE;
        }
    }
}