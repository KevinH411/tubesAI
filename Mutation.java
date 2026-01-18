import java.util.*;

public class Mutation {
    private Random random;

    public Mutation(Random random) {
        this.random = random;
    }


    public void mutate(int[] chromosome, double mutationRate) {
        inversionMutation(chromosome, mutationRate);
    }

    
    //BIT-FLIP MUTATION (Standar untuk Biner)
    public void bitFlipMutation(int[] chromosome, double mutationRate) {
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() <= mutationRate) { //Setiap gen memiliki peluang untuk dibalik
                // Balik nilai: jika 0 jadi 1, jika 1 jadi 0
                chromosome[i] = (chromosome[i] == 0) ? 1 : 0;
            }
        }
    }

    //SWAP MUTATION (Standar untuk Biner)
    public void swapMutation(int[] chromosome, double mutationRate) {
        if (random.nextDouble() <= mutationRate) {
            int idx1 = random.nextInt(chromosome.length);
            int idx2 = random.nextInt(chromosome.length);

            //Menukar dua posisi
            int temp = chromosome[idx1];
            chromosome[idx1] = chromosome[idx2];
            chromosome[idx2] = temp;
        }
    }

    //INVERSION MUTATION
    public void inversionMutation(int[] chromosome, double mutationRate) {
        if (random.nextDouble() <= mutationRate) {
            int point1 = random.nextInt(chromosome.length);
            int point2 = random.nextInt(chromosome.length);

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