import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static final int B = 1;
    static int N, M;
    static int[][] map;
    static boolean[] arrived;
    static List<int[]> people = new ArrayList<>();
    static int[][] targets;
    static int[] dy = {-1, 0, 0, 1};
    static int[] dx = {0, -1, 1, 0};
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N+1][N+1];
        targets = new int[M+1][2];
        arrived = new boolean[M+1];

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            targets[i][0] = y;
            targets[i][1] = x;
        }

        // M명의 사람이 순차적으로 출발
        // -- 1분에 1번, 2분에 2번, ... m분에 m번 사람이 출발
        // -- 출발 시간 전에 격자 밖에서 대기
        Queue<Integer> arrivedTargets = new ArrayDeque<>();

        int T = 0;
        while(true) {
            T++;

            // // test
            // for (int i = 1; i <= N; i++) {
            //     for (int j = 1; j <= N; j++) {
            //         System.out.print(map[i][j] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");

            // 격자에 사람이 있는 경우 1,2번 수행
            if (!fin(T-1)) { // T번째 사람은 이제 입장하니까 제외
                // 1. 모든 사람이 각자 원하는 편의점을 향해 1칸 움직임. (최단거리-상좌우하 우선순위)
                // @TODO
                for (int[] person : people) {
                    int tdx = person[0];
                    if (arrived[tdx]) { // 이미 도착한 사람에 대하여는 패스
                        continue;
                    }
                    int y = person[1];
                    int x = person[2];
                    int targetY = targets[tdx][0];
                    int targetX = targets[tdx][1];

                    // BFS 진행
                    // --> 최단거리 + 상좌우하 우선순위로 1칸 전진방향 설정
                    // --> 일단 가까워져야하고, 거리가 동일하면 상좌우하 순으로 선택
                    int ny = (int) 1e9;
                    int nx = (int) 1e9;
                    for (int i = 0; i < 4; i++) {
                        int ty = y + dy[i];
                        int tx = x + dx[i];
                        if (canGo(ty, tx)) {
                            if (dist(ny,nx,targetY,targetX) > dist(ty,tx, targetY, targetX)) {
                                // 등호를 제거하여 상좌우하 우선순위 적용
                                ny = ty;
                                nx = tx;
                            }
                        }
                    }
                    person[1] = ny;
                    person[2] = nx;
                    // 2. 편의점에 도착했다면 일단 멈춤. 이때부터 벽이 되어, 다른 사람들은 통과 불능.
                    // -- 단, 격자에 있는 사람들이 모두 이동한 뒤에 해당 칸을 지나갈 수 없어짐에 유의
                    // ---> 따라서 arrivedTargets에 추가해두고 나중에 한번에 처리
                    if (ny == targetY && nx == targetX) { // 도착했으면 일단 도착목록에 리스트업
                        arrivedTargets.add(tdx);
                    }
                }

                // 모든 인원이 다 움직인 이후에 도착 편의점에 대하여 벽 처리
                while (!arrivedTargets.isEmpty()) {
                    int idx = arrivedTargets.poll();
                    arrived[idx] = true;
                    map[targets[idx][0]][targets[idx][1]] += 10000;
                }
            }
            // 3. t번째 사람이 베이스캠프로 이동하면서 시작. 이때부터 베이스캠프는 통과할 수 없는 장애물이 됨.
            // --> 먼저, t번 편의점과 가장 가까운 베이스캠프 좌표를 찾아야 함
            if (T <= M) {
                int[] base = findNearestBase(T);
                people.add(new int[]{T, base[0], base[1]}); // target, y, x;

                // --> 해당 베이스 벽 처리 (이거 모든 사람들이 이동한 뒤에 처리해야 하는데, 이미 이동할 애들은 1,2번에서 이동했으니 OK)
                map[base[0]][base[1]] += 10000; // 1만 이상 되는 지역은 벽으로 인식하도록
            }

            // 종료 조건 : 모두 편의점에 갔을 것
            if (fin(M)) {
                break;
            }
        }

        System.out.println(T);
    }

    static int dist(int y1, int x1, int y2, int x2) {
        return Math.abs(y1-y2) + Math.abs(x1- x2);
    }

    static boolean canGo(int y, int x) {
        return inRange(y, x) && map[y][x] < 10000;
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= N && x >= 1 && x <= N;
    }

    static boolean fin(int end) {
        if (end < 1) { // 가장 처음에는 빠르게 pass
            return true;
        }
        boolean finish = true;
        end = end <= M ? end : M;
        for (int i = 1; i <= end; i++) {
            if (!arrived[i]) {
                finish = false;
                break;
            }
        }
        return finish;
    }

    static int[] findNearestBase(int t) {
        // -- 편의점에서 가장 가까운 베이스캠프가 여러개일 경우, 그 중 행이 작은 베이스캠프, 행이 같다면 열이 작은 베이스캠프로 감
        int y = targets[t][0];
        int x = targets[t][1];
        
        int ny = (int) 1e9;
        int nx = (int) 1e9;
        // 좌표를 중심으로 가장 가까운 베이스캠프를 찾아야 함
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (map[i][j] == B) {
                    if (dist(ny,nx,y,x) > dist(i, j, y, x)) {
                        ny = i;
                        nx = j;
                    }
                }
            }
        }

        return new int[]{ny, nx};
    }
}