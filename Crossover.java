import java.util.*;


//Eksperimen untuk Perbandingan Crossover
public class Crossover {
    private Random random;

    public Crossover(Random random) {
        this.random = random;
    }

    // SINGLE-POINT CROSSOVER
    // Ide untuk menggunakan crossover ini terinspirasi dari : https://github.com/happygirlzt/minesweeper/blob/master/
    // Sejauh ini menghasilkan best fitness terbaik dibanding crossover yang lain
    public List<int[]> singlePointCrossover(int[] p1, int[] p2) {
        int length = p1.length;
        int point = random.nextInt(length); // Titik potong acak
        int[] c1 = new int[length];
        int[] c2 = new int[length];

        for (int i = 0; i < length; i++) {
            if (i < point) {
                c1[i] = p1[i];
                c2[i] = p2[i];
            } else {
                c1[i] = p2[i];
                c2[i] = p1[i];
            }
        }
        return Arrays.asList(c1, c2);
    }

    // TWO-POINT CROSSOVER
    // Sejauh ini menghasilkan best fitness yang mirip dengan single-point tetapi bisa lebih buruk
    public List<int[]> twoPointCrossover(int[] p1, int[] p2) {
        int length = p1.length;
        int pt1 = random.nextInt(length);
        int pt2 = random.nextInt(length);
        
        if (pt1 > pt2) { // Tukar agar pt1 selalu lebih kecil
            int temp = pt1; pt1 = pt2; pt2 = temp;
        }

        int[] c1 = new int[length];
        int[] c2 = new int[length];

        for (int i = 0; i < length; i++) {
            if (i >= pt1 && i <= pt2) { // Tukar hanya bagian tengah
                c1[i] = p2[i];
                c2[i] = p1[i];
            } else {
                c1[i] = p1[i];
                c2[i] = p2[i];
            }
        }
        return Arrays.asList(c1, c2);
    }

    // UNIFORM CROSSOVER
    // Penggunaan terinspirasi dari rekomendasi LLM Gemini
    // Sejauh ini menghasilkan best fitness terburuk
    public List<int[]> uniformCrossover(int[] p1, int[] p2) {
        int length = p1.length;
        int[] c1 = new int[length];
        int[] c2 = new int[length];

        for (int i = 0; i < length; i++) {
            //Peluang 50:50 menggunakan nextBoolean()
            if (random.nextBoolean()) {
                //Anak 1 mewarisi gen Induk 1, Anak 2 mewarisi gen Induk 2
                c1[i] = p1[i];
                c2[i] = p2[i];
            } else {
                // Anak 1 mewarisi gen Induk 2, Anak 2 mewarisi gen Induk 1
                c1[i] = p2[i];
                c2[i] = p1[i];
            }
        }
        return Arrays.asList(c1, c2);
    }
}