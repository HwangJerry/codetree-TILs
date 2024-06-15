import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static final int LEFT = 2;
    static int N, M;
    static int D, P;
    static int[] dy = {0, 1, 0, -1};
    static int[] dx = {1, 0, -1, 0};
    static int[][] map;
    static int ans;
    static Queue<Integer> q = new ArrayDeque<>();
    static Queue<int[]> delete = new ArrayDeque<>();
    static Queue<int[]> wait = new ArrayDeque<>();
    static int[] temp;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        temp = new int[N*N+1];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            D = Integer.parseInt(st.nextToken());
            P = Integer.parseInt(st.nextToken());
            simulation();
        }
        System.out.println(ans);
    }
    
    static void simulation() {
        attack();

        gravity();

        while(tetris()) {
            gravity();
        }

        renew();
    }

    static void attack() {
        int y = N/2;
        int x = N/2;
        for (int i = 0; i < P; i++) {
            y = y + dy[D];
            x = x + dx[D];
            if (inRange(y, x)) {
                ans += map[y][x];
                map[y][x] = 0;
            }
        }
        // print("attack");
    }

    static void renew() {
        int y = N/2;
        int x = N/2;
        int d = LEFT;
        int prev = map[y][x];
        int cnt = 1;
        q.clear();
        top:
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < i; k++) {
                    if (y == N/2 && x == N/2) {
                        y = y + dy[d];
                        x = x + dx[d];
                        d = counterClockwise(d);
                        continue;
                    } else if (!inRange(y , x)) {
                        break top;
                    }
                    if (map[y][x] == prev) {
                        cnt++;
                    } else { // 달라지면
                        if (prev != 0) {
                            q.add(cnt);
                            q.add(prev);
                        }
                        cnt = 1;
                    }
                    prev = map[y][x];
                    if (prev == 0) {
                        break top;
                    }
                    y += dy[d];
                    x += dx[d];
                    // 방향 전환
                    if (k == i - 1) {
                        d = counterClockwise(d);
                    }
                }
            }
        }
        y = N/2;
        x = N/2;
        d = LEFT;
        top:
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < i; k++) {
                    if (y == N/2 && x == N/2) {
                        y = y + dy[d];
                        x = x + dx[d];
                        d = counterClockwise(d);
                        continue;
                    } else if (!inRange(y , x)) {
                        break top;
                    }
                    if (!q.isEmpty()) {
                        map[y][x] = q.poll();
                    }
                    y += dy[d];
                    x += dx[d];
                    // 방향 전환
                    if (k == i - 1) {
                        d = counterClockwise(d);
                    }
                }
            }
        }
        // print("renew");

    }

    static boolean tetris() {
        int y = N/2;
        int x = N/2;
        int d = LEFT;
        int prev = map[y][x];
        boolean hasDone = false;
        delete.clear();
        wait.clear();
        top:
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < i; k++) {
                    if (y == N/2 && x == N/2) {
                        y = y + dy[d];
                        x = x + dx[d];
                        d = counterClockwise(d);
                        continue;
                    } else if (!inRange(y , x)) {
                        break top;
                    }
                    if (map[y][x] == prev) {
                        wait.add(new int[]{y, x});
                    } else { // 달라지면
                        if (wait.size() >= 4) {
                            delete.addAll(wait);
                        }
                        wait.clear();
                        wait.add(new int[]{y, x});
                    }
                    prev = map[y][x];
                    if (prev == 0) {
                        break top;
                    }
                    y += dy[d];
                    x += dx[d];
                    // 방향 전환
                    if (k == i - 1) {
                        d = counterClockwise(d);
                    }
                }
            }
        }

        if (!delete.isEmpty()) {
            while(!delete.isEmpty()) {
                int[] p = delete.poll();
                ans += map[p[0]][p[1]];
                map[p[0]][p[1]] = 0;
            }
            hasDone = true;
        }
        // print("tetris");
        return hasDone; // test
    }

    static void gravity() {
        int y = N/2;
        int x = N/2;
        int d = LEFT;
        int tdx = 0;
        Arrays.fill(temp, 0);
        top:
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < i; k++) {
                    if (y == N/2 && x == N/2) {
                        y = y + dy[d];
                        x = x + dx[d];
                        d = counterClockwise(d);
                        continue;
                    } else if (!inRange(y , x)) {
                        break top;
                    }
                    if (map[y][x] > 0) {
                        temp[tdx++] = map[y][x];
                    }
                    y += dy[d];
                    x += dx[d];
                    // 방향 전환
                    if (k == i - 1) {
                        d = counterClockwise(d);
                    }
                }
            }
        }
        tdx = 0;
        y = N/2;
        x = N/2;
        d = LEFT;
        top:
        for (int i = 1; i <= N; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < i; k++) {
                    if (y == N/2 && x == N/2) {
                        y = y + dy[d];
                        x = x + dx[d];
                        d = counterClockwise(d);
                        continue;
                    } else if (!inRange(y , x)) {
                        break top;
                    }
                    map[y][x] = temp[tdx++];
                    y += dy[d];
                    x += dx[d];
                    // 방향 전환
                    if (k == i - 1) {
                        d = counterClockwise(d);
                    }
                }
            }
        }
        // print("gravity");
    }

    static void print(String title) {
        System.out.println("--- "+title + " --");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("_");
    }

    static int counterClockwise(int d) {
        return (d-1) < 0 ? d + 3 : d - 1;
    }

    static boolean inRange(int y, int x) {
        return y >= 0 && y < N && x >= 0 && x < N;
    }
}