import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static int M, T, R, C;
    static ArrayList<Integer>[][] map = new ArrayList[5][5];
    static int[][] deads = new int[5][5];
    static Queue<int[]> news = new ArrayDeque<>(); // r, c, d
    static int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dx = {0, -1, -1, -1, 0, 1, 1, 1};

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        M = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());
        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                map[i][j] = new ArrayList<>();
            }
        }

        // 몬스터 정보 (몬스터 초기 위치와 팩맨의 초기 위치가 같을 수 있음)
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken()) - 1;
            map[r][c].add(d);
        }

        for (int t = 1; t <= T; t++) {
            // print();
            simulation();
        }

        int ans = 0;
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                ans += map[i][j].size();
            }
        }
        System.out.println(ans);
    }
    
    static void simulation() {
        // 1. 몬스터 복제 시도
        copyMonsters();

        // 2. 몬스터 이동
        moveMonsters();

        // 3. 팩맨 이동
        movePacman();

        // 4. 시체 소멸
        deleteDeadmons();

        // 5. 몬스터 복제 부화
        newMonsters();
    }

    static void copyMonsters(){
        // 현재 위치에서 자신과 같은 방향의 몬스터를 복제함
        // 복제된 몬스터는 부화되지 않은 상태로 아직 움직이지 않음
        // 알이 부화하면 모체의 방향을 지닌 채로 깨어남
        // -- 조건 : 몬스터는 백만 개 이상 만들어지진 않음
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                for (int d : map[i][j]) {
                    news.add(new int[]{i, j, d});
                }
            }
        }
    }

    static void moveMonsters() {
        // 몬스터는 현재 가진 방향으로 한칸 이동
        // 만약 몬스터 시체가 있거나, 팩맨이 있거나, 격자를 벗어나는 방향이면 반시계로 45도 회전(d++)
        // 갈 수 있을 때 까지 돌고, 다 돌아도 갈 수 있는 데가 없으면 움직임 X
        // -- 주의 : 한 칸에 여러 몬스터가 존재할 수 있음
        ArrayList<Integer>[][] temp = new ArrayList[5][5];
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                temp[i][j] = new ArrayList<>();
            }
        }

        for (int y = 1; y <= 4; y++) {
            for (int x = 1; x <= 4; x++) {
                for (int d : map[y][x]) {
                    for (int i = 0; i < 8; i++) {
                        int ny = y + dy[(d+i) % 8];
                        int nx = x + dx[(d+i) % 8];
                        if (inRange(ny, nx) && (ny != R || nx != C) && deads[ny][nx] == 0) {
                            temp[ny][nx].add((d+i) % 8);
                            break;
                        }
                    }
                }
            }
        }

        map = temp;

    }

    static void movePacman() {
        // 총 3칸 이동, 각 이동마다 상좌하우 우선순위 선택
        // 가장 몬스터를 많이 먹을 수 있는 방향으로 움직임
        // 팩맨의 자리에 있는 몬스터는 먹지 않고, 알을 먹지 않음(이동과정의 몬스터만 먹음)
        // 몬스터를 먹으면 시체를 남김 (시체는 총 2턴동안만 유지됨)
        // --> deads[i][j] = 3 (현재턴 + 다음 2턴)
        int maxCnt = -1; // 최대로 먹은 수
        int maxR = R;
        int maxC = C;
        int mfy = -1;
        int mfx = -1;
        int msy = -1;
        int msx = -1;
        int mty = -1;
        int mtx = -1;
        Queue<int[]> q = new ArrayDeque(); // {y, x, depth, cnt, tracking} -- 어느 번째 단계고, 얼마나 먹었는지
        
        q.add(new int[]{R, C, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int y = now[0];
            int x = now[1];
            int depth = now[2];
            int cnt = now[3];
            int fy = now[4];
            int fx = now[5];
            int sy = now[6];
            int sx = now[7];
            int ty = now[8];
            int tx = now[9];
            int v1 = now[10];
            int v2 = now[11];
            int v3 = now[12];
            int v4 = now[13];
            if (depth == 3) {
                if (maxCnt < cnt) {
                    maxCnt = cnt;
                    maxR = y;
                    maxC = x;
                    mfy = fy;
                    mfx = fx;
                    msy = sy;
                    msx = sx;
                    mty = ty;
                    mtx = tx;
                }
            } else {
                for (int j = 0; j < 8; j = j + 2) { // 상좌하우
                    int ny = y + dy[j];
                    int nx = x + dx[j];
                    if (inRange(ny, nx) && canVisit(ny, nx, v1, v2, v3, v4)) {
                        if (depth+1 == 1) {
                            fy = ny;
                            fx = nx;
                        } else if (depth+1 == 2) {
                            sy = ny;
                            sx = nx;
                        } else if (depth+1 == 3) {
                            ty = ny;
                            tx = nx;
                        }
                        int[] v = visit(ny, nx, v1, v2, v3, v4);
                        q.add(new int[]{ny, nx, depth+1, cnt + map[ny][nx].size(), fy, fx, sy,sx, ty, tx, v[0], v[1], v[2], v[3]});
                    }
                }
            }
        }

        R = maxR;
        C = maxC;
        
        int aliveCnt = 3;
        if (!map[mfy][mfx].isEmpty()) {
            map[mfy][mfx].clear();
            deads[mfy][mfx] = aliveCnt;
        }
        if (!map[msy][msx].isEmpty()) {
            map[msy][msx].clear();
            deads[msy][msx] = aliveCnt;
        }
        if (!map[mty][mtx].isEmpty()) {
            map[mty][mtx].clear();
            deads[mty][mtx] = aliveCnt;
        }
    }

    static void deleteDeadmons() {
        // 시체는 총 2턴동안만 유지된다.
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                deads[i][j] -= (deads[i][j] - 1 >= 0 ? 1 : 0);
            }
        }
    }

    static void newMonsters() {
        // 알 형태였던 몬스터 부화(아까의 방향을 갖고 태어남)
        while(!news.isEmpty()) {
            int[] m = news.poll();
            map[m[0]][m[1]].add(m[2]);
        }
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= 4 && x >= 1 && x <= 4;
    }

    static boolean canVisit(int ny, int nx, int v1, int v2, int v3, int v4) {
        if (ny == 1) {
            if ((v1 & (1 << nx)) != 0) {
                return false;
            }
        } else if (ny == 2) {
            if ((v2 & (1 << nx)) != 0) {
                return false;
            }
        } else if (ny == 3) {
            if ((v3 & (1 << nx)) != 0) {
                return false;
            }
        } else if (ny == 4) {
            if ((v4 & (1 << nx)) != 0) {
                return false;
            }
        }
        return true;
    }

    static int[] visit(int ny, int nx, int v1, int v2, int v3, int v4) {
        if (ny == 1) {
            v1 |= 1 << nx;
        } else if (ny == 2) {
            v2 |= 1 << nx;
        } else if (ny == 3) {
            v3 |= 1 << nx;
        } else if (ny == 4) {
            v4 |= 1 << nx;
        }
        return new int[]{v1, v2, v3, v4};
    }

    static void print() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= 4; j++) {
                System.out.print(map[i][j].size());
            }
            System.out.println();
        }
        System.out.println("-");
    }
}