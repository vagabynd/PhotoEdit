import  java.awt.*;
import  java.awt.geom.*;
import  java.awt.event.*;
import  java.io.*;
import  javax.swing.*;
import javax.swing.border.Border;
import  javax.swing.event.*;
import  java.awt.image.*;
import  javax.imageio.*;
import  javax.swing.filechooser.FileFilter;
import java.util.Arrays;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static org.opencv.core.CvType.CV_8UC;

public class GUI {
    int  rezhim=100;
    boolean loading=false;
    int  xPad;
    int  xf;
    int  yf;
    int  yPad;
    int faceX, faceY;
    int  thickness;
    boolean pressed=false;
    boolean maska = false;
    int del; //для яркости
    int x,y,wx,wy; //для кадрирования
    int n;
    int delta; //для rgb
    MyFrame f;
    Color maincolor;

    MyPanel japan;

    BufferedImage imag, cowboy;
    private String fileName;

    // если мы загружаем картинку

    JColorChooser tcc;
    JButton colorbutton;
    private int     height;             // высота изображения
    private int     width;              // ширина изображения
    private int     staticHeight;
    private int     staticWidth;
    private int[]   pixels;             // собственно массив цветов точек составляющих изображение
    private int[]   staticPixels;       // массив пикселей для отмены рисования
    private int[]   lastPixels;

    public int getPixel(int x, int y)   { return pixels[y*width+x]; }   // получить пиксель в формате RGB
    public int getRed(int color)        { return color >> 16; }         // получить красную составляющую цвета
    public int getGreen(int color)      { return (color >> 8) & 0xFF; } // получить зеленую составляющую цвета
    public int getBlue(int color)       { return color  & 0xFF;}        // получить синюю   составляющую цвета

    public GUI() {

        f = new MyFrame("Графический редактор");
        f.setSize(800,800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        maincolor=Color.black;

        JMenuBar menuBar = new  JMenuBar();

        f.setJMenuBar(menuBar);
        menuBar.setBounds(0,0,350,30);
        JMenu fileMenu = new  JMenu("Файл");
        menuBar.add(fileMenu);

        JMenu colorСorrection = new JMenu("Коррекция");
        menuBar.add(colorСorrection);

        JMenu draw = new JMenu("Рисование");
        menuBar.add(draw);
        JMenu masks = new JMenu("Маски");
        menuBar.add(masks);
        JMenu change = new JMenu("Правка");
        menuBar.add(change);
        JMenu help = new JMenu("Справка");
        menuBar.add(help);

        Action helpAction = new  AbstractAction("Помощь")
        {
            public void actionPerformed(ActionEvent event)
            {
                JOptionPane.showMessageDialog(f, "Увы, но я вам не помогу. Чтобы закрыть это окно нажмите ОК");

            }
        };
        JMenuItem helpMenu = new  JMenuItem(helpAction);
        help.add(helpMenu);

        Action aboutAction = new  AbstractAction("О программе")
        {
            public void actionPerformed(ActionEvent event)
            {
                JOptionPane.showMessageDialog(f, "Хорошая программа");
            }
        };
        JMenuItem aboutMenu = new  JMenuItem(aboutAction);
        help.add(aboutMenu);



        Action searchFaceAction = new  AbstractAction("Ковбой")
        {
            public void actionPerformed(ActionEvent event)
            {
                maska = true;
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                Mat image = bufferedImageToMat(imag);
                MatOfRect faceDetections = new MatOfRect();
                CascadeClassifier faceDetector = new CascadeClassifier("lib/lbpcascade_frontalface.xml");
                faceDetector.detectMultiScale(image, faceDetections, 1.05, 4, 10, new Size(20,20), new Size(500,500));
                System.out.println("Num of faces detected: " + faceDetections.toArray().length);
                for (Rect rect : faceDetections.toArray()) {

                    System.out.println(rect.x); //начало прямоугольника лица
                    System.out.println(rect.y);
                    faceX = rect.x;
                    faceY = rect.y;
                    //Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                      //      new Scalar(250, 100, 100), 2);
                }

                imag = convertMatToImage(image);



                //JFileChooser jf= new  JFileChooser();
                //int  result = jf.showOpenDialog(null);
                //if(result==JFileChooser.APPROVE_OPTION)
                //{
                    try
                    {
                        // при выборе изображения подстраиваем размеры формы
                        // и панели под размеры данного изображения
                        fileName = "image/cowboy.png";
                        File iF= new  File(fileName);
                        //jf.addChoosableFileFilter(new TextFileFilter(".png"));
                        //jf.addChoosableFileFilter(new TextFileFilter(".jpg"));
                        cowboy = ImageIO.read(iF);

                    }
                    catch (Exception ex) {

                    }
               // }







                japan.repaint();
            }
        };
        JMenuItem searchFaceMenu = new  JMenuItem(searchFaceAction);
        masks.add(searchFaceMenu);






        Action loadAction = new  AbstractAction("Загрузить")
        {
            public void actionPerformed(ActionEvent event)
            {
                JFileChooser jf= new  JFileChooser();
                int  result = jf.showOpenDialog(null);
                if(result==JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        // при выборе изображения подстраиваем размеры формы
                        // и панели под размеры данного изображения
                        fileName = jf.getSelectedFile().getAbsolutePath();
                        File iF= new  File(fileName);
                        jf.addChoosableFileFilter(new TextFileFilter(".png"));
                        jf.addChoosableFileFilter(new TextFileFilter(".jpg"));
                        imag = ImageIO.read(iF);

                        height = imag.getHeight();
                        width = imag.getWidth();
                        staticPixels = copyFromBufferedImage(imag);
                        staticHeight = height;
                        staticWidth = width;
                        loading=true;
                        f.setSize(width+80, height+120);
                        japan.setSize(imag.getWidth(), imag.getHeight());
                        japan.repaint();
                        f.repaint();
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(f, "Такого файла не существует");

                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(f, "Исключение ввода-вывода");
                    }
                    catch (Exception ex) {
                    }
                }
            }
        };
        JMenuItem loadMenu = new  JMenuItem(loadAction);
        fileMenu.add(loadMenu);

        Action drawing = new AbstractAction("Инструменты") {
            public void actionPerformed(ActionEvent e) {
                JButton colbutton = new  JButton(new ImageIcon("image/colorbotle 15.40.12.png"));
                //colorbutton.setBackground(maincolor);
                colbutton.setBounds(20, 10, 30, 30);
                colbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        ColorDialog coldi = new  ColorDialog(f,"Выбор цвета");
                        coldi.setVisible(true);
                    }
                });
                f.add(colbutton);


                JButton penbutton = new  JButton(new  ImageIcon("image/pen.png"));
                penbutton.setBounds(100,10,30,30);
                penbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=0;
                    }
                });
                f.add(penbutton);

                JButton brushbutton = new  JButton(new  ImageIcon("image/brush.png"));
                brushbutton.setBounds(200,10,30,30);
                brushbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=1;
                    }
                });
                f.add(brushbutton);

                JButton lasticbutton = new JButton(new  ImageIcon("image/lastic.png"));
                lasticbutton.setBounds(300,10,30,30);
                lasticbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=2;
                    }
                });
                f.add(lasticbutton);

                JButton textbutton = new  JButton(new  ImageIcon("image/text.png"));
                textbutton.setBounds(400,10,30,30);
                textbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=3;
                    }
                });
                f.add(textbutton);

                JButton linebutton = new  JButton(new  ImageIcon("image/line.png"));
                linebutton.setBounds(500,10,30,30);
                linebutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=4;
                    }
                });
                f.add(linebutton);

                JButton elipsbutton = new  JButton(new  ImageIcon("image/elips.png"));
                elipsbutton.setBounds(600,10,30,30);
                elipsbutton.addActionListener(new  ActionListener(){
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=5;
                    }
                });
                f.add(elipsbutton);

                JButton rectbutton = new  JButton(new  ImageIcon("image/rect.png"));
                rectbutton.setBounds(700,10,30,30);
                rectbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        rezhim=6;

                    }
                });
                f.add(rectbutton);
                f.repaint();
            }
        };
        JMenuItem drawMenu = new JMenuItem(drawing);
        draw.add(drawMenu);

        Action colorAction = new AbstractAction("Цвета") {
            public void actionPerformed(ActionEvent e) {

                JButton redbutton = new  JButton();
                redbutton.setBackground(Color.RED);
                redbutton.setBounds(10, 60, 20, 20);
                redbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.red;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(redbutton);

                JButton orangebutton = new  JButton();
                orangebutton.setBackground(Color.orange);
                orangebutton.setBounds(10, 100, 20, 20);
                orangebutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.orange;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(orangebutton);

                JButton yellowbutton = new  JButton();
                yellowbutton.setBackground(Color.YELLOW);
                yellowbutton.setBounds(10, 140, 20, 20);
                yellowbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.yellow;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(yellowbutton);

                JButton greenbutton = new  JButton();
                greenbutton.setBackground(Color.green);
                greenbutton.setBounds(10, 180, 20, 20);
                greenbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.green;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(greenbutton);

                JButton bluebutton = new JButton();
                bluebutton.setBackground(Color.blue);
                bluebutton.setBounds(10, 220, 20, 20);
                bluebutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.blue;
                        colorbutton.setBackground(maincolor);
                    }
                });

                f.add(bluebutton);

                JButton cyanbutton = new  JButton();
                cyanbutton.setBackground(Color.cyan);
                cyanbutton.setBounds(10, 260, 20, 20);
                cyanbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.cyan;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(cyanbutton);

                JButton magentabutton = new  JButton();
                magentabutton.setBackground(Color.magenta);
                magentabutton.setBounds(10, 300, 20, 20);
                magentabutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.magenta;
                        colorbutton.setBackground(maincolor);
                    }
                });

                f.add(magentabutton);

                JButton whitebutton = new  JButton();
                whitebutton.setBackground(Color.white);
                whitebutton.setBounds(10, 340, 20, 20);
                whitebutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.white;
                        colorbutton.setBackground(maincolor);
                    }
                });

                f.add(whitebutton);

                JButton blackbutton = new  JButton();
                blackbutton.setBackground(Color.black);
                blackbutton.setBounds(10, 380, 20, 20);
                blackbutton.addActionListener(new  ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        maincolor = Color.black;
                        colorbutton.setBackground(maincolor);
                    }
                });
                f.add(blackbutton);
                f.repaint();
            }

        };

        JMenuItem colorMenu = new JMenuItem(colorAction);
        draw.add(colorMenu);



        Action changeDrawAction = new  AbstractAction("Шаг назад")
        {
            public void actionPerformed(ActionEvent event)
            {
                imag = copyToBufferedImage(lastPixels);
                japan.repaint();
            }
        };
        JMenuItem changeDrawMenu = new  JMenuItem(changeDrawAction);
        change.add(changeDrawMenu);

        Action delCadrAction = new  AbstractAction("Отменить всё")
        {
            public void actionPerformed(ActionEvent event)
            {
                BufferedImage bi = new BufferedImage(staticWidth, staticHeight, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < staticHeight; i++)
                    for (int j = 0; j < staticWidth; j++)
                        bi.setRGB(j, i, staticPixels[i*staticWidth +j]);
                imag = bi;
                width = imag.getWidth();
                height = imag.getHeight();
                japan.repaint();
            }
        };
        JMenuItem delCadrMenu = new  JMenuItem(delCadrAction);
        change.add(delCadrMenu);



        Action saveAction = new  AbstractAction("Сохранить")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    JFileChooser jf= new  JFileChooser();
                    // Создаем фильтры  файлов
                    TextFileFilter pngFilter = new TextFileFilter(".png");
                    TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                    if(fileName==null)
                    {
                        // Добавляем фильтры
                        jf.addChoosableFileFilter(pngFilter);
                        jf.addChoosableFileFilter(jpgFilter);
                        int  result = jf.showSaveDialog(null);
                        if(result==JFileChooser.APPROVE_OPTION)
                        {
                            fileName = jf.getSelectedFile().getAbsolutePath();
                        }
                    }
                    // Смотрим какой фильтр выбран
                    if(jf.getFileFilter()==pngFilter)
                    {
                        ImageIO.write(imag, "png", new  File(fileName+".png"));
                    }
                    else
                    {
                        ImageIO.write(imag, "jpeg", new  File(fileName+".jpg"));
                    }
                }
                catch(IOException ex)
                {
                    JOptionPane.showMessageDialog(f, "Ошибка ввода-вывода");
                }
            }
        };
        JMenuItem saveMenu = new  JMenuItem(saveAction);
        fileMenu.add(saveMenu);

        Action saveasAction = new  AbstractAction("Сохранить как...")
        {
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    JFileChooser jf= new  JFileChooser();
                    // Создаем фильтры для файлов
                    TextFileFilter pngFilter = new  TextFileFilter(".png");
                    TextFileFilter jpgFilter = new  TextFileFilter(".jpg");
                    // Добавляем фильтры
                    jf.addChoosableFileFilter(pngFilter);
                    jf.addChoosableFileFilter(jpgFilter);
                    int  result = jf.showSaveDialog(null);
                    if(result==JFileChooser.APPROVE_OPTION)
                    {
                        fileName = jf.getSelectedFile().getAbsolutePath();
                    }
                    // Смотрим какой фильтр выбран
                    if(jf.getFileFilter()==pngFilter)
                    {
                        ImageIO.write(imag, "png", new  File(fileName+".png"));
                    }
                    else
                    {
                        ImageIO.write(imag, "jpeg", new  File(fileName+".jpg"));
                    }
                }
                catch(IOException ex)
                {
                    JOptionPane.showMessageDialog(f, "Ошибка ввода-вывода");
                }
            }
        };

        JMenuItem saveasMenu = new  JMenuItem(saveasAction);
        fileMenu.add(saveasMenu);


        Action brightAction = new AbstractAction("Яркость") {
            public void actionPerformed(ActionEvent e) {

                    JSlider slider = new JSlider(-12,12,0);
                    JFrame frame = new JFrame();
                    frame.setTitle("Яркость");
                    frame.setBounds(70,150,250,100);
                    JPanel contents = new JPanel();
                    contents.add(slider);
                    frame.add(contents);
                    frame.setVisible(true);
                    slider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            JSlider slider = (JSlider)e.getSource();
                            del = slider.getValue();
                            //System.out.print(del);
                            pixels = copyFromBufferedImage(imag);
                            //System.out.println(pixels[1]);
                            for(int i = 0; i < height; i++)
                                for (int j = 0; j < width; j++){
                                    int R;
                                    int G;
                                    int B;
                                    R = (getRed(pixels[i * width + j]) + del);
                                    //System.out.println(R);

                                    G = (getGreen(pixels[i * width + j]) + del);
                                    //System.out.println(G);
                                    B = (getBlue(pixels[i * width + j]) + del);
                                    //System.out.println(B);
                                    //контролируем переполнение переменных
                                    if (R < 0) R = 0;
                                    if (R > 255) R = 255;
                                    if (G < 0) G = 0;
                                    if (G > 255) G = 255;
                                    if (B < 0) B = 0;
                                    if (B > 255) B = 255;

                                    pixels[i * width + j] = (R << 16) | (G << 8) | (B);
                                }
                            imag = copyToBufferedImage(pixels);
                            japan.repaint();
                        }
                    });
                }
        };

        JMenuItem brightMenu = new JMenuItem(brightAction);
        colorСorrection.add(brightMenu);


        Action contrastAction = new AbstractAction("Контрастность") {
            public void actionPerformed(ActionEvent e) {

                JSlider slider = new JSlider(-12,12,0);
                JFrame frame = new JFrame();
                frame.setTitle("Контрастность");
                frame.setBounds(70,150,250,100);
                JPanel contents = new JPanel();
                contents.add(slider);
                frame.add(contents);
                frame.setVisible(true);
                slider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        JSlider slider = (JSlider)e.getSource();
                        del = slider.getValue();
                        //System.out.print(del);
                        pixels = copyFromBufferedImage(imag);
//                            System.out.println(pixels[1]);
                        for(int i = 0; i < height; i++)
                            for (int j = 0; j < width; j++){
                                int R;
                                int G;
                                int B;
                                if (del >= 0)
                                {
                                    if (del == 100) del = 99;
                                    R = ((getRed(pixels[i * width + j]) * 100 - 128 * del) / (100 - del));
                                    G = ((getGreen(pixels[i * width + j]) * 100 - 128 * del) / (100 - del));
                                    B = ((getBlue(pixels[i * width + j]) * 100 - 128 * del) / (100 - del));
                                }
                                else
                                {
                                    R = ((getRed(pixels[i * width + j]) * (100 - (-del)) + 128 * (-del)) / 100);
                                    G = ((getGreen(pixels[i * width + j]) * (100 - (-del)) + 128 * (-del)) / 100);
                                    B = ((getBlue(pixels[i * width + j]) * (100 - (-del)) + 128 * (-del)) / 100);
                                }
                                //контролируем переполнение переменных
                                if (R < 0) R = 0;
                                if (R > 255) R = 255;
                                if (G < 0) G = 0;
                                if (G > 255) G = 255;
                                if (B < 0) B = 0;
                                if (B > 255) B = 255;

                                pixels[i * width + j] = (R << 16) | (G << 8) | (B);
                            }
                        imag = copyToBufferedImage(pixels);
                        japan.repaint();
                    }
                });
            }
        };
        JMenuItem contrastMenu = new JMenuItem(contrastAction);
        colorСorrection.add(contrastMenu);



        Action cutAction = new AbstractAction("Кадрирование") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                japan.addMouseListener(new MouseAdapter() {
                     public void mousePressed(MouseEvent e) {
                         if (e.getButton() == MouseEvent.BUTTON3) {
                             x = e.getX();
                             y = e.getY();
                         }
                                           }
                     public void mouseReleased(MouseEvent e) {
                         if (e.getButton() == MouseEvent.BUTTON3) {

                             wx = e.getX();
                             wy = e.getY();
                             int w = wx - x;
                             int h = y - wy;
                             imag = imag.getSubimage(x, y, Math.abs(w), Math.abs(h));
                             width = imag.getWidth();
                             height = imag.getHeight();
                         }
                     }
                });
                japan.repaint();
            }

        };

        JMenuItem cutMenu = new JMenuItem(cutAction);
        colorСorrection.add(cutMenu);




        Action bwAction = new AbstractAction("Черно-белое") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                pixels = copyFromBufferedImage(imag);
                for (int i = 0; i < height; i++)
                    for (int j = 0; j < width; j++) {
                        // находим среднюю арифметическую интенсивность пикселя по всем цветам
                        int intens = (getRed(pixels[i * width + j]) +
                                getGreen(pixels[i * width + j]) +
                                getBlue(pixels[i * width + j])) / 3;
                        // ... и записываем ее в каждый цвет за раз , сдвигая байты RGB на свои места
                        pixels[i * width + j] = intens + (intens << 8) + (intens << 16);
                    }
                imag = copyToBufferedImage(pixels);
                japan.repaint();
            }
        };
        JMenuItem bwMenu = new JMenuItem(bwAction);
        colorСorrection.add(bwMenu);

        Action negativeAction = new AbstractAction("Негатив") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                pixels = copyFromBufferedImage(imag);
                for (int i = 0; i < height; i++)
                    for (int j = 0; j < width; j++)
                        // Применяем логическое отрицание и отбрасываем старший байт
                        pixels[i*width + j] = ~pixels[i*width + j] & 0xFFFFFF;
                imag = copyToBufferedImage(pixels);
                japan.repaint();
            }
        };
        JMenuItem negativeMenu = new JMenuItem(negativeAction);
        colorСorrection.add(negativeMenu);

        Action sharpnessAction = new AbstractAction("Резкость +") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                pixels = copyFromBufferedImage(imag);
                // Чтобы работать с неизмененными данными скопируем в новый массив
                int[] arrnew= Arrays.copyOf(pixels, width*height);

                for (int j = 1; j < height-1; j++)
                    for (int i = 1; i < width-1; i++) {
                        // покомпонентно применяем фильтр усиления резкости
                        //  -0.1 -0.1 -0.1
                        //  -0.1  1.8 -0.1
                        //  -0.1 -0.1 -0.1
                        int newRed=getRed(getPixel(i,j))*18/10 -
                                (getRed(getPixel(i-1,j-1)) + getRed(getPixel(i-1,j)) + getRed(getPixel(i-1,j+1)) +
                                        getRed(getPixel(i,j-1))   + getRed(getPixel(i,j+1)) +
                                        getRed(getPixel(i+1,j-1)) + getRed(getPixel(i+1,j)) + getRed(getPixel(i+1,j+1)))/10;
                        if (newRed > 255) newRed=255;  // Отсекаем при превышении границ байта
                        if (newRed < 0)   newRed=0;

                        int newGreen=getGreen(getPixel(i,j))*18/10 -
                                (getGreen(getPixel(i-1,j-1)) + getGreen(getPixel(i-1,j)) + getGreen(getPixel(i-1,j+1)) +
                                        getGreen(getPixel(i,j-1))   + getGreen(getPixel(i,j+1)) +
                                        getGreen(getPixel(i+1,j-1)) + getGreen(getPixel(i+1,j)) + getGreen(getPixel(i+1,j+1)))/10;
                        if (newGreen > 255) newGreen=255;  // Отсекаем при превышении границ байта
                        if (newGreen < 0)   newGreen=0;

                        int newBlue=getBlue(getPixel(i,j))*18/10 -
                                (getBlue(getPixel(i-1,j-1)) + getBlue(getPixel(i-1,j)) + getBlue(getPixel(i-1,j+1)) +
                                        getBlue(getPixel(i,j-1))   + getBlue(getPixel(i,j+1)) +
                                        getBlue(getPixel(i+1,j-1)) + getBlue(getPixel(i+1,j)) + getBlue(getPixel(i+1,j+1)))/10;
                        if (newBlue > 255) newBlue=255;  // Отсекаем при превышении границ байта
                        if (newBlue < 0)   newBlue=0;

                        arrnew[j * width + i] = newBlue + (newGreen << 8) + (newRed << 16);
                    }
                pixels = arrnew;
                imag = copyToBufferedImage(pixels);
                japan.repaint();
            }
        };
        JMenuItem sharpnessMenu = new JMenuItem(sharpnessAction);
        colorСorrection.add(sharpnessMenu);



        Action blurAction = new AbstractAction("Размытие +") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                pixels = copyFromBufferedImage(imag);
                // Чтобы работать с неизмененными данными скопируем в новый массив
                int[] arrnew= Arrays.copyOf(pixels, width*height);

                for (int j = 1; j < height-1; j++)
                    for (int i = 1; i < width-1; i++) {
                        // матрица свертки
                        //  0.2 0.2 0.2
                        //  0.2 0.2 0.2
                        //  0.2 0.2 0.2
                        int newRed=getRed(getPixel(i,j))*2/10 +
                                (getRed(getPixel(i-1,j-1)) + getRed(getPixel(i-1,j)) + getRed(getPixel(i-1,j+1)) +
                                        getRed(getPixel(i,j-1))   + getRed(getPixel(i,j+1)) +
                                        getRed(getPixel(i+1,j-1)) + getRed(getPixel(i+1,j)) + getRed(getPixel(i+1,j+1)))/10;
                        if (newRed > 255) newRed=255;  // Отсекаем при превышении границ байта
                        if (newRed < 0)   newRed=0;

                        int newGreen=getGreen(getPixel(i,j))*2/10 +
                                (getGreen(getPixel(i-1,j-1)) + getGreen(getPixel(i-1,j)) + getGreen(getPixel(i-1,j+1)) +
                                        getGreen(getPixel(i,j-1))   + getGreen(getPixel(i,j+1)) +
                                        getGreen(getPixel(i+1,j-1)) + getGreen(getPixel(i+1,j)) + getGreen(getPixel(i+1,j+1)))/10;
                        if (newGreen > 255) newGreen=255;  // Отсекаем при превышении границ байта
                        if (newGreen < 0)   newGreen=0;

                        int newBlue=getBlue(getPixel(i,j))*2/10 +
                                (getBlue(getPixel(i-1,j-1)) + getBlue(getPixel(i-1,j)) + getBlue(getPixel(i-1,j+1)) +
                                        getBlue(getPixel(i,j-1))   + getBlue(getPixel(i,j+1)) +
                                        getBlue(getPixel(i+1,j-1)) + getBlue(getPixel(i+1,j)) + getBlue(getPixel(i+1,j+1)))/10;
                        if (newBlue > 255) newBlue=255;  // Отсекаем при превышении границ байта
                        if (newBlue < 0)   newBlue=0;

                        arrnew[j * width + i] = newBlue + (newGreen << 8) + (newRed << 16);
                    }
                pixels = arrnew;
                imag = copyToBufferedImage(pixels);
                japan.repaint();
            }
        };
        JMenuItem blurMenu = new JMenuItem(blurAction);
        colorСorrection.add(blurMenu);





        Action greenAction = new AbstractAction("RGB") {
            public void actionPerformed(ActionEvent e) {
                JLabel red = new JLabel("Red");
                JLabel green = new JLabel("Green");
                JLabel blue = new JLabel("Blue");

                JSlider sliderr = new JSlider(-50,50,0);

                JSlider sliderg = new JSlider(-50,50,0);
                JSlider sliderb = new JSlider(-50,50,0);
                JFrame frame = new JFrame();
                frame.setTitle("Red Green Blue");
                frame.setBounds(70,150,250,100);
                JPanel contents = new JPanel();
                contents.setLayout(new GridLayout(3,2));
                contents.add(red);
                contents.add(sliderr);
                contents.add(green);
                contents.add(sliderg);
                contents.add(blue);
                contents.add(sliderb);
                frame.add(contents);
                frame.setVisible(true);
                sliderg.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        JSlider slider = (JSlider)e.getSource();
                        delta = slider.getValue();
                        lastPixels = copyFromBufferedImage(imag);
                        pixels = copyFromBufferedImage(imag);
                        for (int i = 0; i < height; i++)
                            for (int j = 0; j < width; j++) {
                                int newGreen =  getGreen(pixels[i * width + j]) + delta/*delta(изменяемое для зеленого)*/;
                                if (newGreen > 255) newGreen=255;  // Отсекаем при превышении границ байта
                                if (newGreen < 0)   newGreen=0;
                                // В итоговом пикселе R и B цвета оставляем без изменений: & 0xFF00FF
                                // Полученный новый G (зеленый) засунем в "серединку" RGB: | (newGreen << 8)
                                pixels[i * width + j] = pixels[i * width + j] & 0xFF00FF | (newGreen << 8);
                            }
                        imag = copyToBufferedImage(pixels);
                        japan.repaint();
                        delta = 0;
                    }
                });

                sliderr.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        JSlider slider = (JSlider)e.getSource();
                        delta = slider.getValue();
                        lastPixels = copyFromBufferedImage(imag);
                        pixels = copyFromBufferedImage(imag);
                        for (int i = 0; i < height; i++)
                            for (int j = 0; j < width; j++) {
                                int newRed =  getRed(pixels[i * width + j]) + delta/*delta(изменяемое для зеленого)*/;
                                if (newRed > 255) newRed=255;  // Отсекаем при превышении границ байта
                                if (newRed < 0)   newRed=0;
                                // В итоговом пикселе R и B цвета оставляем без изменений: & 0xFF00FF
                                // Полученный новый G (зеленый) засунем в "серединку" RGB: | (newGreen << 8)
                                pixels[i * width + j] = pixels[i * width + j] & 0x00FFFF | (newRed << 16);
                            }
                        imag = copyToBufferedImage(pixels);
                        japan.repaint();
                        delta = 0;
                    }
                });

                sliderb.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        JSlider slider = (JSlider)e.getSource();
                        delta = slider.getValue();
                        lastPixels = copyFromBufferedImage(imag);
                        pixels = copyFromBufferedImage(imag);
                        for (int i = 0; i < height; i++)
                            for (int j = 0; j < width; j++) {
                                int newBlue =  getBlue(pixels[i * width + j]) + delta/*delta(изменяемое для зеленого)*/;
                                if (newBlue > 255) newBlue=255;  // Отсекаем при превышении границ байта
                                if (newBlue < 0)   newBlue=0;
                                // В итоговом пикселе R и B цвета оставляем без изменений: & 0xFF00FF
                                // Полученный новый G (зеленый) засунем в "серединку" RGB: | (newGreen << 8)
                                pixels[i * width + j] = pixels[i * width + j] & 0xFFFF00 | (newBlue);
                            }
                        imag = copyToBufferedImage(pixels);
                        japan.repaint();
                        delta = 0;
                    }
                });

            }
        };
        JMenuItem greenMenu = new JMenuItem(greenAction);
        colorСorrection.add(greenMenu);

        japan = new  MyPanel();
        japan.setBounds(40,50,f.getWidth()-100,f.getHeight()-150);
        japan.setBackground(Color.white);
        japan.setOpaque(true);
        f.add(japan);



        tcc = new  JColorChooser(maincolor);
        tcc.getSelectionModel().addChangeListener(new  ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                maincolor = tcc.getColor();
                colorbutton.setBackground(maincolor);
            }
        });
        japan.addMouseMotionListener(new  MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent e)
            {
                if (pressed==true)
                {
                    Graphics g = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D)g;
                    // установка цвета
                    g2.setColor(maincolor);
                    switch (rezhim)
                    {
                        // карандаш
                        case 0:
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // кисть
                        case 1:
                            g2.setStroke(new  BasicStroke(3.0f));
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                        // ластик
                        case 2:
                            g2.setStroke(new  BasicStroke(3.0f));
                            g2.setColor(Color.WHITE);
                            g2.drawLine(xPad, yPad, e.getX(), e.getY());
                            break;
                    }
                    xPad=e.getX();
                    yPad=e.getY();
                }
                japan.repaint();
            }
        });
        japan.addMouseListener(new  MouseAdapter()
        {
            public void mouseClicked(MouseEvent e) {

                Graphics g = imag.getGraphics();
                Graphics2D g2 = (Graphics2D)g;
                // установка цвета
                g2.setColor(maincolor);
                switch (rezhim)
                {
                    // карандаш
                    case 0:
                        g2.drawLine(xPad, yPad, xPad+1, yPad+1);
                        break;
                    // кисть
                    case 1:
                        g2.setStroke(new  BasicStroke(3.0f));
                        g2.drawLine(xPad, yPad, xPad+1, yPad+1);
                        break;
                    // ластик
                    case 2:
                        g2.setStroke(new  BasicStroke(3.0f));
                        g2.setColor(Color.WHITE);
                        g2.drawLine(xPad, yPad, xPad+1, yPad+1);
                        break;
                    // текст
                    case 3:
                        // устанавливаем фокус для панели,
                        // чтобы печатать на ней текст
                        japan.requestFocus();
                        break;
                }
                xPad=e.getX();
                yPad=e.getY();

                pressed=true;
                japan.repaint();
            }
            public void mousePressed(MouseEvent e) {
                xPad=e.getX();
                yPad=e.getY();
                xf=e.getX();
                yf=e.getY();
                pressed=true;
            }
            public void mouseReleased(MouseEvent e) {

                Graphics g = imag.getGraphics();
                Graphics2D g2 = (Graphics2D)g;
                // установка цвета
                g2.setColor(maincolor);
                // Общие рассчеты для овала и прямоугольника
                int  x1=xf, x2=xPad, y1=yf, y2=yPad;
                if(xf>xPad)
                {
                    x2=xf; x1=xPad;
                }
                if(yf>yPad)
                {
                    y2=yf; y1=yPad;
                }
                switch(rezhim)
                {
                    // линия
                    case 4:
                        g.drawLine(xf, yf, e.getX(), e.getY());
                        break;
                    // круг
                    case 5:
                        g.drawOval(x1, y1, (x2-x1), (y2-y1));
                        break;
                    // прямоугольник
                    case 6:
                        g.drawRect(x1, y1, (x2-x1), (y2-y1));
                        break;
                }
                xf=0; yf=0;
                pressed=false;
                japan.repaint();
            }
        });
        japan.addKeyListener(new  KeyAdapter()
        {
            public void keyReleased(KeyEvent e)
            {
                // устанавливаем фокус для панели,
                // чтобы печатать на ней текст
                japan.requestFocus();
            }
            public void keyTyped(KeyEvent e)
            {
                if(rezhim==3){
                    Graphics g = imag.getGraphics();
                    Graphics2D g2 = (Graphics2D)g;
                    // установка цвета
                    g2.setColor(maincolor);
                    g2.setStroke(new  BasicStroke(2.0f));

                    String str = new  String("");
                    str+=e.getKeyChar();
                    g2.setFont(new  Font("Arial", 0, 15));
                    g2.drawString(str, xPad, yPad);
                    xPad+=10;
                    // устанавливаем фокус для панели,
                    // чтобы печатать на ней текст
                    japan.requestFocus();
                    japan.repaint();
                }
            }
        });

        //расширение
        f.addComponentListener(new  ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // если делаем загрузку, то изменение размеров формы
                // отрабатываем в коде загрузки
                if(loading==false)
                {
                    japan.setSize(f.getWidth()-80, f.getHeight()-120);
                    BufferedImage tempImage = new  BufferedImage(japan.getWidth(), japan.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D d2 = (Graphics2D) tempImage.createGraphics();
                    d2.setColor(Color.white);
                    d2.fillRect(0, 0, japan.getWidth(), japan.getHeight());
                    //tempImage.setData(imag.getRaster());
                    imag=tempImage;
                    japan.repaint();
                }
            }
        });

        f.setLayout(null);
        f.setVisible(true);
    }
    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
    private BufferedImage convertMatToImage(Mat mat) {

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] bytes = new byte[bufferSize];
        mat.get(0, 0, bytes);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
        return image;
    }
    private int[] copyFromBufferedImage(BufferedImage bi)  {
        int[] pict = new int[height*width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                pict[i*width + j] = bi.getRGB(j, i) & 0xFFFFFF; // 0xFFFFFF: записываем только 3 младших байта RGB
        return pict;
    }
    private BufferedImage copyToBufferedImage(int[] pixels1)  {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                bi.setRGB(j, i, pixels1[i*width +j]);
        return bi;
    }





    // Фильтр картинок
    class TextFileFilter extends FileFilter
    {
        private String ext;
        public TextFileFilter(String ext)
        {
            this.ext=ext;
        }
        public boolean accept(java.io.File file)
        {
            if (file.isDirectory()) return true;
            return (file.getName().endsWith(ext));
        }
        public String getDescription()
        {
            return "*"+ext;
        }
    }
    class MyFrame extends JFrame
    {
        public void paint(Graphics g)
        {
            super.paint(g);
        }
        public MyFrame(String title)
        {
            super(title);
        }
    }

    class MyPanel extends JPanel
    {
        public MyPanel()
        { }



        public void paintComponent (Graphics g)
        {

            if(imag==null)
            {
                imag = new  BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D d2 = (Graphics2D) imag.createGraphics();
                d2.setColor(Color.white);
                d2.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            super.paintComponent(g);
            g.drawImage(imag, 0, 0,this);
            if(maska==true) {
                g.drawImage(cowboy,faceX-75,faceY-250,this);
            }
        }
    }

    class ColorDialog extends JDialog
    {
        public ColorDialog(JFrame owner, String title)
        {
            super(owner, title, true);
            add(tcc);
            setSize(200, 200);
        }
    }

}
