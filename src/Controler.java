import java.awt.*;
import java.awt.event.*;

public class Controler {

    private static int blockSide = 6;
    private static int holdOn = 10;
    private static final int nextStep[][] = { { 0, -1}, { 1, 0}, { 0, 1}, { -1, 0}};

    private MazeData data;
    private MazeFrame frame;

    public Controler( String mazeInputFile) {

        ///初始化数据
        data = new MazeData( mazeInputFile);
        int sceneWidth = data.getMazeWidth() * blockSide;
        int sceneHeight = data.getMazeHeight() * blockSide;

        ///初始化视图
        EventQueue.invokeLater( ()->{
            /// Event Queue ( import java.awt.*) for safe thread dispatch (事件分发线程-Java官方建议创建窗口时使用)

            frame = new MazeFrame("Maze Solution", sceneWidth, sceneHeight);
            //frame.addKeyListener( new AlgoKeyListener());
            //frame.addMouseListener( new AlgoMouseListener());

            new Thread(()->{
                run();
            }).start();
        });
    }

    private void run() {

        setData( -1, -1, false);
        if( !go( data.getMazeEntryX(), data.getMazeEntryY()))
            System.out.println("No Solution!");

        setData( -1, -1, false);
    }

    private boolean go( int x, int y) {

        if( !data.inArea( x, y))
            throw new IllegalArgumentException( "Index Out of Maze Range...");

        data.visited[x][y] = true;
        setData( x, y, true);
        if( x == data.getMazeExitX() && y == data.getMazeExitY())
            return true;

        for( int i=0; i<4; ++i) {

            int newX = x + nextStep[i][0];
            int newY = y + nextStep[i][1];
            if( data.inArea( newX, newY)
                    && data.getMaze( newX, newY) == data.ROAD
                    && !data.visited[newX][newY])
                if( go( newX, newY))
                    return true;
        }
        setData( x, y, false);

        return false;
    }

    private void setData( int x, int y, boolean isPath) {

        if( data.inArea( x, y))
            data.path[x][y] = isPath;

        frame.render( data);
        VisibleHelper.pause( holdOn);
    }

/*    private class AlgoKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent event) {

            if( event.getKeyChar() == ' ')
                isAnimated = !isAnimated;
        }
    }*/

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

        String inputMazeFile = "maze_101_101.txt";
        Controler ctrl = new Controler( inputMazeFile);
    }
}
