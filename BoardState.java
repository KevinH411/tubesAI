import java.io.*;
import java.util.*;

/*
  BoardState untuk Mosaic Puzzle
  Kelas ini menyimpan informasi papan dan mencari sel-sel yang sudah pasti
  Referensi: Aturan game Mosaic dari https://www.puzzle-minesweeper.com/mosaic-rules/
*/
public class BoardState {
    public int size;                    // ukuran papan (size x size)
    public int[][] grid;                // grid angka dari puzzle (-1 untuk kosong)
    public List<int[]> variables;       // posisi sel yang belum pasti warna putih atau hitam [[row1,col1], [row2,col2], ...]
    public boolean[][] white;           // true jika sel pasti putih
    public boolean[][] black;           // true jika sel pasti hitam  
    public List<int[]> constraints;     // angka-angka constraint [[row,col,number], ...]
    
    // baca puzzle dari file
    public void load(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        
        // baca ukuran papan
        size = Integer.parseInt(br.readLine().trim());
        grid = new int[size][size];
        
        // inisialisasi semua sel sebagai kosong (-1)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = -1;
            }
        }
        
        // baca grid puzzle
        for (int row = 0; row < size; row++) {
            String line = br.readLine().trim();
            for (int col = 0; col < size && col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != '.') {
                    grid[row][col] = Integer.parseInt(String.valueOf(c));
                }
            }
        }
        br.close();
    }
    
    // cari sel-sel yang pasti putih atau hitam
    public void findGuaranteedCells() {
        // inisialisasi semua array
        white = new boolean[size][size];
        black = new boolean[size][size];
        variables = new ArrayList<>();
        constraints = new ArrayList<>();

        // tandai sel angka sebagai putih dan simpan constraints
        findNumberCells();

        // cari sel pasti hitam berdasarkan constraints
        findGuaranteedBlack();

        // kumpulkan sel yang belum pasti sebagai variables
        findVariables();

        showInfo();
    }
    
    // mencari constraints dari hasil konversi input
    private void findNumberCells() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] >= 0) {  // jika ada angka
                    // simpan constraint
                    constraints.add(new int[]{row, col, grid[row][col]});

                    // jika angka 0, tetangganya juga putih
                    if (grid[row][col] == 0) {
                        markZeroNeighbors(row, col);
                    }
                }
            }
        }
    }

    // tandai tetangga angka 0 sebagai putih
    private void markZeroNeighbors(int row, int col) {
        // periksa semua 8 arah sekitar angka 0 dan angka 0 itu sendiri
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int newRow = row + dr;
                int newCol = col + dc;
                
                // cek masih dalam batas papan
                if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                    white[newRow][newCol] = true;
                }
            }
        }
    }

    // cari sel yang pasti hitam
    private void findGuaranteedBlack() {
        for (int[] constraint : constraints) {
            int row = constraint[0];
            int col = constraint[1];
            int number = constraint[2];
    
            List<int[]> candidates = new ArrayList<>();
    
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int newRow = row + dr;
                    int newCol = col + dc; 
                    if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                        // kandidat adalah sel yang belum putih DAN belum hitam
                        if (!white[newRow][newCol] && !black[newRow][newCol]) {
                            candidates.add(new int[]{newRow, newCol});
                        }
                    }
                }
            }
    
            // hitung sudah berapa hitam di sekitar constraint
            int blackCount = 0;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int newRow = row + dr;
                    int newCol = col + dc;
                    if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                        if (black[newRow][newCol]) {
                            blackCount++;
                        }
                    }
                }
            }
    
            // jika sisa kandidat = angka - hitam yang sudah ada, maka semua kandidat pasti hitam
            if (candidates.size() == number - blackCount && candidates.size() > 0) {
                for (int[] pos : candidates) {
                    black[pos[0]][pos[1]] = true;
                }
            }
        }
    }

    // kumpulkan sel yang belum pasti (variables)
    private void findVariables() {
        variables.clear();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // jika belum putih DAN belum hitam, itu variable
                if (!white[row][col] && !black[row][col]) {
                    variables.add(new int[]{row, col});
                }
            }
        }
    }

    /*
      Mengubah kromosom GA menjadi papan hasil
      0 = putih, 1 = hitam
    */
    public int[][] decodeChromosome(int[] chromosome) {
        int[][] encodedBoard = new int[size][size];

        // isi sel fixed
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (black[row][col]) {
                    encodedBoard[row][col] = 1;
                } else {
                    encodedBoard[row][col] = 0;
                }
            }
        }

        // isi sel variabel dari kromosom
        for (int i = 0; i < variables.size(); i++) {
            int row = variables.get(i)[0];
            int col = variables.get(i)[1];
            encodedBoard[row][col] = chromosome[i];
        }

        return encodedBoard;
    }
    
    // tampilkan informasi papan
    private void showInfo() {
        int whiteCount = 0;
        int blackCount = 0;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (white[row][col]) whiteCount++;
                if (black[row][col]) blackCount++;
            }
        }
        
        System.out.println("Papan " + size + "x" + size + " siap");
        System.out.println("Variabel: " + variables.size() + " sel");
        System.out.println("Putih pasti: " + whiteCount + " sel");
        System.out.println("Hitam pasti: " + blackCount + " sel");
    }
    
    public int getVariableCount() {
        return variables.size();
    }
 
    public List<int[]> getVariables() {
        return variables;
    }
}