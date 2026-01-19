import java.util.*;

// Eksperimen untuk Perbandingan Operator Mutasi dalam Algoritma Genetika
public class Mutation {
    private Random random;

    public Mutation(Random random) {
        this.random = random;
    }

    /**
     * Entry point untuk melakukan mutasi. 
     * Saat ini default menggunakan bitFlipMutation.
     * @param chromosome array integer yang merepresentasikan kromosom
     * @param mutationRate peluang terjadinya mutasi (0 hingga 1)
     */
    public void mutate(int[] chromosome, double mutationRate) {
        bitFlipMutation(chromosome, mutationRate);
    }

    
    /**
     * BIT-FLIP MUTATION
     * Umum digunakan pada representasi biner. 
     * Setiap gen dalam kromosom memiliki probabilitas sebesar mutationRate untuk dibalik nilainya.
     * @param chromosome array integer (biner 0 atau 1)
     * @param mutationRate probabilitas mutasi per gen
     */
    public void bitFlipMutation(int[] chromosome, double mutationRate) {
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() <= mutationRate) { //Setiap gen memiliki peluang untuk dibalik
                // Balik nilai: jika 0 jadi 1, jika 1 jadi 0
                chromosome[i] = (chromosome[i] == 0) ? 1 : 0;
            }
        }
    }

    /*
     * SWAP MUTATION
     * Memilih dua posisi (indeks) secara acak pada kromosom, lalu menukar nilai di antara keduanya.
     * @param chromosome array integer yang akan dimutasi
     * @param mutationRate probabilitas terjadinya satu kali operasi swap pada kromosom
     */
    public void swapMutation(int[] chromosome, double mutationRate) {
        if (random.nextDouble() <= mutationRate) {
            // Memilih dua indeks secara acak
            int idx1 = random.nextInt(chromosome.length);
            int idx2 = random.nextInt(chromosome.length);

            //Menukar dua posisi
            int temp = chromosome[idx1];
            chromosome[idx1] = chromosome[idx2];
            chromosome[idx2] = temp;
        }
    }

    /*
     * INVERSION MUTATION
     * Memilih dua titik secara acak untuk membentuk sebuah bagian, 
     * kemudian membalikkan urutan gen di dalam segmen tersebut.
     * @param chromosome array integer yang akan dimutasi
     * @param mutationRate probabilitas terjadinya operasi inversi pada kromosom
     */
    public void inversionMutation(int[] chromosome, double mutationRate) {
        if (random.nextDouble() <= mutationRate) {
            // Memilih dua titik acak untuk menentukan batas segmen
            int point1 = random.nextInt(chromosome.length);
            int point2 = random.nextInt(chromosome.length);

            // Menentukan titik awal dan akhir yang benar
            int start = Math.min(point1, point2);
            int end = Math.max(point1, point2);

            //Membalikkan urutan
            while (start < end) {
                int temp = chromosome[start];
                chromosome[start] = chromosome[end];
                chromosome[end] = temp;
                start++;
                end--;
            }
        }
    }
}
