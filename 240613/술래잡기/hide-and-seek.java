import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static final int Y = 0;
    static final int X = 1;
    static final int D = 2;
    static final int MODE = 3;

    // default : counter-clockwise
    static final int DOWN = 0;
    static final int RIGHT = 1;
    static final int UP = 2;
    static final int LEFT = 3;

    static final int CLOCK_WISE = -1;
    static final int COUNTER_CLOCK_WISE = 1;

    static int N, M, H, K;
    static int ans;
    static int[] chaser;
    static boolean[][] tree;
    static int[] dy = {1, 0, -1, 0}; // 나머지가 0이면 상하 (기본 하)
    static int[] dx = {0, 1, 0, -1}; // 나머지가 1이면 좌우 (기본 우)
    static ArrayList<Integer>[][] map;
    static ArrayList<Integer>[][] temp;
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken()); // 반드시 홀수
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        tree = new boolean[N+1][N+1];
        map = new ArrayList[N+1][N+1];
        chaser = new int[]{N/2 + 1, N/2 + 1, UP, CLOCK_WISE}; // 방향이 위를 보면서 시작, mod(나가고 있으면 0, 들어오고 있으면 1)

        // 미리 모든 움직임을 queue에 담아두는건 어떨까?
        ArrayDeque<int[]> chasingQueue = new ArrayDeque<>(); // {dy, dx, d}
        int r = 0;
        int d = UP; // default value of direction
        int mode = CLOCK_WISE; // default value of mode
        int round = 0;
        while (chasingQueue.size() < K) {
            round++;
            // 홀수마다 이동 거리(r)를 하나씩 늘린다.
            if (round % 2 == 1) {
                r += -mode;
            }
            // 끝쪽 라인 가면 3개를 한번에 추가한다.
            if (((round==1 || round==2) && r == 1) || r >= 2 && r < N - 1) {
                if (r == 1) {
                    chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + mode)});
                    d = convertDir(d + mode);

                } else {
                    for (int i = 0; i < r-1; i++) {
                        chasingQueue.add(new int[]{dy[d], dx[d], d});
                    }
                    chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + mode)});
                    d = convertDir(d + mode); // 방향 진행
                }
            } else if (r == N-1) {
                // 나갈 때
                if (mode == CLOCK_WISE) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < r; j++) {
                            if (i == 2 && j == r-1) {
                                chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + 2)});
                            } else {
                                chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + (j == r-1 ? mode : 0))});
                            }
                        }
                        d = convertDir(d + (i == 2 ? 2 : mode));
                    }
                    mode = changeMode(mode);
                // 다시 돌아갈 때
                } else {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0 ; j < r; j++) {
                            chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + (j == r-1 ? mode : 0))});
                        }
                        d = convertDir(d + mode);
                    }
                }
            } else if (r == 1) {
                if (mode == COUNTER_CLOCK_WISE) { // 들어갈 때
                    for (int i = 0; i < 2; i++) {
                        if (i == 1) {
                            chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + 2)});
                        } else {
                            chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + mode)});
                        }
                        d = convertDir(d + (i == 1 ? 2 : mode));
                    }
                    mode = changeMode(mode);
                } else { // 다시 돌려나올때
                     for (int i = 0; i < 2; i++) {
                        chasingQueue.add(new int[]{dy[d], dx[d], convertDir(d + mode)});
                        d = convertDir(d + mode);
                     }
                }
            // 이동거리를 dy dx로 계산해준다.
            }
        }
        while(chasingQueue.size() != K) {
            chasingQueue.pollLast();
        }

        // // test
        // while(!chasingQueue.isEmpty()) {
        //     int[] now = chasingQueue.poll();
        //     System.out.println(now[0] + " " + now[1] + " " + (now[2] == UP ? "UP" : now[2] == DOWN ? "DOWN" : now[2] == RIGHT ? "RIGHT" : "LEFT"));
        // }

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                map[i][j] = new ArrayList<>();
            }
        }

        // 도망자 위치와 이동방법 입력
        for (int i = 1; i <= M; i++) {
            // 처음에 주어질 땐 겹치게 주어지지 않지만, 나중에 이동중에는 겹칠 수 있음에 유의
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            // 1인 경우 : 좌우로만 (default : 오른쪽) , 2인 경우 : 상하로만 (default : 아래쪽)
            int dd = Integer.parseInt(st.nextToken()) == 1 ? 1 : 0; // 나머지 값 0, 1 로 구분함 (방향전환은 (d + 2) % 4)
            map[y][x].add(dd);
        }

        // 나무의 위치 입력
        for (int i = 1; i <= H; i++) {
            // 나무끼리 겹치게 주어지진 않음
            // 단, 도망자와 나무가 겹쳐져 주어지는 것은 --가능--
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            tree[y][x] = true;
        }

        // for (int i = 1; i <= N; i++) {
        //     for (int j = 1; j <= N; j++) {
        //         System.out.print(map[i][j].peek() + " ");
        //     }
        //     System.out.println();
        // }

        // business logic below ...
        for (int T = 1; T <= K; T++) {
            temp = new ArrayList[N+1][N+1];
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    temp[i][j] = new ArrayList<>();
                }
            }

            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    if (dist(i, j, chaser[0], chaser[1]) <= 3) {
                        for (int pd : map[i][j]) {
                            int npy = i + dy[pd];
                            int npx = j + dx[pd];
                            while(true) {
                                if (inRange(npy,npx) && chaser[Y] != npy || chaser[X] != npx) {
                                    temp[npy][npx].add(pd);
                                    break;
                                } else {
                                    pd = (pd + 2) % 4;
                                    npy = i + dy[pd];
                                    npx = j + dx[pd];
                                }
                            }
                        }                        
                    } else {
                        temp[i][j].addAll(map[i][j]);
                    }
                }
            }
            map = temp;

            // System.out.println("after people move " + T + " ---- ");
            // for (int i = 1; i <= N; i++) {
            //     for (int j = 1; j <= N; j++) {
            //         System.out.print(map[i][j].size() + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");
            // System.out.println("chaser : " + chaser[0] + " " + chaser[1] + " " + chaser[2]);
            // System.out.println("-");
        
            /* 술래가 움직이고 */
            int y = chaser[Y];
            int x = chaser[X];
            // 술래는 달팽이 모양으로 움직임
            // 만약 끝에 도달하면 다시 거꾸로 중심으로 이동
            // k번의 턴 동안 위와 같은 규칙으로 1칸씩 계속 이동
            // 만약 이동방향이 틀어지는 지점이라면 바로 틀음
            int[] move = chasingQueue.poll();
            int ddy = move[0];
            int ddx = move[1];
            int ddd = move[2];
            chaser[Y] = y + ddy;
            chaser[X] = x + ddx;
            chaser[D] = ddd;

            // 움직이고 나서 시야 방향으로 자기 칸 포함 3칸까지 바라봄
            for (int i = 0; i < 3; i++) {
                int ey = chaser[Y]+dy[chaser[D]]*i;
                int ex = chaser[X]+dx[chaser[D]]*i;
                if (inRange(ey, ex) && !map[ey][ex].isEmpty() && !tree[ey][ex]) {
                    ans += map[ey][ex].size()*T;
                    M -= map[ey][ex].size();
                    map[ey][ex].clear();
                }
            }

            // System.out.println("after chaser move " + T + " ---- ");
            // for (int i = 1; i <= N; i++) {
            //     for (int j = 1; j <= N; j++) {
            //         System.out.print(map[i][j].size() + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");
            // System.out.println("chaser : " + chaser[0] + " " + chaser[1] + " " + chaser[2]);
            // System.out.println("-");

            if (M == 0) {
                break;
            }
        }


        // 정답: 술래가 k번의 턴 동안 얻게되는 총 점수 출력
        System.out.println(ans);
    }


    static int dist(int y1, int x1, int y2, int x2) {
        return Math.abs(y1 - y2) + Math.abs(x1 - x2);
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= N && x >= 1 && x <= N;
    }

    static int changeMode(int mode) {
        return mode == 1 ? -1 : 1;
    }

    static int convertDir(int d) {
        if (d < 0) {
            return d + 4;
        } else if (d >= 4) {
            return d % 4;
        }
        return d;
    }
}