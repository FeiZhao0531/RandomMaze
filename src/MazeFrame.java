import javax.swing.*;
import java.awt.*;

public class MazeFrame extends JFrame {

    private int canvasWidth;
    private int canvasHeight;

    public int getCanvasWidth() { return canvasWidth;};
    public int getCanvasHeight() { return canvasHeight;};

    /// change to corresponding data
    private MazeData data;

    public void render( MazeData data) {

        this.data = data;
        repaint(); /// JPanel: this.repaint() 刷新JPanel里的所有控件
    }

    public MazeFrame( String title) {
        this( title, 1024, 768);
    }

    public MazeFrame( String title, int canvasWidth, int canvasHeight) { /// 窗口

        super( title);

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        MazeCanvas canvas = new MazeCanvas();
        canvas.setBackground(Color.white);
        //canvas.setPreferredSize( new Dimension( canvasWidth, canvasHeight));
        setContentPane( canvas); /// Fill frame with canvas
        pack(); /// automatically adjust frame to the canvas size

        setResizable( false); /// this.setResizable() ( could omit this)
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
            ///stop program by clicking the button "X" on the window
        setVisible( true);
    }

    private class MazeCanvas extends JPanel {

        public MazeCanvas() {
            super(true); /// 双缓存 ( default : true)
        }

        @Override /// 覆盖JPanel里的paintComponent
        public void paintComponent( Graphics g) { /// Graphics g - 绘制的上下文环境
            /// Drawing operation is done by this function
            super.paintComponent( g);

            Graphics2D g2d = ( Graphics2D)g;

            /// anti-aliased ( 抗锯齿)
            RenderingHints hints = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            /// Drawing steps ( mainly change this part)
            int w = canvasWidth / data.getMazeWidth();
            int h = canvasHeight / data.getMazeHeight();

            for( int i=0; i<data.getMazeHeight(); ++i) {
                for( int j=0; j<data.getMazeWidth(); ++j) {

                    if( data.getMaze( i, j) == MazeData.WALL)
                        VisibleHelper.setColor( g2d, VisibleHelper.Purple);
                    else
                        VisibleHelper.setColor( g2d, VisibleHelper.White);

                    if( data.path[i][j] == true)
                        VisibleHelper.setColor( g2d, VisibleHelper.LightBlue);

                    VisibleHelper.fillRectangle( g2d, j*w, i*h, w, h);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            /// automatically set the size of canvas
            return new Dimension( canvasWidth, canvasHeight);
        }
    }
}
