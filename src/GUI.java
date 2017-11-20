import  java.awt.*;
import  java.awt.geom.*;
import  java.awt.event.*;
import  java.io.*;
import  javax.swing.*;
import  javax.swing.event.*;
import  java.awt.image.*;
import  javax.imageio.*;
import  javax.swing.filechooser.FileFilter;
import java.util.Arrays;
public class GUI {
    int  rezhim=100;
    boolean loading=false;
    int  xPad;
    int  xf;
    int  yf;
    int  yPad;
    int  thickness;
    boolean pressed=false;

    int x,y,wx,wy; //для кадрирования
    int n;

    MyFrame f;
    Color maincolor;

    MyPanel japan;

    BufferedImage imag;
    private String fileName;

    // если мы загружаем картинку

    JColorChooser tcc;
    JButton colorbutton;
    private int     height;             // высота изображения
    private int     width;              // ширина изображения
    private int     staticHeight;             // высота изображения
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
        JMenu change = new JMenu("Правка");
        menuBar.add(change);

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
                        f.setSize(width+40, width+80);
                        japan.setSize(imag.getWidth(), imag.getWidth());
                        japan.repaint();
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


                JButton redbutton = new  JButton();
                redbutton.setBackground(Color.RED);
                redbutton.setBounds(10, 50, 15, 15);
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
                orangebutton.setBounds(10, 90, 15, 15);
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
                yellowbutton.setBounds(10, 130, 15, 15);
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
                greenbutton.setBounds(10, 170, 15, 15);
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
                bluebutton.setBounds(10, 210, 15, 15);
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
                cyanbutton.setBounds(10, 250, 15, 15);
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
                magentabutton.setBounds(10, 290, 15, 15);
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
                whitebutton.setBounds(10, 330, 15, 15);
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
                blackbutton.setBounds(10, 370, 15, 15);
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
        JMenuItem drawMenu = new JMenuItem(drawing);
        draw.add(drawMenu);



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

        Action greenAction = new AbstractAction("Зеленый -") {
            public void actionPerformed(ActionEvent e) {
                lastPixels = copyFromBufferedImage(imag);
                pixels = copyFromBufferedImage(imag);
                for (int i = 0; i < height; i++)
                    for (int j = 0; j < width; j++) {
                        int newGreen =  getGreen(pixels[i * width + j]) + (-100)/*delta(изменяемое для зеленого)*/;
                        if (newGreen > 255) newGreen=255;  // Отсекаем при превышении границ байта
                        if (newGreen < 0)   newGreen=0;
                        // В итоговом пикселе R и B цвета оставляем без изменений: & 0xFF00FF
                        // Полученный новый G (зеленый) засунем в "серединку" RGB: | (newGreen << 8)
                        pixels[i * width + j] = pixels[i * width + j] & 0xFF00FF | (newGreen << 8);
                    }
                imag = copyToBufferedImage(pixels);
                japan.repaint();
            }
        };
        JMenuItem greenMenu = new JMenuItem(greenAction);
        colorСorrection.add(greenMenu);

        japan = new  MyPanel();
        japan.setBounds(50,50,f.getWidth()-100,f.getHeight()-150);
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
/*
        //перетаскивание

        f.addComponentListener(new  ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // если делаем загрузку, то изменение размеров формы
                // отрабатываем в коде загрузки
                if(loading==false)
                {
                    japan.setSize(f.getWidth()-40, f.getHeight()-80);
                    BufferedImage tempImage = new  BufferedImage(japan.getWidth(), japan.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D d2 = (Graphics2D) tempImage.createGraphics();
                    d2.setColor(Color.white);
                    d2.fillRect(0, 0, japan.getWidth(), japan.getHeight());
                    tempImage.setData(imag.getRaster());
                    imag=tempImage;
                    japan.repaint();
                }
                loading=false;
            }
        });
*/
        f.setLayout(null);
        f.setVisible(true);
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
