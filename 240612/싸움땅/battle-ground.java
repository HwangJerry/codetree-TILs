import java.util.*;
import java.io.*;
import java.util.stream.*;

public class Main {
    static final int Y = 0;
    static final int X = 1;
    static final int D = 2;
    static final int STR = 3;
    static final int GUN = 4;
    static final int SCORE = 5;

    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;

    static int N, M, K; // 격자 크기, 플레이어 수, 라운드 수
    static Queue<Integer>[][] map;
    static int[] dy = {-1, 0, 1, 0};
    static int[] dx = {0, 1, 0, -1};
    static int x, y, d, s;
    static int[][] battleMap;
    static int[][] people;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        map = new PriorityQueue[N+1][N+1]; // indices start from 1;
        battleMap = new int[N+1][N+1];

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = new PriorityQueue<>(Comparator.comparingInt(o -> -o));
                map[i][j].add(Integer.parseInt(st.nextToken()));
            }
        }
        people = new int[M+1][6];
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            y = Integer.parseInt(st.nextToken());
            x = Integer.parseInt(st.nextToken());
            d = Integer.parseInt(st.nextToken());
            s = Integer.parseInt(st.nextToken()); // 총 : 공격력, 사람 : 능력치
            people[i] = new int[]{y,x,d,s, 0, 0}; // y좌표,x좌표, 이동방향, 능력치, 가진 총의 공격력, 획득한 점수
        }

        for (int T = 0; T < K; T++) {
            // 배틀맵에 먼저 플레이어 반영
            battleMap = new int[N+1][N+1];
            for (int i = 1; i <= M; i++) {
                int[] person = people[i];
                int y = person[Y];
                int x = person[X];
                battleMap[y][x] = i;
            }

            // 첫번째 플레이어부터 순서대로 본인이 향하고 있는 방향대로 한 칸씩 이동.
            for (int i = 1; i <= M; i++) {
                int[] person = people[i];
                int y = person[Y];
                int x = person[X];
                int d = person[D];
                // 만약 격자를 벗어나는 무빙이라면 튕겨져서 1만큼 이동
                int[] next = move(y,x,d);
                int ny = next[0];
                int nx = next[1];
                int nd = next[2];
                person[Y] = ny;
                person[X] = nx;
                person[D] = nd;
                battleMap[y][x] = 0;
                // --> 이동 방향에 플레이어가 X
                if (battleMap[ny][nx] == 0) {
                    // -- 총이 있다면 그걸 획득 (더 쎈 총으로 획득하고, 남은건 해당 격자에 둠)
                    if (!map[ny][nx].isEmpty()) {
                        // 더 쎈 총이면
                        if (people[i][GUN] < map[ny][nx].peek()) {
                            map[ny][nx].add(people[i][GUN]);
                            people[i][GUN] = map[ny][nx].poll();
                        }
                    }
                    // 사람 위치 이동
                    battleMap[ny][nx] = i;

                // --> 이동 방향에 플레이어가 O, -- 싸움. 능력+총 쎈 놈이 이김. 차이만큼 점수 획득
                // ----> 진사람은 본인이 가지고 있는 총을 해당 격자에 내려놓고, 해당 플레이어가 원래 가지고 있던 방향으로 한칸 이동.
                // ----> 진사람이 이동할 때 다른 플레이어가 있거나, 격자 밖 범위인 경우 오른쪽으로 90도 방향 전환하여 빈칸이 보일때 이동. 해당 칸에 총이 있다면 해당 플레이어는 총 획득
                // ------> 이긴 플레이어는 승리한 칸에 있는 총들을 비교하여 가장 공격력 높은거 취함
                } else {
                    int vsIdx = battleMap[ny][nx];
                    int[] vsPerson = people[vsIdx];
                    // 총까지 해서는 비기는 경우
                    if ((person[STR] + person[GUN]) == (vsPerson[STR] + vsPerson[GUN])) {
                        if (person[STR] > vsPerson[STR]) {
                            // 내가 이긴 경우
                            // -- 점수계산부터
                            person[SCORE] += (person[STR] + person[GUN]) - (vsPerson[STR] + vsPerson[GUN]);
                            // -- 진사람은 총을 내려놓고
                            map[ny][nx].add(vsPerson[GUN]);
                            vsPerson[GUN] = 0;
                            // 가능한 방향으로 run
                            for (int k = 0; k < 4; k++) {
                                int vd = (vsPerson[D] + k) < 4 ? vsPerson[D] + k : vsPerson[D] + k - 4;
                                int vy = ny + dy[vd];
                                int vx = nx + dx[vd];
                                if (inRange(vy, vx) && battleMap[vy][vx] == 0) {
                                    // 이동하고 종료
                                    if (!map[vy][vx].isEmpty()) { // 총 있으면 취하고
                                        vsPerson[GUN] = map[vy][vx].poll();
                                    }
                                    battleMap[vy][vx] = vsIdx;
                                    vsPerson[Y] = vy;
                                    vsPerson[X] = vx;
                                    vsPerson[D] = vd;
                                    break;
                                }
                            }
                            battleMap[ny][nx] = i;
                            person[Y] = ny;
                            person[X] = nx;
                            person[D] = nd;
                            // 이긴 사람은 총 갈아끼우기
                            if (person[GUN] < map[ny][nx].peek()) {
                                map[ny][nx].add(person[GUN]);
                                person[GUN] = map[ny][nx].poll();
                            }
                        } else {
                            // 상대가 이긴 경우
                            // -- 점수계산부터
                            vsPerson[SCORE] += (vsPerson[STR] + vsPerson[GUN]) - (person[STR] + person[GUN]);
                            // -- 진사람은 총을 내려놓고
                            map[ny][nx].add(person[GUN]);
                            person[GUN] = 0;
                            // 가능한 방향으로 run
                            for (int k = 0; k < 4; k++) {
                                int vd = person[D] + k < 4 ? person[D] + k : person[D] + k - 4;
                                int vy = ny + dy[vd];
                                int vx = nx + dx[vd];
                                if (inRange(vy, vx) && battleMap[vy][vx] == 0) {
                                    // 이동하고 종료
                                    if (!map[vy][vx].isEmpty()) {
                                        person[GUN] = map[vy][vx].poll();
                                    }
                                    battleMap[vy][vx] = i;
                                    person[Y] = vy;
                                    person[X] = vx;
                                    person[D] = vd;
                                    break;
                                }
                            }
                            // 이긴 사람은 총 갈아끼우기
                            if (vsPerson[GUN] < map[ny][nx].peek()) {
                                map[ny][nx].add(vsPerson[GUN]);
                                vsPerson[GUN] = map[ny][nx].poll();
                            }
                        }
                    } else if (person[STR] + person[GUN] > vsPerson[STR] + vsPerson[GUN]) {
                        // 내가 이긴 경우
                        // -- 점수계산부터
                        person[SCORE] += (person[GUN] + person[STR]) - (vsPerson[STR] + vsPerson[GUN]);
                        // -- 진사람은 총을 내려놓고
                        map[ny][nx].add(vsPerson[GUN]);
                        vsPerson[GUN] = 0;
                        // 가능한 방향으로 run
                        for (int k = 0; k < 4; k++) {
                            int vd = (vsPerson[D] + k) < 4 ? vsPerson[D] + k : vsPerson[D] + k - 4;
                            int vy = ny + dy[vd];
                            int vx = nx + dx[vd];
                            if (inRange(vy, vx) && battleMap[vy][vx] == 0) {
                                // 이동하고 종료
                                if (!map[vy][vx].isEmpty()) { // 총 있으면 취하고
                                    vsPerson[GUN] = map[vy][vx].poll();
                                }
                                battleMap[y][x] = 0;
                                battleMap[vy][vx] = vsIdx;
                                vsPerson[Y] = vy;
                                vsPerson[X] = vx;
                                vsPerson[D] = vd;
                                break;
                            }
                        }
                        battleMap[ny][nx] = i;
                        person[Y] = ny;
                        person[X] = nx;
                        person[D] = nd;
                        // 이긴 사람은 총 갈아끼우기
                        if (person[GUN] < map[ny][nx].peek()) {
                            map[ny][nx].add(person[GUN]);
                            person[GUN] = map[ny][nx].poll();
                        }

                    } else if (person[STR] + person[GUN] < vsPerson[STR] + vsPerson[GUN]) {
                        // 상대가 이긴 경우
                        // -- 점수계산부터
                        vsPerson[SCORE] += (vsPerson[GUN] + vsPerson[STR]) - (person[STR] + person[GUN]);
                        // -- 진사람은 총을 내려놓고
                        map[ny][nx].add(person[GUN]);
                        person[GUN] = 0;
                        // 가능한 방향으로 run
                        for (int k = 0; k < 4; k++) {
                            int vd = person[D] + k < 4 ? person[D] + k : person[D] + k - 4;
                            int vy = ny + dy[vd];
                            int vx = nx + dx[vd];
                            if (inRange(vy, vx) && battleMap[vy][vx] == 0) {
                                // 이동하고 종료
                                if (!map[vy][vx].isEmpty()) {
                                    person[GUN] = map[vy][vx].poll();
                                }
                                battleMap[vy][vx] = i;
                                person[Y] = vy;
                                person[X] = vx;
                                person[D] = vd;
                                break;
                            }
                        }
                        // 이긴 사람은 총 갈아끼우기
                        if (vsPerson[GUN] < map[ny][nx].peek()) {
                            map[ny][nx].add(vsPerson[GUN]);
                            vsPerson[GUN] = map[ny][nx].poll();
                        }

                    }
                }
                
            }

        }

        for (int i = 1; i <= M; i++) {
            System.out.print(people[i][SCORE] + " ");
        }


        
    }
    static int[] move(int y, int x, int d) {
        y = y + dy[d];
        x = x + dx[d];

        if (y < 1) {
            return new int[]{2, x, DOWN};
        } else if (y > N) {
            return new int[]{N-1, x, UP};
        } else if (x < 1) {
            return new int[]{y, 2, RIGHT};
        } else if (x > N) {
            return new int[]{y, N-1, LEFT};
        }
        return new int[]{y, x, d};
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= N && x >= 1 && x <= N;
    }
}