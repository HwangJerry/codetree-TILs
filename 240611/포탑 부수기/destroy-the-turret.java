import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static final int ATK = 0;
    static final int ROUND = 1;
    static final int UNDER_ATTACK = 2;
    static final int INF = (int) 1e9;

    static int N, M, K;
    static int[][][] map;
    static int aliveCnt;
    static int[] dy = {0, 1, 0, -1, -1, 1, 1, -1};
    static int[] dx = {1, 0, -1, 0, 1, 1, -1, -1};
    static boolean canLazer;
    static int minDepth;
    static int[][][] minTemp;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int[] input = Stream.of(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        N = input[0]; M = input[1]; K = input[2];
        map = new int[N][M][3]; // 공격력, 가장 최근에 공격했던 라운드, 타격과 무관했는지 여부

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j][ATK] = Integer.parseInt(st.nextToken());
                if (map[i][j][ATK] > 0) {
                    aliveCnt++;
                }
            }
        }
        
        for (int round = 1; round <= K; round++) {
            // System.out.println("aliveCnt : " + aliveCnt);
            // 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지
            if (aliveCnt == 1) {
                break;
            }
            // 1. 공격 준비
            // --> 공격자 선정
            int[] attacker = getAttacker();
            // System.out.println("attacker : " + attacker[0] + " " + attacker[1]); // test

            // --> 공격자 라운드 갱신
            map[attacker[0]][attacker[1]][ROUND] = round;

            // -> 공격자 관련 여부 갱신
            map[attacker[0]][attacker[1]][UNDER_ATTACK] = 1;

            // --> 공격자 공력 증가
            map[attacker[0]][attacker[1]][ATK] += N + M;

            // --> 공격대상 선정
            int[] target = getTarget();
            // System.out.println("target : " + target[0] + " " + target[1]); // test

            // 2. 공격자의 공격
            // --> if 레이저 가능: 레이저 공격
            // if (!shootLazer(attacker[0], attacker[1], target[0], target[1], map[attacker[0]][attacker[1]][ATK])) {

            boolean[][] visited = new boolean[N][M];
            visited[attacker[0]][attacker[1]] = true;
            int[][][] temp = new int[N][M][3];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    temp[i][j] = map[i][j].clone();
                }
            }
            minTemp = new int[N][M][3];
            canLazer = false;
            minDepth = INF;
            shootLazerDfs(attacker[0], attacker[1], attacker[0], attacker[1], target[0], target[1], temp, map[attacker[0]][attacker[1]][ATK], visited, 0);
            if (canLazer) {
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < M; j++) {
                        map[i][j] = minTemp[i][j].clone();
                    }
                }
            } else {
                // --> else: 포탄 공격
                // System.out.println("throw bomb");
                throwBomb(target[0], target[1], map[attacker[0]][attacker[1]][ATK]);
            }

            // System.out.println("--- after attack ---");
            // for (int i = 0; i < N; i++) {
            //     for (int j = 0; j < M; j++) {
            //         System.out.print(map[i][j][ATK] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");
            // for (int i = 0; i < N; i++) {
            //     for (int j = 0; j < M; j++) {
            //         System.out.print(map[i][j][UNDER_ATTACK] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");

            // 포탑 정비
            // --> 공격과 무관했던 포탑은 공격력 1씩 증가
            // System.out.println("--- after upgrade ---");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (map[i][j][UNDER_ATTACK] == 1) {
                        map[i][j][UNDER_ATTACK] = 0;
                    } else if (map[i][j][ATK] != 0) {
                        map[i][j][ATK]++;
                    }
                    // System.out.print(map[i][j][ATK] + " ");
                }
                // System.out.println();
            }
            aliveCnt = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j< M; j++) {
                    if (map[i][j][ATK] > 0) {
                        aliveCnt++;
                    }
                }
            }
        }

        int ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                ans = Math.max(ans, map[i][j][ATK]);
            }
        }

        System.out.println(ans);
    }
    static void shootLazerDfs(int y, int x, int startY, int startX, int targetY, int targetX, int[][][] temp, int atk, boolean[][] visited, int depth) {
        // System.out.println("y x : " + y + " " + x);
        if (depth >= minDepth) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            int ny = convertY(y + dy[i]);
            int nx = convertX(x + dx[i]);
            if (temp[ny][nx][ATK] != 0 && !visited[ny][nx]) {
                if (ny == targetY && nx == targetX) {
                    // 경로에 데미지를 줘야함...
                    int s = temp[ny][nx][ATK];
                    temp[ny][nx][ATK] = temp[ny][nx][ATK] - atk > 0 ? temp[ny][nx][ATK] - atk : 0;
                    temp[ny][nx][UNDER_ATTACK] = 1;
                    canLazer = true;
                    if (minDepth > depth) {
                        minDepth = depth;
                        for (int j = 0; j < N; j++) {
                            for (int k = 0; k < M; k++) {
                                minTemp[j][k] = temp[j][k].clone();
                            }
                        }
                    }
                    temp[ny][nx][ATK] = s;
                    temp[ny][nx][UNDER_ATTACK] = 0;
                    return;
                }
                int save = temp[ny][nx][ATK];
                temp[ny][nx][ATK] = temp[ny][nx][ATK] - atk/2 > 0 ? temp[ny][nx][ATK] - atk/2 : 0;
                temp[ny][nx][UNDER_ATTACK] = 1;
                visited[ny][nx] = true;
                shootLazerDfs(ny, nx, startY, startX, targetY, targetX, temp, atk, visited, depth+1);
                temp[ny][nx][ATK] = save;
                temp[ny][nx][UNDER_ATTACK] = 0;
            }
        }
    }

    static boolean shootLazer(int startY, int startX, int targetY, int targetX, int atk) {
        Queue<int[]> q = new PriorityQueue<>((o1, o2) -> {
            // 맨해튼 거리 오름차순 (더 짧은 순으로 gogo)
            return (Math.abs(o1[0]-targetY) + Math.abs(o1[1]-targetX)) - (Math.abs(o2[0]-targetY) + Math.abs(o2[1]-targetX));
        });
        q.add(new int[]{startY, startX});
        boolean[][] visited = new boolean[N][M];
        visited[startY][startX] = true;
        int[][][] temp = new int[N][M][3];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                temp[i][j] = map[i][j].clone();
            }
        }

        int halfAtk = atk / 2;
        // System.out.println("---- bfs ---");
        while(!q.isEmpty()) {
            int[] now = q.poll();
            int y = convertY(now[0]);
            int x = convertX(now[1]);
            // System.out.println(y + " " + x);
            if (startY != y || startX != x) {
                temp[y][x][ATK] = temp[y][x][ATK] - halfAtk > 0 ? temp[y][x][ATK] - halfAtk : 0;
                temp[y][x][UNDER_ATTACK] = 1;
            }
            for (int i = 0; i < 4; i++) {
                int ny = convertY(y + dy[i]);
                int nx = convertX(x + dx[i]);
                if (temp[ny][nx][ATK] != 0 && !visited[ny][nx]) {
                    if (ny == targetY && nx == targetX) {
                        // 경로에 데미지를 줘야함...
 
                        // System.out.println("--- true end ---");
                        return true;
                    }
                    visited[ny][nx] = true;
                    q.add(new int[]{ny, nx});
                }
            }
        }
        // System.out.println("--- false end ---");

        return false;
    }

    static void throwBomb(int targetY, int targetX, int atk) {
        int halfAtk = atk / 2;

        doAttack(targetY, targetX, atk);

        for (int i = 0; i < 8; i++) {
            doAttack(convertY(targetY+dy[i]), convertX(targetX+dx[i]), halfAtk);
        }
    }

    static int convertY(int y) {
        if (y < 0) {
            return N + y;
        } else if (y >= N) {
            return y - N;
        }
        return y;
    }

    static int convertX(int x) {
        if (x < 0) {
            return M + x;
        } else if (x >= M) {
            return x - M;
        }
        return x;
    }

    static void doAttack(int targetY, int targetX, int atk) {
        map[targetY][targetX][ATK] = map[targetY][targetX][ATK] - atk > 0 ? map[targetY][targetX][ATK] - atk : 0;
        if (map[targetY][targetX][ATK] != 0) {
            map[targetY][targetX][UNDER_ATTACK] = 1;
        }
    }

    static int[] getAttacker() {
        int minAtk = INF;
        int maxRnd = 0;
        int[] attacker = new int[]{0, 0};
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j][ATK] == 0) {
                    continue;
                }
                if (minAtk > map[i][j][ATK]) { // 1. 공격력이 가장 낮은 포탑
                    attacker = new int[]{i, j};
                    minAtk = map[i][j][ATK];
                    maxRnd = map[i][j][ROUND];
                } else if (minAtk == map[i][j][ATK]) { 
                    if (maxRnd < map[i][j][ROUND]) { // 2. 가장 최근에 공격한 포탑
                        attacker = new int[]{i, j};
                        minAtk = map[i][j][ATK];
                        maxRnd = map[i][j][ROUND];
                    } else if (maxRnd == map[i][j][ROUND]) {
                        if (attacker[0] + attacker[1] < i + j) { // 3. 행렬 합이 가장 큰 포탑
                            attacker = new int[]{i, j};
                            minAtk = map[i][j][ATK];
                            maxRnd = map[i][j][ROUND];
                        } else if (attacker[0] + attacker[1] == i + j) {
                            if (attacker[0] < i) { // 열 값이 가장 큰 포탑
                                attacker = new int[]{i, j};
                                minAtk = map[i][j][ATK];
                                maxRnd = map[i][j][ROUND];
                            }
                        }
                    }
                }
            }
        }
        return attacker;
    }

    static int[] getTarget() {
        int[] target = new int[]{N, M};
        int maxAtk = 0;
        int minRnd = INF;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j][ATK] == 0) {
                    continue;
                }
                if (maxAtk < map[i][j][ATK]) { // 1. 공격력이 가장 높은 포탑
                    target = new int[]{i, j};
                    maxAtk = map[i][j][ATK];
                    minRnd = map[i][j][ROUND];
                } else if (maxAtk == map[i][j][ATK]) { 
                    if (minRnd > map[i][j][ROUND]) { // 2. 가장 옛날에 공격한 포탑
                        target = new int[]{i, j};
                        maxAtk = map[i][j][ATK];
                        minRnd = map[i][j][ROUND];
                    } else if (minRnd == map[i][j][ROUND]) {
                        if (target[0] + target[1] > i + j) { // 3. 행렬 합이 가장 작은 포탑
                            target = new int[]{i, j};
                            maxAtk = map[i][j][ATK];
                            minRnd = map[i][j][ROUND];
                        } else if (target[0] + target[1] == i + j) {
                            if (target[0] > i) { // 열 값이 가장 작은 포탑
                                target = new int[]{i, j};
                                maxAtk = map[i][j][ATK];
                                minRnd = map[i][j][ROUND];
                            }
                        }
                    }
                }
            }
        }
        return target;
    }
}