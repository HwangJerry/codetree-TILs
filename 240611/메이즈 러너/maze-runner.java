import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static final int P = -1;
    static final int E = (int) 1e9;

    static int N, M, K;
    static int[][] map;
    static int ans;

    static Queue<int[]> q = new ArrayDeque<>();
    static int[] dy = {-1, 1, 0, 0};
    static int[] dx = {0, 0, -1, 1};
    static int[] exit;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new int[N+1][N+1];

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            map[r][c] = P; // 사람 - 사람은 구별할 필요가 없으므로 몇명이 중복됐는지만 체크하면 됨.
        }
        st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        map[r][c] = E; // 출구

        // test
        // for (int i = 1; i <= N; i++) {
        //     for (int j = 1; j <= N; j++) {
        //         System.out.print((map[i][j] == E ? "E" : map[i][j] == -1 ? "P" : map[i][j]) + " ");
        //     }
        //     System.out.println();
        // }
        // System.out.println("-");

        for (int T = 0; T < K; T++) {
            // 모든 사람들이 탈출한 상태라면 연산 종료
            int cnt = 0;
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    if (map[i][j] < 0) {
                        cnt += Math.abs(map[i][j]); // 사람수 체크
                    } else if (map[i][j] == E) {
                        exit = new int[]{i, j}; // 출구 등록
                    }
                }
            }
            if (cnt == 0) { // 사람 없으면 즉시종료
                break;
            }

            // 1. 참가자 이동
            int[][] temp = new int[N+1][N+1];
            for (int i = 1; i <= N; i++) {
                temp[i] = map[i].clone();
            }
            
            for (int y = 1; y <= N; y++) {
                top:
                for (int x = 1; x <= N; x++) {
                    if (map[y][x] < 0) { // 참가자 발견
                        for (int i = 0; i < 4; i++) {
                            int ny = y + dy[i];
                            int nx = x + dx[i];
                            // 기존보다 거리가 줄어들면서, 이동할 수 있는 경우
                            if (canGo(ny, nx, y, x)) {
                                // System.out.println(ny + " " + nx); // test
                                ans += Math.abs(map[y][x]); // 있는 사람 수만큼 이동 거리 채집 
                                // 출구면
                                if (map[ny][nx] != E) { // 탈출이 아니면 -> 이동
                                    temp[ny][nx] = map[y][x];
                                }
                                temp[y][x] = 0;
                                continue top;
                            }
                        }
                    }
                }
            }

            map = temp;

            // test
            // for (int i = 1; i <= N; i++) {
            //     for (int j = 1; j <= N; j++) {
            //         System.out.print((map[i][j] == E ? "E" : map[i][j] == -1 ? "P" : map[i][j]) + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");

            // 2. 미로 회전
            int[] rect = findRect();
            // System.out.println(rect[0] + " " + rect[1] + " " + rect[2]);
            rotate(rect[0], rect[1], rect[2]);

            // test
            // for (int i = 1; i <= N; i++) {
            //     for (int j = 1; j <= N; j++) {
            //         System.out.print((map[i][j] == E ? "E" : map[i][j] == -1 ? "P" : map[i][j]) + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");
        }

        // 정답 출력 (참가자들의 이동 거리 합과 출구 좌표)
        System.out.println(ans);
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (map[i][j] == E) {
                    System.out.println(i + " " + j);
                    return;
                }
            }
        }

    }
    static int[] findRect() {
        // 크기 설정
        for (int r = 2; r <= N; r++) {
            // 좌상단 좌표 설정
            for (int y = 1; y < N; y++) {
                for (int x = 1; x < N; x++) {
                    boolean isE = false;
                    boolean isP = false;
                    // 좌표내 출구랑 참가자 한명이라도 있는지 탐색
                    for (int i = y; i < y + r; i++) {
                        for (int j = x; j < x + r; j++) {
                            if (inRange(i, j) && map[i][j] == E) {
                                isE = true;
                            } else if (inRange(i, j) && map[i][j] < 0) {
                                isP = true;
                            }
                        }
                    }
                    if (isE && isP) {
                        return new int[]{y, x, r};
                    }
                }
            }
        }
        return null;
    }
    static boolean inRange(int y, int x) {
        return y >= 1 && y <= N && x >= 1 && x <= N;
    }

    static void rotate(int y, int x, int r) {
        int[][] temp = new int[N+1][N+1];
        for (int i = 1; i <= N; i++) {
            temp[i] = map[i].clone();
        }
        
        // 시계방향 90도 회전
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < r; j++) {
                temp[y+j][x+r-1-i] = map[y+i][x+j];     
                if (map[y+i][x+j] == E) { // 출구가 아니면서
                    continue;
                } else if (map[y+i][x+j] > 0) { // 사람도 아니고, 평지도 아니라면
                    temp[y+j][x+r-1-i] -= 1; // 벽이므로, -1 수행
                }
            }   
        }
        map = temp;
    }
    static boolean canGo(int ny, int nx, int y, int x) {
        return dist(ny, nx) < dist(y, x) && (map[ny][nx] <= 0 || map[ny][nx] == E); // 사람이 있거나, 빈칸이거나, 출구거나
    }

    static int dist(int y, int x) {
        return Math.abs(exit[0] - y) + Math.abs(exit[1] - x);
    }
}