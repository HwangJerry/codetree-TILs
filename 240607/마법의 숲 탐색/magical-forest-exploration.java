import java.util.*;
import java.io.*;

public class Main {
    static int R, C, K, ans, res, fairyY, fairyX, golemExit;
    static Queue<int[]> queue = new ArrayDeque<>();
    static int[][] grid;
    static boolean[][] visited;
    static int[] dy = {-1, 0, 1, 0}; // 북 동 남 서
    static int[] dx = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        grid = new int[R+3][C]; // 처음에 3칸 오버해서 시작

        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken()) - 1;
            int b = Integer.parseInt(st.nextToken());
            queue.add(new int[]{a, b});
        }

        // step 1 : queue 하나씩 뽑아서 "내려가기"
        int idx = 1;
        while(!queue.isEmpty()) {
            visited = new boolean[R+3][C];
            int[] crt = queue.poll();
            fairyY = 1;
            fairyX = crt[0];
            golemExit = crt[1];
            res = 0; // 정령이 이동한 결과
            // 내려가기
            golemGo(fairyY, fairyX, 2, 0);
            // 다 내려간 뒤 정령의 y가 4이하면 map 초기화하고 continue;
            if (fairyY <= 3) {
                grid = new int[R+3][C];
                continue;
            }
            // grid 완성
            grid[fairyY][fairyX] = idx;
            for (int i = 0; i < 4; i++) {
                grid[fairyY + dy[i]][fairyX + dx[i]] = idx; // 상하좌우
            }
            idx++;
            grid[fairyY + dy[golemExit]][fairyX+dx[golemExit]] += 10000;
            // visited 초기화
            visited = new boolean[R+3][C];

            // 이동하기
            // 출구면 다른 골렘으로 이동 가능
            // res 최대값으로 갱신
            // if (queue.size() == 0) {
            //     for (int z = 0; z < R+3; z++) {
            //         for (int zz = 0; zz < C; zz++) {
            //             System.out.print((grid[z][zz] == 0 ? "X" : grid[z][zz] > 10000 ? "@" : "O") + " ");
            //         }
            //         System.out.println();
            //     }
            // }
            //System.out.println(fairyY - 2 + " " + (fairyX+1));
            fairyGo(fairyY, fairyX);
            //System.out.println(fairyY - 2 + " " + (fairyX+1));
            res -= 2;
            ans += res;
        }    
        System.out.println(ans);

    }
    public static void fairyGo(int y, int x) {
        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (canFairyGo(ny, nx, y, x)) {
                if (fairyY < ny) {
                    fairyY = ny;
                    fairyX = nx;
                    res = Math.max(res, fairyY);
                }
                visited[ny][nx] = true;
                fairyGo(ny, nx);
            }
        }
    }

    public static void golemGo(int y, int x, int dir, int cnt) {
        if (cnt > 1) {
            return;
        }
        int ny, nx;
        if (canGolemGo(y+dy[2], x+dx[2], 2)) { // 남쪽 이동 먼저
            ny = y + dy[2];
            nx = x + dx[2];
            if (fairyY < ny) {
                fairyY = ny;
                fairyX = nx;
                if (dir != 2) {
                    // for (int i = 0; i < cnt; i++) {
                        golemExit = convertGolemExit(dir);
                    // }
                }
            }
            // visited[ny][nx] = true;
            golemGo(ny, nx, 2, 0);
        } else if (cnt == 0) {
            if (canGolemGo(y + dy[3], x + dx[3], 3)) { // 서쪽
                ny = y + dy[3];
                nx = x + dx[3];
                // visited[ny][nx] = true;
                golemGo(ny, nx, 3, cnt+1);
            } else if (canGolemGo(y + dy[1], x + dx[1], 1)) {
                ny = y + dy[1];
                nx = x + dx[1];
                // visited[ny][nx] = true;
                golemGo(ny, nx, 1, cnt+1);
            }
        }
    }

    public static boolean inRange(int y, int x, int range) { // range : 골렘 타고있을땐 1, 정령 이동시 0
        return y - range >= 0 && y + range < R+3 && x - range >= 0 && x + range < C;
    }

    public static boolean canGolemGo(int y, int x, int dir) {
        if (dir == 1) { // 동쪽으로 이동
            return inRange(y, x, 1) && grid[y][x+1] == 0 && grid[y-1][x] == 0 && grid[y+1][x] == 0 && !visited[y][x];
        } else if (dir == 3) { // 서쪽으로 이동
            return inRange(y, x, 1) && grid[y][x-1] == 0 && grid[y-1][x] == 0 && grid[y+1][x] == 0 && !visited[y][x];
        } else if (dir == 2) { // 남쪽으로 이동
            return inRange(y, x, 1) && grid[y+1][x] == 0 && grid[y][x-1] == 0 && grid[y][x+1] == 0 && !visited[y][x];
        }
        return false;
    }

    public static boolean canFairyGo(int y, int x, int prevY, int prevX) {
        return inRange(y,x,0) && grid[y][x] > 0 && ((grid[y][x] % 10000 == grid[prevY][prevX] % 10000) || (grid[prevY][prevX] > 10000)) && !visited[y][x];
    }

    public static int convertGolemExit(int dir) {
        return golemExit = (dir == 1 ? (golemExit + 1) % 4 : ((golemExit - 1) < 0 ? 4 + (golemExit - 1) : golemExit - 1));
    }

}