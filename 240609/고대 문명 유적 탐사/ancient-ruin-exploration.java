import java.io.*;
import java.util.*;

public class Main {
    static int K, M;
    static int[][] grid = new int[5][5];
    static int[][] maxGrid = new int[5][5];
    static int[] backup;
    static int[] dy = {0, 1, 0, -1}; // 우 하 좌 상
    static int[] dx = {1, 0, -1, 0};
    static int backupIdx = 0;
    static int depth = 0;
    static int res = 0;
    static int maxVal = 0;
    static boolean[][] visited = new boolean[5][5];
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        backup = new int[M];

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                grid[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            backup[i] = Integer.parseInt(st.nextToken());
        }

        for (int z = 0; z < K; z++) {
            maxVal = 0;
            if (z > 0) {
                for (int q = 0; q < 5; q++) {
                    grid[q] = maxGrid[q].clone();
                }
            }


            for (int y = 1; y < 4; y++) {
                for (int x = 1; x < 4; x++) {
                    // 회전 중심 y,x 선정
                    int[][] trid;
                    visited = new boolean[5][5];
                    trid = rotate90(y,x, grid);
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (trid[i][j] > 0 && !visited[i][j]) {
                                visited[i][j] = true;
                                res = 0;
                                depth = 0;
                                go(i, j, trid);
                                if (maxVal < res) {
                                    maxVal = res;
                                    for (int k = 0; k < 5; k++) {
                                        maxGrid[k] = trid[k].clone();
                                    }
                                }
                            }
                        }
                    }
                    visited = new boolean[5][5];
                    trid = rotate90(y,x, grid);
                    trid = rotate90(y,x, trid);
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (trid[i][j] > 0 && !visited[i][j]) {
                                visited[i][j] = true;
                                depth = 0;
                                res = 0;
                                go(i, j, trid);
                                if (maxVal < res) {
                                    maxVal = res;
                                    for (int k = 0; k < 5; k++) {
                                        maxGrid[k] = trid[k].clone();
                                    }
                                }
                            }
                        }
                    }
                    visited = new boolean[5][5];
                    trid = rotate270(y,x);
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (trid[i][j] > 0 && !visited[i][j]) {
                                visited[i][j] = true;
                                depth = 0;
                                res = 0;
                                go(i, j, trid);
                                if (maxVal < res) {
                                    maxVal = res;
                                    for (int k = 0; k < 5; k++) {
                                        maxGrid[k] = trid[k].clone();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (maxVal == 0) {
                return;
            }
            for (int l = 0; l < 5; l++) {
                for (int k = 4; k >= 0; k--) {
                    if (maxGrid[k][l] == 0) {
                        maxGrid[k][l] = backup[backupIdx++];
                    }
                }
            }
            while(true) {
                int roundSum = 0;
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (maxGrid[i][j] > 0) {
                            visited = new boolean[5][5];
                            visited[i][j] = true;
                            res = 0;
                            go(i, j, maxGrid);
                            roundSum += res;
                            for (int l = 0; l < 5; l++) {
                                for (int k = 4; k >= 0; k--) {
                                    if (maxGrid[k][l] == 0) {
                                        maxGrid[k][l] = backup[backupIdx++];
                                    }
                                }
                            }
                        }
                    }
                }
                if (roundSum == 0) {
                    break;
                }
                maxVal += roundSum;
            }
            System.out.print(maxVal + " ");
        }
    }

    public static void go(int y, int x, int[][] trid) {
        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (canGo(ny, nx, y, x, trid)) {
                System.out.println(ny + " " + nx);
                depth++;
                visited[ny][nx] = true;
                go(ny,nx,trid);
            }
        }
        if (depth >= 3) {
            trid[y][x] = 0;
            res++;
        }
    }

    public static boolean canGo(int y, int x, int prevY, int prevX, int[][] trid) {
        return inRange(y, x) && trid[y][x] == trid[prevY][prevX] && !visited[y][x];
    }

    public static boolean inRange(int y, int x) {
        return y >= 0 && y < 5 && x >= 0 && x < 5;
    }

    public static int[][] rotate90(int y, int x, int[][] grid) {
        int[][] trid = new int[5][5];
        for (int i = 0; i < 5; i++) {
            trid[i] = grid[i].clone();
        }
        int[] temp = new int[3];
        // 맨 윗줄 temp로 저장해두기
        temp[0] = trid[y-1][x-1];
        temp[1] = trid[y-1][x];
        temp[2] = trid[y-1][x+1];

        // 위에 배열 입히기
        trid[y-1][x+1] = trid[y-1][x-1];
        trid[y-1][x] = trid[y][x-1];
        trid[y-1][x-1] = trid[y+1][x-1];

        // 왼쪽 배열 입히기
        trid[y-1][x-1] = trid[y+1][x-1];
        trid[y][x-1] = trid[y+1][x];
        trid[y+1][x-1] = trid[y+1][x+1];

        // 아래 배열 입히기
        trid[y+1][x-1] = trid[y+1][x+1];
        trid[y+1][x] = trid[y][x+1];
        trid[y+1][x+1] = trid[y-1][x+1];

        // 오른쪽 배열 입히기
        trid[y+1][x+1] = temp[2];
        trid[y][x+1] = temp[1];
        trid[y-1][x+1] = temp[0];

        return trid;
    }

    public static int[][] rotate180(int y, int x) {
        int[][] trid = new int[5][5];
        for (int i = 0; i < 5; i++) {
            trid[i] = grid[i].clone();
        }
        int[] temp = new int[3];
        // 맨 윗줄 temp로 저장해두기
        temp[0] = trid[y-1][x-1];
        temp[1] = trid[y-1][x];
        temp[2] = trid[y-1][x+1];

        trid[y-1][x+1] = trid[y+1][x-1];
        trid[y-1][x] = trid[y+1][x];
        trid[y-1][x-1] = trid[y+1][x+1];

        trid[y+1][x-1] = temp[2];
        trid[y+1][x] = temp[1];
        trid[y+1][x+1] = temp[0];

        temp[0] = trid[y-1][x-1];
        temp[1] = trid[y][x-1];
        temp[2] = trid[y+1][x-1];

        trid[y-1][x-1] = trid[y+1][x+1];
        trid[y][x-1] = trid[y][x+1];
        trid[y+1][x-1] = trid[y-1][x+1];

        trid[y+1][x+1] = temp[2];
        trid[y][x+1] = temp[1];
        trid[y-1][x+1] = temp[0];

        return trid;
    }

    public static int[][] rotate270(int y, int x) {
        int[][] trid = new int[5][5];
        for (int i = 0; i < 5; i++) {
            trid[i] = grid[i].clone();
        }
        int[] temp = new int[3];
        // 맨 윗줄 temp로 저장해두기
        temp[0] = trid[y-1][x-1];
        temp[1] = trid[y-1][x];
        temp[2] = trid[y-1][x+1];

        // 위에 배열 입히기
        trid[y-1][x-1] = trid[y-1][x+1];
        trid[y-1][x] = trid[y][x+1];
        trid[y-1][x+1] = trid[y+1][x+1];

        // 오른쪽 배열 입히기
        trid[y-1][x+1] = trid[y+1][x+1];
        trid[y][x+1] = trid[y+1][x];
        trid[y+1][x+1] = trid[y+1][x-1];

        // 아래 배열 입히기
        trid[y+1][x+1] = trid[y+1][x-1];
        trid[y+1][x] = trid[y][x-1];
        trid[y+1][x-1] = trid[y-1][x-1];

        // 왼쪽 배열 입히기
        trid[y+1][x-1] = temp[0];
        trid[y][x-1] = temp[1];
        trid[y-1][x-1] = temp[2];

        return trid;
    }
}