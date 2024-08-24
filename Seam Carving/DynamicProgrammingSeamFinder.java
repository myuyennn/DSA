package seamcarving;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        int numRows = energies.length;
        int numCols = energies[0].length;

        int[][] spt = new int[numRows][numCols];
        double[][] minTotalEnergy = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++)
        {
            minTotalEnergy[i][0] = energies[i][0];
        }

        for (int j = 1; j < numCols; j++)
        {
            for (int i = 0; i < numRows; i++)
            {
                double minEnergy = minTotalEnergy[i][j - 1];
                int minIndex = i;

                //comparing left-up neighbor
                if (i > 0 && minTotalEnergy[i - 1][j - 1] < minEnergy)
                {
                    minEnergy = minTotalEnergy[i - 1][j - 1];
                    minIndex = i - 1;
                }
                //comparing left-down neighbor
                if (i < numRows - 1 && minTotalEnergy[i + 1][j - 1] < minEnergy)
                {
                    minEnergy = minTotalEnergy[i + 1][j - 1];
                    minIndex = i + 1;
                }

                //adds the one with the min energy
                minTotalEnergy[i][j] = minEnergy + energies[i][j];

                //adds same one to the path
                spt[i][j] = minIndex;
            }
        }

        int minEnergyIndex = 0;
        List<Integer> result = new ArrayList<>();

        for (int i = 1; i < numRows; i++)
        {
            if (minTotalEnergy[i][numCols - 1] < minTotalEnergy[minEnergyIndex][numCols - 1])
            {
                minEnergyIndex = i;
            }
        }

        for (int j = numCols - 1; j >= 0; j--)
        {
            result.add(minEnergyIndex);
            minEnergyIndex = spt[minEnergyIndex][j];
        }

        Collections.reverse(result);
        return result;
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        // transpose the energy matrix to reuse the findVerticalSeam method
        int numCols = energies.length;
        int numRows = energies[0].length;
        double[][] transposed = new double[numRows][numCols];

        for (int i = 0; i < numCols; i++)
        {
            for (int j = 0; j < numRows; j++)
            {
                transposed[j][i] = energies[i][j];
            }
        }

        return findVerticalSeam(transposed);
    }
}
