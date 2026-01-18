import java.util.*;


//Eksperimen untuk Perbandingan Crossover
public class Crossover {
    private Random random;

    public Crossover(Random random) {
        this.random = random;
    } 

    /**
     * SINGLE-POINT CROSSOVER
     * Memilih titik sebagai crossover point lalu mengisi children dengan genes dari kedua parent.
     * Penggunaan terinspirasi dari kode yang menyelesaikan masalah yang hampir serupa.(Minesweeper)
     * link: https://github.com/happygirlzt/minesweeper/tree/master
     * @param parent1 adalah kromosom induk pertama
     * @param parent2 adalah kromosom induk pertama
     * @return list berisi 2 anak hasil persilangan
     */
    public List<int[]> singlePointCrossover(int[] parent1, int[] parent2) {
        int length = parent1.length;
        int point = random.nextInt(length); // Memilih titik pemotong acak untuk crossover.
        int[] children1 = new int[length];
        int[] children2 = new int[length];

        for (int i = 0; i < length; i++) {
            if (i < point) {
                children1[i] = parent1[i];
                children2[i] = parent2[i];
            } else {
                children1[i] = parent2[i];
                children2[i] = parent1[i];
            }
        }
        return Arrays.asList(children1, children2);
    }

    /*
     * TWO-POINT CROSSOVER
     * Memilih 2 titik sebagai crossover point lalu mengisi children dengan genes dari kedua parent.
     * @param parent1 adalah kromosom induk pertama
     * @param parent2 adalah kromosom induk pertama
     * @return list berisi 2 anak hasil persilangan
     */
    public List<int[]> twoPointCrossover(int[] parent1, int[] parent2) {
        int length = parent1.length;
        int pt1 = random.nextInt(length); //Ini adalah point pertama yang akan menjadi titik pemotong.
        int pt2 = random.nextInt(length); //Ini adalah point kedua yang akan menjadi titik pemotong.
        
        if (pt1 > pt2) { // pt1 harus memiliki nilai yang lebih kecil.
            int temp = pt1; pt1 = pt2; pt2 = temp;
        }

        int[] children1 = new int[length];
        int[] children2 = new int[length];

        for (int i = 0; i < length; i++) {
            if (i >= pt1 && i <= pt2) { // Tukar hanya bagian tengah.
                children1[i] = parent2[i];
                children2[i] = parent1[i];
            } else {
                children1[i] = parent1[i];
                children2[i] = parent2[i];
            }
        }
        return Arrays.asList(children1, children2);
    }

    /* 
     * UNIFORM CROSSOVER
     * Menentukan nilai setiap gen pada children berdasarkan pemilihan acak dari salah satu induk.
     * Penggunaan terinspirasi dari rekomendasi LLM Gemini.
     * @param parent1 adalah kromosom induk pertama
     * @param parent2 adalah kromosom induk pertama
     * @return list berisi 2 anak hasil persilangan
     */
    public List<int[]> uniformCrossover(int[] parent1, int[] parent2) {
        int length = parent1.length;
        int[] children1 = new int[length];
        int[] children2 = new int[length];

        for (int i = 0; i < length; i++) {
            //Peluang 50:50 menggunakan nextBoolean()
            if (random.nextBoolean()) {
                //Anak 1 mewarisi gen Induk 1, Anak 2 mewarisi gen Induk 2
                children1[i] = parent1[i];
                children2[i] = parent2[i];
            } else {
                // Anak 1 mewarisi gen Induk 2, Anak 2 mewarisi gen Induk 1
                children1[i] = parent2[i];
                children2[i] = parent1[i];
            }
        }
        return Arrays.asList(children1, children2);
    }
}
