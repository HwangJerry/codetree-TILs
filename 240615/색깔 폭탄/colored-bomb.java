import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static int N, M;
    static int[][] map;
    static int ans;
    static int[] dy = {1, -1, 0, 0};
    static int[] dx = {0, 0, 1, -1};
    static boolean[][] visited;
    static boolean[][] rVisited;
    static Queue<int[]> q = new ArrayDeque<>();
    static int maxSize = 0; // 1
    static int minRed = (int) 1e9; // 2
    static int maxY = 0; // 3
    static int minX = (int) 1e9; // 4
    
    static int resY;
    static int resX;
    static int resRed;
    static int resSize;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        for (int i = 0 ; i < N; i ++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        boolean isContinue = true;
        // print();
        while(isContinue) {
            isContinue = simulation();
        }
        System.out.println(ans);
    }

    static boolean simulation() {
        // 현재 격자에서 크기가 가장 큰 폭탄 묶음 찾기
        // 우선순위
        // 1. 빨간색 폭탄이 가장 적게 포함된 묶음
        // 2. 가장 행이 큰 폭탄 묶음 (기준 : 빨간색이 아니면서 행이 가장 큰 칸)
        // 3. 가장 열이 작은 폭탄 묶음
        int[] res = findGroup();
        if (res == null) {
            return false;
        }
        // 폭발
        bomb(res[0], res[1]);
        // print();

        // 중력이 작용하여 위에 있던 폭탄들이 떨어짐(단, 돌은 떨어지지 않음)
        gravity();
        // 반시계 방향으로 90만큼 격자 회전 회전
        rotate();
        // 다시 중력 작용
        gravity();
        return true;
    }

    static int[] findGroup() {
        // 각 종류별로 bfs 탐색하면서 폭탄 그룹을 탐색
        // 탐색간에 우선순위 큐에 넣고, 각 빨간폭탄 개수 갱신하면서 진행
        maxSize = 0; // 1
        minRed = (int) 1e9; // 2
        maxY = 0; // 3
        minX = (int) 1e9; // 4
        visited = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j] && map[i][j] > 0) {
                    bfs(i, j, visited);
                    if (maxSize < resSize) {
                        maxSize = resSize;
                        minRed = resRed;
                        maxY = resY;
                        minX = resX;
                        // System.out.println(maxY + " " + minX);
                    } else if (maxSize == resSize) {
                        if (minRed > resRed) {
                            maxSize = resSize;
                            minRed = resRed;
                            maxY = resY;
                            minX = resX;
                        } else if (minRed == resRed) {
                            if (maxY < resY) {
                                maxSize = resSize;
                                minRed = resRed;
                                maxY = resY;
                                minX = resX;
                            } else if (maxY == resY) {
                                if (minX > resX) {
                                    maxSize = resSize;
                                    minRed = resRed;
                                    maxY = resY;
                                    minX = resX;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (maxSize >= 2) {
            ans += (int) Math.pow(maxSize, 2);
            return new int[]{maxY, minX};
        }
        return null;
    }
    
    static void bfs(int i, int j, boolean[][] visited) {
        resY = i;
        resX = j;
        resRed = 0;
        resSize = 1;
        q.clear();
        q.add(new int[]{i, j});
        visited[i][j] = true;
        rVisited = new boolean[N][N];
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int y = now[0];
            int x = now[1];
            for (int k = 0; k < 4; k++) {
                int ny = y + dy[k];
                int nx = x + dx[k];
                if (inRange(ny, nx) && ((map[ny][nx] == map[y][x] && !visited[ny][nx]) || map[ny][nx] == 0 && !rVisited[ny][nx])) {
                    visited[ny][nx] = true;
                    resSize++;
                    if (map[ny][nx] != 0) {
                        resY = Math.max(resY, ny);
                        resX = Math.min(resX, nx);
                    } else {
                        resRed++;
                        rVisited[ny][nx] = true;
                    }
                    q.add(new int[]{ny, nx});
                }
            }
        }
    }

    static void bomb(int i, int j) {
        int[][] temp = copyMap();
        q.clear();
        visited = new boolean[N][N];
        q.add(new int[]{i, j});
        visited[i][j] = true;
        temp[i][j] = -2;
        rVisited = new boolean[N][N];
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int y = now[0];
            int x = now[1];
            temp[y][x] = -2;
            for (int k = 0; k < 4; k++) {
                int ny = y + dy[k];
                int nx = x + dx[k];
                if (inRange(ny, nx) && ((map[ny][nx] == map[y][x] && !visited[ny][nx]) || (map[ny][nx] == 0 && !rVisited[ny][nx]))) {
                    q.add(new int[]{ny, nx});
                    if (map[ny][nx] > 0) {
                        visited[ny][nx] = true;
                    } else {
                        rVisited[ny][nx] = true;
                    }
                }
            }
        }
        map = temp;
    }

    static void rotate() {
        int[][] temp = copyMap();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[N-1-j][i] = map[i][j];
            }
        }
        map = temp;
    }

    static void gravity() {
        int[] temp = new int[N];
        for (int j = 0; j < N; j++) {
            Arrays.fill(temp, -2);
            int tdx = N-1;
            for (int i = N-1; i >= 0; i--) {
                if (map[i][j] >= 0) {
                    temp[tdx--] = map[i][j];
                } else if (map[i][j] == -1) {
                    tdx = i;
                    temp[tdx--] = map[i][j];
                }
            }
            for (int i = 0; i < N; i++) {
                map[i][j] = temp[i];
            }
        }
    }

    static int[][] copyMap() {
        int[][] temp = new int[N][N];
        for (int i = 0; i < N; i++) {
            temp[i] = map[i].clone();
        }
        return temp;
    }

    static void print() {
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < N; j ++) {
                System.out.print((map[i][j] == -2 ? "B" : map[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("-");
    }

    static boolean inRange(int y, int x) {
        return y >= 0 && y < N && x >= 0 && x < N;
    }

    static void resetVisited() {
        for (int i = 0; i < N; i++) {
            Arrays.fill(visited[i], false);
        }
    }
}