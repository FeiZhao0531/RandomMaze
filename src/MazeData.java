import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class MazeData {

    public static final char WALL = '#';
    public static final char ROAD = ' ';

    private int mazeWidth, mazeHeight;
    private int entryX, entryY, exitX, exitY;

    private char[][] mazeMatrix;
    public boolean[][] visited;
    public boolean[][] path;


    public MazeData( String inputFileName) {

        if( inputFileName == null)
            throw new IllegalArgumentException("File can not be empty!");

        Scanner scanner = null;
        try{
            File file = new File( inputFileName);
            if( !file.exists())
                throw new IllegalArgumentException( "File" + inputFileName + " doesn't exist");

            FileInputStream fIS = new FileInputStream( file);
            scanner = new Scanner( new BufferedInputStream( fIS), "UTF-8");

            String whLine = scanner.nextLine();
            String[] wh = whLine.trim().split("\\s+");

            mazeHeight = Integer.parseInt( wh[0]);
            mazeWidth = Integer.parseInt( wh[1]);

            mazeMatrix = new char[ mazeHeight][ mazeWidth];
            visited = new boolean[ mazeHeight][ mazeWidth];
            path = new boolean[ mazeHeight][ mazeWidth];
            for( int i=0; i<mazeHeight; ++i) {

                String line = scanner.nextLine();
                if( line.length() != mazeWidth)
                    throw new IllegalArgumentException(" Maze file " + inputFileName + "is invalid..");

                for( int j=0; j<mazeWidth; ++j) {
                    mazeMatrix[i][j] = line.charAt(j);
                    visited[i][j] = false;
                    path[i][j] = false;
                }
            }
        }
        catch ( IOException e) {
            e.printStackTrace();
        }
        finally {
            if ( scanner != null)
                scanner.close();
        }

        entryX = 1;
        entryY = 0;
        exitX = mazeHeight - 2;
        exitY = mazeWidth - 1;
    }

    public int getMazeWidth() { return mazeWidth;}
    public int getMazeHeight() { return mazeHeight;}
    public int getMazeEntryX() { return entryX;}
    public int getMazeEntryY() { return entryY;}
    public int getMazeExitX() { return exitX;}
    public int getMazeExitY() { return exitY;}
    public char getMaze( int i, int j) {

        if( !inArea( i, j))
            throw new IllegalArgumentException(" i or j is out of Maze border...");
        return mazeMatrix[i][j];
    }

    public boolean inArea( int x, int y) {

        return ( x >= 0 && x < mazeWidth) && ( y >= 0 && y < mazeHeight);
    }

    public void print() {
        /// helper function for test in Terminal

        System.out.println( mazeWidth + " " + mazeHeight);

        for( int i=0; i<mazeHeight; ++i) {
            for( int j=0; j<mazeWidth; ++j)
                System.out.print( mazeMatrix[i][j]);
            System.out.println();
        }
        return;
    }

    /// this main function is just for test
    public static void main( String[] args) {

        String mazeFile = "maze_101_101.txt";
        MazeData data = new MazeData(mazeFile);
        data.print();
    }
}
