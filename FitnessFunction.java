public class FitnessFunction {
    // Hyper-parameter untuk fitness function
    private static final int EXACT_BONUS = 100; // bonus jika tile hitam sama dengan constraint
    private static final int OVERFILL_PENALTY = 50; // penalty jika tile hitam melebihi constraint
    private static final int UNDERFILL_PENALTY = 5; // penalty jika tile hitam kurang dari constraint
    private static final double HEURISTIC_WEIGHT = 0.05; // seberapa penting heuristic

    private BoardState boardState;
    private double[][] blackProb; // kemungkinan setiap cell/index akan berwarna hitam

    FitnessFunction(BoardState boardState) {
        this.boardState = boardState;
        loadHeuristic();
    }

    double evaluate(int[] chromosome) {
        int[][] board = boardState.decodeChromosome(chromosome);
        double fitness = 0.0;

        // distance fitness(sejauh apa state ke state yang benar)
        // Local Search
        for (int[] constraint : boardState.constraints) {
            int row = constraint[0];
            int col = constraint[1];
            int required = constraint[2];

            int blackCount = 0;

            for (int dRow = -1; dRow <= 1; dRow++) {
                for (int dCol = -1; dCol <= 1; dCol++) {
                    int r = row + dRow;
                    int c = col + dCol;

                    if (r >= 0 && r < boardState.size && c >= 0 && c < boardState.size) {
                        if (board[r][c] == 1) {
                            blackCount++;
                        }
                    }
                }
            }

            int delta = required - blackCount;

            if (delta < 0) {
                fitness -= OVERFILL_PENALTY * (blackCount - required);
            } else if (delta == 0) {
                fitness += EXACT_BONUS;
            } else {
                fitness -= UNDERFILL_PENALTY * (required - blackCount);
            }
        }

        double heuristicPenalty = 0.0;

        for (int i = 0; i < boardState.variables.size(); i++) {
            int row = boardState.variables.get(i)[0];
            int col = boardState.variables.get(i)[1];

            int assigned = board[row][col]; // 0 or 1
            double likelyBlack = blackProb[row][col];

            double diff = assigned - likelyBlack;
            // dikuadrat supaya tidak usah pakai absolute
            // dan untuk mempebesar penalti jika perbedaan besar(yang heuristic yakin)
            // dan meng-negasi penalti jika perbedaan kecil(yang heuristic tidak yakin)
            heuristicPenalty += diff * diff;
        }

        fitness -= HEURISTIC_WEIGHT * heuristicPenalty;

        return fitness;
    }

    // isi array blackProb yang akan dibutuhkan untuk fungsi heuristic
    // Probabilistic Reasoning
    void loadHeuristic() {
        blackProb = new double[boardState.size][boardState.size];

        // default probability = 0 (assume white unless constraints say otherwise)
        for (int r = 0; r < boardState.size; r++) {
            for (int c = 0; c < boardState.size; c++) {
                blackProb[r][c] = 0.0;
            }
        }

        // compute heuristic only for variable cells
        for (int[] cell : boardState.variables) {
            int row = cell[0];
            int col = cell[1];

            double product = 1.0;

            for (int[] constraint : boardState.constraints) {
                // Jika constraint memang merupakan neighbour
                if (Math.abs(row - constraint[0]) <= 1 && Math.abs(col - constraint[1]) <= 1) {
                    double p = probabilityFromConstraint(constraint);

                    // Di konversi untuk mendapatkan A AND B AND C
                    // dan bukan A AND B AND C

                    // Jika langsung A OR B OR C, maka probabilitas > 1

                    // Maka dicari -A AND -B AND -C
                    // supaya dapat di konversi dengan De Morgan menjadi A OR B OR C
                    product *= (1.0 - p);
                }
            }

            // blackProb disini adalah probabilitas bahwa satu atau lebih constraint perlu
            // cell ini menjadi cell hitam atau A OR B OR C
            blackProb[row][col] = 1.0 - product;
        }
    }

    // Mengembalikan probabilitas bahwa constraint ini membutuhkan sebuah unknown
    // cell menjadi cell hitam
    private double probabilityFromConstraint(int[] constraint) {
        int row = constraint[0];
        int col = constraint[1];
        int required = constraint[2];

        int blackCount = 0;
        int unknownCount = 0;

        // baca semua neighbour dan tile sendirinya
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;

                if (newRow >= 0 && newRow < boardState.size && newCol >= 0 && newCol < boardState.size) {
                    if (boardState.black[newRow][newCol]) {
                        blackCount++;
                    } else if (!boardState.white[newRow][newCol]) {
                        unknownCount++;
                    }
                }
            }
        }

        int remaining = required - blackCount;

        // kalau sudah tidak ada required neighbor atau sudah semua guarenteed
        // white/black, berarti akan langsung di return.
        // nilai ini tidak penting kalau cell sudah guarenteed karena tidak akan dipakai
        if (remaining <= 0 || unknownCount <= 0) {
            return 0.0;
        }

        double p = (double) remaining / unknownCount;

        return p;
    }
}
