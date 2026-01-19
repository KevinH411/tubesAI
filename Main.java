import java.io.*;
import java.util.*;

public class Main {

    private static final String RESULT_FILE = "eksperimen/result.txt";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(
                    "Usage: java Main <puzzle_file> [populationSize] [maxGenerations] [tournamentSize] [crossoverRate] [mutationRate] [seed]");
            return;
        }

        String puzzleFile = args[0];
        int populationSize = (args.length >= 2) ? Integer.parseInt(args[1]) : 50;
        int maxGenerations = (args.length >= 3) ? Integer.parseInt(args[2]) : 500;
        int tournamentSize = (args.length >= 4) ? Integer.parseInt(args[3]) : 3;
        double crossoverRate = (args.length >= 5) ? Double.parseDouble(args[4]) : 0.8;
        double mutationRate = (args.length >= 6) ? Double.parseDouble(args[5]) : 0.05;
        long seed = (args.length >= 7) ? Long.parseLong(args[6]) : System.currentTimeMillis();

        System.out.printf(
                "Puzzle: %s | pop=%d | gens=%d | tour=%d | cx=%.2f | mut=%.3f | seed=%d%n",
                puzzleFile, populationSize, maxGenerations,
                tournamentSize, crossoverRate, mutationRate, seed);

        try {
            Random rng = new Random(seed);

            // Load board
            BoardState board = new BoardState();
            board.load(puzzleFile);
            board.findGuaranteedCells();

            // Create GA components
            PopulationInitializer initializer = new PopulationInitializer(board, rng);
            List<int[]> population = initializer.generatePopulation(populationSize); // Generate gen 0 population

            FitnessFunction fitness = new FitnessFunction(board);
            TournamentSelection tournament = new TournamentSelection(fitness, rng);
            Crossover crossover = new Crossover(rng);
            Mutation mutation = new Mutation(rng);

            int generation = 0;
            double bestFitness = Double.NEGATIVE_INFINITY;
            int[] bestChromosome = null;

            // Gen 0 evaluation
            for (int[] chrom : population) {
                double f = fitness.evaluate(chrom);
                if (f > bestFitness) {
                    bestFitness = f;
                    bestChromosome = chrom.clone();
                }
            }

            long genTimeSum = 0; // total time for last 10 generations (ns)
            int genCount = 0;
            // GA loop
            while (generation < maxGenerations) {
                long genStartTime = System.nanoTime();
                generation++;

                if (bestChromosome != null && isSolution(board, bestChromosome)) {
                    System.out.println("✔ Found exact solution at generation " + generation);
                    printSolution(board, bestChromosome);
                    writeResult(generation);
                    return;
                }

                List<int[]> nextGen = new ArrayList<>(populationSize);

                while (nextGen.size() < populationSize) {
                    int[] parent1 = tournament.selectParent(population, tournamentSize);
                    int[] parent2 = tournament.selectParent(population, tournamentSize);

                    int[] child1 = parent1.clone();
                    int[] child2 = parent2.clone();

                    if (rng.nextDouble() < crossoverRate) {
                        List<int[]> offspring = crossover.twoPointCrossover(parent1, parent2);
                        child1 = offspring.get(0).clone();
                        child2 = offspring.get(1).clone();
                    }

                    mutation.mutate(child1, mutationRate);
                    mutation.mutate(child2, mutationRate);

                    nextGen.add(child1);
                    if (nextGen.size() < populationSize) {
                        nextGen.add(child2);
                    }
                }

                population = nextGen;

                for (int[] chrom : population) {
                    double f = fitness.evaluate(chrom);
                    if (f > bestFitness) {
                        bestFitness = f;
                        bestChromosome = chrom.clone();
                    }
                }

                long genEndTime = System.nanoTime();
                long genDuration = genEndTime - genStartTime;

                genTimeSum += genDuration;
                genCount++;

                if (generation % 10 == 0) {
                    double avgMs = (genTimeSum / 1_000_000.0) / genCount;
                    System.out.printf(
                            "Gen %d | bestFitness=%.3f | avgTime=%.3f ms/gen%n",
                            generation, bestFitness, avgMs);

                    // reset for next 10 generations
                    genTimeSum = 0;
                    genCount = 0;
                } else if (generation == 1) {
                    System.out.printf("Gen %d | bestFitness=%.3f%n", generation, bestFitness);
                }
            }

            // No solution
            System.out.println("✖ Reached max generations, no exact solution found.");
            writeResult(-1);

        } catch (Exception e) {
            e.printStackTrace();
            writeResult(-1);
        }
    }

    // Eksperimntasi, -1 if failed, x if found
    private static void writeResult(int value) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESULT_FILE, true))) {
            out.println(value);
        } catch (IOException e) {
            System.err.println("Failed to write result file");
        }
    }

    private static boolean isSolution(BoardState board, int[] chromosome) {
        int[][] decoded = board.decodeChromosome(chromosome);

        for (int[] constraint : board.constraints) {
            int r = constraint[0];
            int c = constraint[1];
            int required = constraint[2];

            int blackCount = 0;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < board.size && nc >= 0 && nc < board.size) {
                        if (decoded[nr][nc] == 1)
                            blackCount++;
                    }
                }
            }
            if (blackCount != required)
                return false;
        }
        return true;
    }

    private static void printSolution(BoardState board, int[] chromosome) {
        int[][] decoded = board.decodeChromosome(chromosome);

        System.out.println("\nFinal board:");
        for (int r = 0; r < board.size; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < board.size; c++) {
                boolean isBlack = decoded[r][c] == 1;
                boolean hasNumber = board.grid[r][c] >= 0;

                if (isBlack && hasNumber)
                    sb.append(board.grid[r][c]).append('B');
                else if (isBlack)
                    sb.append('X');
                else if (hasNumber)
                    sb.append(board.grid[r][c]);
                else
                    sb.append('.');

                if (c < board.size - 1)
                    sb.append(' ');
            }
            System.out.println(sb);
        }
    }
}