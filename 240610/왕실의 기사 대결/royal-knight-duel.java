import java.io.*;
import java.util.*;
import java.util.stream.*;

/*
규칙
-1. 상하좌우 1칸씩 이동
-2. 이동하려는 위치에 다른 기사가 있다면 연쇄적으로 밀림(해당 경로의 모든 기사 대상)
-3. 이동 방향의 끝에 벽에 있다면 모든 기사는 이동 불가
-4. 사라진 기사에게 명령을 내리면 아무 일도 일어나지 않음

-5. 기사 범위(H x W) 내의 함정 수 만큼 체력 피해
-6. 체력이 0이 되면 체스판에서 사라짐
-7. 명령을 받은 기사는 피해를 입지 않고, 기사들은 일단 밀린 이후에 데미지를 입음
-8. 밀렸더라도 함정이 없다면 피해를 입지 않음

정답
=> 명령이 모두 끝난 후, '생존'한 기사들이 총 받은 대미지의 합
*/
public class Main {
    static final int R = 0;
    static final int C = 1;
    static final int H = 2;
    static final int W = 3;
    static final int K = 4;
    static final int TRAP = -1;
    static final int WALL = -2;

    static int L, N, Q;
    static int[][] map;
    static int[][] knights; // 기사들 정보
    static int[][] knightMap;

    static int[] dy = {-1, 0, 1, 0}; // 위 오 아래 왼
    static int[] dx = {0, 1, 0, -1};


    static boolean[][] visited;
    static Queue<Integer> damageQueue = new ArrayDeque<>(); // knights 데미지 업데이트 stage {idx} : -1씩
    static Set<Integer> updateSet = new HashSet<>(); //knights 정보에 업데이트할 기사 인덱스 저장 {idx, dir}
    static Queue<int[]> moveQueue = new ArrayDeque<>(); // knightMap 업데이트할 좌표와 방향 저장 {r, c, dir}

    static List<Integer> health = new ArrayList<>();

    static int ans;

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        map = new int[L+1][L+1]; // indices start from 1;
        visited = new boolean[L+1][L+1];
        knightMap = new int[L+1][L+1];
        knights = new int[N+1][5];
        health.add(-1);

        for (int i = 1; i <= L; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= L; j++) {
                map[i][j] = -Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= N; i++) {
            int[] knight = Stream.of(br.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            
            health.add(knight[K]);
            for (int j = 0; j < knight[H]; j++) {
                for (int k = 0; k < knight[W]; k++) {
                    knightMap[knight[R]+j][knight[C]+k] = i; // 기사 인덱스 맵에 주입
                }
            }
            knights[i] = knight;
            
        }
        
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            int dir = Integer.parseInt(st.nextToken());
            if (isDead(idx)) {
                // System.out.println("dead: " + idx);
                continue; // 사라진 기사에 대한 명령은 아무일도 일어나지 않음
            }

            int[] crt = knights[idx];
            // 명령 수행
            visited = new boolean[L+1][L+1];
            visited[crt[R]][crt[C]] = true;
            bfs(crt, dir);

            if (moveQueue.isEmpty()) {
                continue;
            }

            // 먼저 queue를 순회하면서 이동 방향에 벽 끝이 있는지 체크
            if (!canMove(dir)) {
                continue;
            }
            
            // 이동 준비를 마친 상태
            int[][] tempMap = new int[L+1][L+1];
            Set<Integer> selected = new HashSet<>();
            while(!moveQueue.isEmpty()) {
                int[] p = moveQueue.poll();
                int y = p[0];
                int x = p[1];
                selected.add(knightMap[y][x]);
                int ny = y + dy[dir];
                int nx = x + dx[dir];
                if (knightMap[y][x] != idx && map[ny][nx] == TRAP) {
                    damageQueue.add(knightMap[y][x]);
                }
                updateSet.add(knightMap[y][x]);
                tempMap[ny][nx] = knightMap[y][x];
            }
            

            for (int j = 1; j <= L; j++) {
                knightMap[j] = tempMap[j].clone();
            }
            for (int j = 1; j <= N; j++) {
                if (!selected.contains(j) && !isDead(j)) {
                    for (int k = 0; k < knights[j][H]; k++) {
                        for (int l = 0; l < knights[j][W]; l++) {
                            knightMap[knights[j][R]+k][knights[j][C]+l] = j;
                        }
                    }
                }
            }

            for (int jdx : updateSet) {
                knights[jdx][R] += dy[dir];
                knights[jdx][C] += dx[dir];

                // System.out.println("jdx :" + jdx);
                // System.out.println("knights : " + knights[jdx][R] +" "+ knights[jdx][C] +" "+ knights[jdx][H] +" "+ knights[jdx][W] +" "+ knights[jdx][K]);
            }
            updateSet.clear();

  

            // 이동을 마친 상태에서 데미지 정산할 타이밍
            while(!damageQueue.isEmpty()) {
                int jdx = damageQueue.poll();
                if (knights[jdx][K] > 0) {
                    knights[jdx][K]--;
                    ans++;
                    if (knights[jdx][K] <= 0) {
                        int[] knight = knights[jdx];
                        // System.out.println("jdx: " +jdx); // test
                        for (int j = 0; j < knight[H]; j++) {
                            for (int k = 0; k < knight[W]; k++) {
                                // System.out.println(knight[R]+j + " " + (knight[C] + k)); // test
                                knightMap[knight[R]+j][knight[C]+k] = 0; // 기사 맵에서 삭제
                            }
                        }
                        ans -= health.get(jdx);
                    }
                }
            }
            // test
            // for (int j = 1; j <= L; j++) {
            //     for (int k = 1; k <= L; k++) {
            //         System.out.print(knightMap[j][k]+ " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("-");
        }
        System.out.println(ans);

    }

    static boolean canMove(int dir) {
        for (int[] p : moveQueue) {
            if (!inRange(p[0]+dy[dir], p[1]+dx[dir])) {
                return false;
            }
        }
        return true;
    }

    static void bfs(int[] sKnight, int dir) {
        Queue<int[]> q = new ArrayDeque<>();
        Queue<int[]> tempMove = new ArrayDeque<>();
        int startY = sKnight[R];
        int startX = sKnight[C];
        q.add(new int[]{startY, startX});
        tempMove.add(new int[]{startY, startX, dir});

        while (!q.isEmpty()) {
            int[] now = q.poll();
            int y = now[0];
            int x = now[1];
            for (int i = 0; i < 4; i++) {
                int ny = y + dy[i];
                int nx = x + dx[i];

                if (inRange(ny,nx) && knightMap[ny][nx] == knightMap[y][x] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    q.add(new int[]{ny, nx});
                    tempMove.add(new int[]{ny, nx, dir});
                } else if (inRange(ny, nx) && i == dir && knightMap[ny][nx] > 0 && !visited[ny][nx]) { // 다른 기사를 만났는데, 방향이 미는 방향에 있는 경우
                    visited[ny][nx] = true;
                    q.add(new int[]{ny, nx});
                    tempMove.add(new int[]{ny, nx, dir});
                } else if (inRange(ny, nx) && i == dir && map[ny][nx] == WALL) {
                    moveQueue = new ArrayDeque<>();
                    return; // 미는 방향에 벽이 있으면 아무일도 일어나지 않는다.
                }
            }
        }

        // 이동방향에 벽이 없었고, 모두 이동 준비를 마친 상황
        moveQueue = tempMove;
    }

    static boolean inRange(int y, int x) {
        return y >= 1 && y <= L && x >= 1 && x <= L;
    }

    static boolean isDead(int idx) {
        return knights[idx][K] <= 0;
    }
}