import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {
    static final int RIGHT = 0;
    static final int DOWN = 1;
    static final int LEFT = 2;
    static final int UP = 3;

    static int N, M;
    static int[][] map;
    static int Y = 0;
    static int X = 0;
    static int D = 0;
    static int[] dy = {0, 1, 0, -1};
    static int[] dx = {1, 0, -1, 0};
    static int bottom = 6;
    static int down = 2;
    static int left = 4;
    static int right = 3;
    static int up = 5;
    static int ans;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken()); // 20
        M = Integer.parseInt(st.nextToken()); // 1000
        map = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 주사위 : 1,1 에서 시작
        // 처음엔 오른쪽으로 움직임
        // 주사위가 놓여있는 칸에 적혀있는 숫자와 상하좌우로 인접하며 같은 숫자가 적혀있는 모든 칸의 합만큼 점수를 얻게 된다.
        
        // 주사위 아랫면이 보드의 해당 칸에 있는 숫자보다 크면 현재 진행방향에서 90 시계방향 회전 / 더 작으면 반시계로 회전 / 동일하면 방향 유지
        // 주사위 바닥면이 기준
        for (int T = 1; T <= M; T++) {
            // System.out.println(Y + " " + X);
            simulation();

        }
        System.out.println(ans);
    }

    static void simulation() {
        rollDice();

        getScore(); // 움직인 주사위를 기준으로 그 칸에 있는 숫자와 상하좌우 인접하며 같은 숫자가 적혀있는 모든 칸의 합만큼 점수 획득

        rotateDice();
    }

    static void rollDice() {
        int ny = Y + dy[D];
        int nx = X + dx[D];
        int[] moved = roll(ny, nx);
        Y = moved[0];
        X = moved[1];
        D = moved[2];
        dice();
    }

    static void getScore() {
        // 현재 주사위가 있는 좌표와 동일 숫자로 이뤄진 인접 좌표 합
        Queue<int[]> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[N][N];
        q.add(new int[]{Y, X});
        visited[Y][X] = true;
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int y = now[0];
            int x = now[1];
            ans += map[y][x];
            for (int i = 0; i < 4; i++) {
                int ny = y + dy[i];
                int nx = x + dx[i];
                if (inRange(ny, nx) && map[ny][nx] == map[y][x] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    q.add(new int[]{ny, nx});
                }
            }
        }
    }

    static void rotateDice() {
        // 바닥이랑 비교해서 D 변경할지 선택
        if (map[Y][X] > bottom) {
            D = (D - 1) < 0 ? D + 3 : D - 1;
        } else if (map[Y][X] < bottom) {
            D = (D + 1) % 4;
        }
    }

    static void dice() {
        if (D == UP) {
            int t = up;
            down = bottom;
            up = 7 - bottom;
            bottom = t;
        } else if (D == DOWN) {
            int t = down;
            up = bottom;
            down = 7 - bottom;
            bottom = t;
        } else if (D == RIGHT) {
            int t = right;
            left = bottom;
            right = 7 - bottom;
            bottom = t;
        } else if (D == LEFT) {
            int t = left;
            left = 7 - bottom;
            right = bottom;
            bottom = t;
        }
    }

    static int[] roll(int ny, int nx) {
        if (ny < 0) {
            return new int[]{1, nx, DOWN};
        } else if (ny > N-1) {
            return new int[]{N-2, nx, UP};
        } else if (nx < 0) {
            return new int[]{ny, 1, RIGHT};
        } else if (nx > N-1) {
            return new int[]{ny, N-2, LEFT};
        }
        return new int[]{ny, nx, D};
    }

    static boolean inRange(int y, int x) {
        return y >= 0 && y < N && x >= 0 && x < N;
    }
}