import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static int N, M, K;
    static int[][][] map;
    static int[][][] pMoves;
    static int[] dy = {0, -1, 1, 0, 0};
    static int[] dx = {0, 0, 0, -1, 1};
    static int cnt;
    static ArrayList<int[]> players = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        cnt = M;
        map = new int[N+1][N+1][2]; // {독점한 플레이어 넘버, 남은 독점기간}
        pMoves = new int[M+1][5][5]; // 플레이어넘버-방향-우선순위
        for (int i = 0; i <= M; i++) {
            players.add(new int[]{-1, -1, -1});
        }

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                int num = Integer.parseInt(st.nextToken());
                if (num > 0) {
                    map[i][j][0] = num;
                    map[i][j][1] = K;
                    int[] player = players.get(num);
                    player[0] = i;
                    player[1] = j;
                }
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 1; i <= M; i++) {
            int[] player = players.get(i);
            player[2] = Integer.parseInt(st.nextToken());
        }

        for (int i = 1; i <= M; i++) {
            for (int j = 1; j <= 4; j++) {
                st = new StringTokenizer(br.readLine());
                pMoves[i][j][1] = Integer.parseInt(st.nextToken());
                pMoves[i][j][2] = Integer.parseInt(st.nextToken());
                pMoves[i][j][3] = Integer.parseInt(st.nextToken());
                pMoves[i][j][4] = Integer.parseInt(st.nextToken());
            }
        }

        int ans = 0;
        while(cnt > 1 && ans < 1000) {
            ans++;
            simulation();
        }
        System.out.println(ans < 1000 ? ans : -1);
    }

    static void simulation() {
        // 플레이어 이동 - 동시 이동임에 주의
        // 방향별 이동 우선순위대로 움직임
        // 독점계약 안맺은 칸 우선 이동, 없으면 독점계약 칸으로 이동
        // 여러개면 이동 우선순위에 따라 결정 (보고 있는 방향은 직전 이동방향)
        // 한 칸에 여러 플레이어가 있다면 가장 작은 번호를 가진 플레이어만 살아남음(담아둘 필요없이 바로 삭제하면 됨)
        moveP();
    
        // 독점 계약 (k만큼 턴 동안 유효)
        renewContract();
    }

    static void moveP() {
        int[][] pMap = new int[N+1][N+1];
        int[][][] tempMap = new int[N+1][N+1][2];
        for (int i = 1; i <= N; i++) {
            for (int j =1; j <= N; j++) {
                tempMap[i][j] = map[i][j].clone();
            }
        }
        top:
        for (int i = 1; i <= M; i++) {
            int[] player = players.get(i);
            int y = player[0];
            int x = player[1];
            int d = player[2];
            // 죽은 멤버는 pass
            if (y == -1 && x == -1 && d == -1) {
                continue;
            }
            boolean yetMoved = true;
            for (int j = 1; j <= 4; j++) {
                int ny = y + dy[pMoves[i][d][j]];
                int nx = x + dx[pMoves[i][d][j]];
                // 독점계약 안맺은 칸 우선 이동
                if (inRange(ny, nx) && map[ny][nx][0] == 0) {
                    // 누가 먼저 해당 땅에 있는데, 내가 서열이 낮은 경우
                    if (pMap[ny][nx] > 0 && pMap[ny][nx] < i) {
                        player[0] = -1;
                        player[1] = -1;
                        player[2] = -1;
                        cnt--;
                        continue top;
                    // 만약 상대가 더 서열이 낮은 경우
                    } else if (pMap[ny][nx] > 0 && pMap[ny][nx] > i) {
                        int o = pMap[ny][nx];
                        int[] opposite = players.get(o);
                        opposite[0] = -1;
                        opposite[1] = -1;
                        opposite[2] = -1;
                        cnt--;
                    }
                    pMap[ny][nx] = i;
                    player[0] = ny;
                    player[1] = nx;
                    player[2] = pMoves[i][d][j];
                    // 독점 계약
                    tempMap[ny][nx][0] = i;
                    tempMap[ny][nx][1] = K;
                    yetMoved = false;
                    break;
                }
            }
            // 독점계약 안맺은 칸이 없다면
            if (yetMoved) {
                for (int j = 1; j <= 4; j++) {
                    int ny = y + dy[pMoves[i][d][j]];
                    int nx = x + dx[pMoves[i][d][j]];
                    // 내가 독점한 땅으로 이동
                    if (inRange(ny, nx) && map[ny][nx][0] == i) {
                        if (pMap[ny][nx] > 0 && pMap[ny][nx] < i) {
                            player[0] = -1;
                            player[1] = -1;
                            player[2] = -1;
                            cnt--;
                            continue top;
                        // 만약 상대가 더 서열이 낮은 경우
                        } else if (pMap[ny][nx] > 0 && pMap[ny][nx] > i) {
                            int o = pMap[ny][nx];
                            int[] opposite = players.get(o);
                            opposite[0] = -1;
                            opposite[1] = -1;
                            opposite[2] = -1;
                            cnt--;
                        }
                        player[0] = ny;
                        player[1] = nx;
                        player[2] = pMoves[i][d][j];
                        break;
                    }
                }
            }
            // 독점 계약은 몰아서 하자
        }
        map = tempMap;
    }

    static void renewContract() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++){ 
                if (map[i][j][1] > 0) {
                    map[i][j][1]--;
                    // 계약 만료
                    if (map[i][j][1] == 0) {
                        map[i][j][0] = 0;
                    }
                }
            }
        }
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= N && x >= 1 && x <= N;
    }
}