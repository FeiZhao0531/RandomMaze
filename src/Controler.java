import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controler {

    private static int blockPixels = 6; /// number of pixels in every unit block
    private static int holdOn = 5; /// time of pause(ms) in every repaint
    private static final int nextStep[][] = { { 1, 0}, { 0, 1}, { -1, 0}, { 0, -1}};

    private MazeData data;
    private MazeFrame frame;

    public Controler( int mazeHeight, int mazeWidth, boolean isComplexMaze) {
        /// used for generating a random maze and solving it, by the way saving the maze in .txt file

        ///初始化数据
        data = new MazeData( mazeHeight, mazeWidth);
        int sceneHeight = data.getMazeHeight() * blockPixels;
        int sceneWidth = data.getMazeWidth() * blockPixels;

        ///初始化视图
        EventQueue.invokeLater( ()->{
            /// Event Queue ( import java.awt.*) for safe thread dispatch (事件分发线程-Java官方建议创建窗口时使用)

            frame = new MazeFrame("Random Maze", sceneWidth, sceneHeight);
            frame.addKeyListener( new AlgoKeyListener());
            //frame.addMouseListener( new AlgoMouseListener());

            new Thread(()->{
                run( isComplexMaze);
            }).start();
        });
    }

    public Controler( String mazeInputFile) {
        /// used for solving a known maze which saved in .txt file

        ///初始化数据
        data = new MazeData( mazeInputFile);
        int sceneWidth = data.getMazeWidth() * blockPixels;
        int sceneHeight = data.getMazeHeight() * blockPixels;

        ///初始化视图
        EventQueue.invokeLater( ()->{
            /// Event Queue ( import java.awt.*) for safe thread dispatch (事件分发线程-Java官方建议创建窗口时使用)

            frame = new MazeFrame("Maze Solution", sceneWidth, sceneHeight);
            //frame.addKeyListener( new AlgoKeyListener());
            //frame.addMouseListener( new AlgoMouseListener());

            new Thread(()->{
                mazeSolver();
            }).start();
        });
    }

    private void run( boolean isComplexMaze) {

        setRoadData( -1, -1);

        RandomQueue<Coordinate> q = new RandomQueue<>();
        Coordinate start = new Coordinate( data.getMazeEntryX(), data.getMazeEntryY() + 1);
        q.add( start);
        data.visited[ start.getX()][ start.getY()] = true;
        data.removeFog( start.getX(), start.getY());

        if( isComplexMaze == true) {

            System.out.println("Complex maze generator...");
            while( !q.empty()) {

                Coordinate coor = q.remove();

                int randomNextStepIndex[] = { 0, 1, 2, 3};
                for( int j=3; j>=1; --j) {
                    int tmpIndex = (int)(Math.random() * (j+1));
                    int tmpValue = randomNextStepIndex[ tmpIndex];
                    randomNextStepIndex[ tmpIndex] = randomNextStepIndex[j];
                    randomNextStepIndex[j] = tmpValue;
                }

                int i = 0;
                for( ; i<4; ++i) {

                    int newX = coor.getX() + 2*nextStep[randomNextStepIndex[i]][0];
                    int newY = coor.getY() + 2*nextStep[randomNextStepIndex[i]][1];

                    if( data.inArea( newX, newY) && !data.visited[ newX][ newY]) {
                        q.add( new Coordinate( newX, newY));
                        data.visited[ newX][ newY] = true;
                        data.removeFog( newX, newY);
                        setRoadData( coor.getX() + nextStep[randomNextStepIndex[i]][0],
                                coor.getY() + nextStep[randomNextStepIndex[i]][1]);
                        break;
                    }
                }
                if( i < 4)
                    q.add( new Coordinate( coor.getX(), coor.getY()));
            }
        }
        else {
            System.out.println("Simple maze generator...");

            while( !q.empty()) {

                Coordinate coor = q.remove();

                for( int i=0; i<4; ++i) {

                    int newX = coor.getX() + 2*nextStep[i][0];
                    int newY = coor.getY() + 2*nextStep[i][1];

                    if( data.inArea( newX, newY) && !data.visited[ newX][ newY]) {
                        q.add( new Coordinate( newX, newY));
                        data.visited[ newX][ newY] = true;
                        data.removeFog( newX, newY);
                        setRoadData( coor.getX() + nextStep[i][0],
                                coor.getY() + nextStep[i][1]);
                    }
                }
            }
        }

        setRoadData( -1, -1);

        writeFile( fileName( isComplexMaze));
    }

    private void setRoadData( int x, int y) {

        if( data.inArea( x, y))
            data.setMazeRoad( x, y);

        frame.render( data);
        VisibleHelper.pause( holdOn);
    }

    private void mazeSolver() {

        setPathData( -1, -1, false);
        if( !go( data.getMazeEntryX(), data.getMazeEntryY()))
            System.out.println("No Solution...");

        setPathData( -1, -1, false);
    }

    private boolean go( int x, int y) {

        if( !data.inArea( x, y))
            throw new IllegalArgumentException("Index is out of Maze border...");

        if( x == data.getMazeExitX() && y == data.getMazeExitY())
            return true;

        data.visited[x][y] = true;
        setPathData( x, y, true);

        for( int i=0; i<4; ++i) {

            int newX = x + nextStep[i][0];
            int newY = y + nextStep[i][1];

            if( data.inArea( newX, newY)
                && data.getMaze( newX, newY) == MazeData.ROAD
                && !data.visited[ newX][ newY])
            {
                if( go( newX, newY))
                    return true;
            }
        }

        setPathData( x, y, false);
        return false;
    }

    private void setPathData( int x, int y, boolean isPath) {

        if( data.inArea( x, y))
            data.path[x][y] = isPath;

        frame.render( data);
        VisibleHelper.pause( holdOn);
    }

    private String fileName( boolean isComplexMaze) {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateStr = simpleDateFormat.format( date);
        String fileName = data.getMazeHeight() + "_" + data.getMazeWidth() + "_" + dateStr + ".txt";
        if( isComplexMaze)
            fileName = "ComplexMaze_" + fileName;
        else {
            fileName = "SimpleMaze_" + fileName;
        }

        return fileName;
    }

    private void writeFile( String fileName) {

        try {
            File outFile = new File( fileName);
            outFile.createNewFile();
            try( FileWriter writer = new FileWriter( outFile);
                 BufferedWriter out = new BufferedWriter( writer)
            ) {
                out.write( data.getMazeHeight() + " ");
                out.write( data.getMazeWidth() + "\r\n");
                for( int i=0; i<data.getMazeHeight(); ++i) {
                    for( int j=0; j<data.getMazeWidth(); ++j) {
                        out.write( data.getMaze( i,j));
                    }
                    out.write("\r\n");
                }
                out.flush();
            }
        }
        catch ( IOException e) {
            e.printStackTrace();
        }
    }

    private class AlgoKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent event) {

            if( event.getKeyChar() == ' ') {
                for( int i=0; i<data.getMazeHeight(); ++i) {
                    for( int j=0; j<data.getMazeWidth(); ++j) {
                        data.visited[i][j] = false;
                    }
                }

                new Thread( ()->{
                    go( data.getMazeEntryX(), data.getMazeEntryY());
                }).start();
            }
        }
    }

/*    private class AlgoMouseListener extends MouseAdapter {

        @Override
        public void mousePressed( MouseEvent e) {

            e.translatePoint( -7, 7 - (frame.getBounds().height - frame.getCanvasHeight()));
            System.out.println( frame.getBounds().height);
            System.out.println( e.getPoint());

            for( Circle circle : circles) {
                if( circle.contain( e.getPoint()))
                    circle.isFilled = !circle.isFilled;
            }
        }
    }*/

    public static void main( String[] args) {

        int mazeHeight = 101, mazeWidth = 101;
        Controler ctrl = new Controler( mazeHeight, mazeWidth, true);

/*        String dateStr = "2019-07-10_15-59-50";
        String fileName = "ComplexMaze_" + mazeHeight + "_" + mazeWidth + "_" + dateStr + ".txt";
        Controler ctrl = new Controler( fileName);*/
    }
}
