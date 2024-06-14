import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static int N, M, D, P;
    static int[] dy = {0, -1, -1, -1, 0, 1, 1, 1};
    static int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
    static int[][] map;
    static int[][] rules;
    static boolean[][] druged;
    static Queue<int[]> drugs = new ArrayDeque<>();
    static int ans;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        rules = new int[M][2];
        druged = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            rules[i][0] = Integer.parseInt(st.nextToken()) - 1;
            rules[i][1] = Integer.parseInt(st.nextToken());
        }
    
        initDrugs();


        for (int i = 0; i < M; i++) {
            simulation(rules[i]);
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                ans += map[i][j];
            }
        }
        System.out.println(ans);
    }

    static void simulation(int[] rule) {
        D = rule[0];
        P = rule[1];

        // 영양제 이동
        moveDrugs();

        // 영양제 투입(영양제 소멸)
        applyDrugs();

        growTrees();

        // 영양제 구매
        buyDrugs();
    }

    static void moveDrugs() {
        for (int[] d : drugs) {
            int y = d[0];
            int x = d[1];
            int ny = y + dy[D]*P;
            int nx = x + dx[D]*P;
            int[] moved = move(ny, nx);
            d[0] = moved[0];
            d[1] = moved[1];
        }
    }

    static void growTrees() {
        while(!drugs.isEmpty()) {
            int[] now = drugs.poll();
            int y = now[0];
            int x = now[1];
            for (int i = 1; i < 8; i= i + 2) {
                int ny = y + dy[i];
                int nx = x + dx[i];
                if (inRange(ny, nx)) {
                    if (map[ny][nx] > 0) {
                        map[y][x]++;
                    }
                }
            }
        }
    }

    static void applyDrugs() {
        for (int[] now : drugs) {
            int y = now[0];
            int x = now[1];
            map[y][x]++; // 1만큼 증가하고
            druged[y][x] = true;
        }
    }

    static void buyDrugs() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] >= 2 && !druged[i][j]) {
                    map[i][j] -= 2;
                    drugs.add(new int[]{i, j});
                }
            }
        }
        druged = new boolean[N][N];
    }

    static int[] move(int y, int x) {
        if (y < 0) {
            y = N + y;
        } else if (y >= N) {
            y = y - N;
        }
        if (x < 0) {
            x = N + x;
        } else if (x >= N) {
            x = x - N;
        }
        return new int[]{y, x};
    }

    static boolean inRange(int y, int x) {
        return y >= 0 && y < N && x >= 0 && x < N;
    }

    static void initDrugs() {
        drugs.add(new int[]{N-2, 0});
        drugs.add(new int[]{N-2, 1});
        drugs.add(new int[]{N-1, 0});
        drugs.add(new int[]{N-1, 1});
    }
}