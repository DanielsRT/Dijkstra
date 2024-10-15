import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        int[][] adjacencyMatrix = getInputFile();
        int startVertex = getStartVertex(adjacencyMatrix);
        int endVeretx = getEndVertex(adjacencyMatrix);

        // UCFID: 4838979 - 48,83  83,38  38,89  89,97  97,79
        dijkstra(adjacencyMatrix, startVertex,endVeretx);
    }

    private static int[][] convertListToArray(List<int[]> list)
    {
        int[][] array = new int[list.size()][list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static void dijkstra(int[][] adjacencyMatrix, int startVertex, int endVertex) throws IOException
    {
        int numVertices = adjacencyMatrix[0].length;
        int[] weights = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        for (int vertexIndex = 0; vertexIndex < numVertices;
             vertexIndex++)
        {
            weights[vertexIndex] = Integer.MAX_VALUE;
            visited[vertexIndex] = false;
        }

        weights[startVertex] = 0;
        int[] previousVertex = new int[numVertices];
        previousVertex[startVertex] = -1;

        for (int i = 1; i < numVertices; i++)
        {
            int nearestVertex = -1;
            int smallestWeight = Integer.MAX_VALUE;
            for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++)
            {
                if (!visited[vertexIndex] && weights[vertexIndex] < smallestWeight)
                {
                    nearestVertex = vertexIndex;
                    smallestWeight = weights[vertexIndex];
                }
            }

            visited[nearestVertex] = true;

            for (int vertexIndex = 0;
                 vertexIndex < numVertices;
                 vertexIndex++)
            {
                int weight = adjacencyMatrix[nearestVertex][vertexIndex];

                if (weight > 0 && ((smallestWeight + weight) < weights[vertexIndex]))
                {
                    previousVertex[vertexIndex] = nearestVertex;
                    weights[vertexIndex] = smallestWeight + weight;
                }
            }
        }

        saveResults(startVertex, endVertex, weights[endVertex], previousVertex);
    }

    private static int getStartVertex(int[][] adjacencyMatrix)
    {
        boolean hasInput = false;
        while (!hasInput)
        {
            try {
                System.out.print("\nEnter start vertex: ");
                Scanner keyb = new Scanner(System.in);
                int input = Integer.parseInt(keyb.nextLine());
                if (input >= 0 && input <= adjacencyMatrix.length-1) return input;
                else System.out.println("Vertex out of bounds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private static int getEndVertex(int[][] adjacencyMatrix)
    {
        boolean hasInput = false;
        while (!hasInput)
        {
            try {
                System.out.print("\nEnter end vertex: ");
                Scanner keyb = new Scanner(System.in);
                int input = Integer.parseInt(keyb.nextLine());
                if (input >= 0 && input <= adjacencyMatrix.length-1) return input;
                else System.out.println("Vertex out of bounds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private static int[][] getInputFile()
    {
        boolean hasInput = false;
        while(!hasInput) {
            try {
                System.out.print("Enter input filename(leave blank for default): ");
                Scanner keyb = new Scanner(System.in);
                String input = keyb.nextLine();
                if (input.isEmpty()) return readDefaultInput("Data.txt");
                else {
                    File f = new File(input);
                    if (f.exists() && !f.isDirectory()) {
                        hasInput = true;
                        return readInput(input);
                    } else System.out.println("File Not Found!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static List<Integer> getPath(int currentVertex, int[] parents, List<Integer> result)
    {
        if (currentVertex == -1)
        {
            Collections.reverse(result);
            return result;
        }

        result.add(currentVertex);
        return getPath(parents[currentVertex], parents, result);
    }

    public static int[][] readDefaultInput(String filename)
    {
        List<int[]> adjacencyList = new ArrayList<>();
        InputStream inputStream = Main.class.getResourceAsStream(filename);
        if (inputStream != null)
        {
            try (Scanner scanner = new Scanner(inputStream))
            {
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    String[] lineParts = line.split("\\s+");
                    String[] strArray = lineParts[1].split(",");
                    int[] intArray = new int[strArray.length];
                    for(int i = 0; i < strArray.length; i++)
                    {
                        intArray[i] = Integer.parseInt(strArray[i]);
                    }
                    adjacencyList.add(intArray);
                }
            }
        } else System.out.println("Resource Not Found");

        return convertListToArray(adjacencyList);
    }

    private static int[][] readInput(String filename)
    {
        List<int[]> adjacencyList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineParts = line.split("\\s+");
                String[] strArray = lineParts[1].split(",");
                int[] intArray = new int[strArray.length];
                for(int i = 0; i < strArray.length; i++)
                {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }
                adjacencyList.add(intArray);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return convertListToArray(adjacencyList);
    }

    private static void saveResults(int startVertex, int endVertex, int weight, int[] parents) throws IOException {
        System.out.printf("\nStart, End: %s, %s\nPath: ",startVertex, endVertex);
        List<Integer> path = getPath(endVertex, parents, new ArrayList<>());
        System.out.println(path);
        System.out.println("Weight: " + weight);

        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File filepath = new File(jarFile.getParentFile(), startVertex+"-"+endVertex+".txt");
        System.out.println(filepath);
        Path outputFilename = Path.of(filepath.toURI());
        try
        {
            String fileContents = startVertex +", "+ endVertex+"\n";
            for (int i = 0; i < path.size(); i++) {
                if (i == path.size() - 1) fileContents += path.get(i)+"\n";
                else fileContents += path.get(i)+", ";
            }
            fileContents += weight;
            Files.writeString(outputFilename,fileContents);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}