import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;
import javax.swing.*;
// библиотека Gson позволяет легче производить чтение и запись json
import com.google.gson.Gson;

public class Panel extends JPanel implements ActionListener {
    // данные для панели
    private Dimension d;    // размеры
    private final Font smallFont = new Font("Arial", Font.BOLD, 14); // шрифт
    private Image heart, ghost;             // картинки призрака и жизне
    private Image up, down, left, right;    // картинки пакмена
    // Таймер для задержки
    private Timer timer;
    // создаем класс игры, в которой хранятся все данные игры
    private Game game;
    // созадем класс лидеров в который будем записывать лидеров
    private Leader[] leaders;
    // класс геймплея
    private GamePlay gamePlay;
    // конструктор класса Panel
    public Panel() {
        // иницилазируем классы
        game = new Game();
        leaders = new Leader[100];
        gamePlay = new GamePlay(game);
        // загружаем изображения
        loadImages();
        // инициализируем переменные
        initVariables();
        // запускаем чтение с ввода пользователя
        addKeyListener(new InputAdapter());
        // фокусировка на компонент Jpanel
        setFocusable(true);
        // иницилизируем игру
        initGame();
    }
    // загрузка изображений игры
    private void loadImages() {
        // при загрузке получаем абсолютный путь к проекту
        down = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\down.gif").getImage();
        up = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\up.gif").getImage();
        left = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\left.gif").getImage();
        right = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\right.gif").getImage();
        ghost = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\ghost.gif").getImage();
        heart = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\heart.png").getImage();
    }
    // инциализация переменных
    private void initVariables() {
        // инициализируем массив карты
        gamePlay.initVariables();
        // инициализируем размеры окна
        d = new Dimension(400, 400);
        // создаем таймер с отрисовкой с задержкой 40
        timer = new Timer(40, this);
        // запускаем таймер
        timer.start();
    }
    // функция игры
    private void playGame(Graphics2D g2d) throws IOException {
        // если игрок умер, то вызываем функцию death
        if (game.dying) {
            death();
            // если не умер, то..
        } else {
            // вызываем функцию изменения положения пакмена
            gamePlay.movePacman();
            // отрисовка пакмена
            drawPacman(g2d);
            // движение призакров
            moveGhosts(g2d);
            // проверка карты
            gamePlay.checkMaze();
        }
    }
    // начальная заставка
    private void showIntroScreen(Graphics2D g2d) {
        String start = "Нажмите пробел для начала игры";
        g2d.setColor(Color.RED);
        g2d.drawString(start, (game.SCREEN_SIZE) / 10, 150);
    }
    // отрисовка интерфейса снизу с данными игры
    private void drawScore(Graphics2D g) {
        // указываем шрифт
        g.setFont(smallFont);
        // указываем цвет вывода
        g.setColor(new Color(5, 181, 79));
        // записываем в буфер данные с очками
        String s = "Очки: " + game.score;
        // выводис строку с очками
        g.drawString(s, game.SCREEN_SIZE / 2 + 96, game.SCREEN_SIZE + 16);
        // рисуем в цикле картинки с жизнями
        for (int i = 0; i < game.lives; i++) {
            g.drawImage(heart, i * 28 + 8, game.SCREEN_SIZE + 1, this);
        }
        // если собрано пасхальное яйцо, выводи сообщение снизу на экране
        if (game.egpos == -1) {
            g.setColor(new Color(180, 181, 30));
            String egg = "П.Яйцо(+50 очков)";
            g.drawString(egg, 89, game.SCREEN_SIZE + 16);
        }
    }
    // функция смерти
    private void death() throws IOException {
        // отнимаем жизнь
        game.lives--;
        // если жизней уже нет
        if (game.lives == 0) {
            // игра завершена
            game.inGame = false;
            // ждем ввода имени пользователя для записи в лидерах
            String message = JOptionPane.showInputDialog(null, "Введите ваше имя");
            // записываем в лидеры(в файле)
            setLeader(message, game.score);
        }
        // продолжаем игру
        gamePlay.continueLevel();
    }
    // функция перемещения призраков
    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;
        // перебираем призраков
        for (int i = 0; i < game.N_GHOSTS; i++) {
            if (game.ghost_x[i] % game.BLOCK_SIZE == 0 && game.ghost_y[i] % game.BLOCK_SIZE == 0) {
                pos = game.ghost_x[i] / game.BLOCK_SIZE + game.N_BLOCKS * (int) (game.ghost_y[i] / game.BLOCK_SIZE);
                count = 0;
                // различные случайные двжиения  относительно позиции на карте
                if ((game.screenData[pos] & 1) == 0 && game.ghost_dx[i] != 1) {
                    game.dx[count] = -1;
                    game.dy[count] = 0;
                    count++;
                }
                if ((game.screenData[pos] & 2) == 0 && game.ghost_dy[i] != 1) {
                    game.dx[count] = 0;
                    game.dy[count] = -1;
                    count++;
                }
                if ((game.screenData[pos] & 4) == 0 && game.ghost_dx[i] != -1) {
                    game.dx[count] = 1;
                    game.dy[count] = 0;
                    count++;
                }
                if ((game.screenData[pos] & 8) == 0 && game.ghost_dy[i] != -1) {
                    game.dx[count] = 0;
                    game.dy[count] = 1;
                    count++;
                }
                // получаем случайное число
                count = (int) (Math.random() * count);
                if (count > 3) {
                    count = 3; // если больше трех то 3, т.к. в массиве dx всего 4 элемента
                }
                // перебираем чтобы не ушел за переделы экрана призрак
                if ((game.dx[count] == 0 && game.dy[count] == -1 && (pos > -1 && pos < 15) ||
                        game.dx[count] == -1 && game.dy[count] == 0 && (pos == 0 || pos % 15 == 0) ||
                        game.dx[count] == 1 && game.dy[count] == 0 && (pos == 14 || pos == 29 || pos == 44 || pos == 59 || pos == 74
                                || pos == 89 || pos == 104 || pos == 119 || pos == 134 || pos == 149 || pos == 164 || pos == 179 || pos == 194
                                || pos == 209 || pos == 224) ||
                        game.dx[count] == 0 && game.dy[count] == 1 && pos > 209 && pos < 225)) {
                    game.dx[count] = 0; // если вероятность уйти есть, то скрость будет умножаться на 0, соответственно он не покинет зону игры
                    game.dy[count] = 0;
                }
                // передаем направления в i-й призрак
                game.ghost_dx[i] = game.dx[count];
                game.ghost_dy[i] = game.dy[count];

            }
            // скорость умножаем на направление - получаем перещемение призрака
            game.ghost_x[i] = game.ghost_x[i] + (game.ghost_dx[i] * game.ghostSpeed[i]);
            game.ghost_y[i] = game.ghost_y[i] + (game.ghost_dy[i] * game.ghostSpeed[i]);
            // рисуем призрака
            g2d.drawImage(ghost, game.ghost_x[i] + 1, game.ghost_y[i] + 1, this);
            // если призрак на позиции пакмена то он умирает
            if (game.pacman_x > (game.ghost_x[i] - 12) && game.pacman_x < (game.ghost_x[i] + 12) && game.pacman_y > (game.ghost_y[i] - 12)
                    && game.pacman_y < (game.ghost_y[i] + 12) && game.inGame) {

                game.dying = true;
            }
        }
    }
    // отрисовка пакмена
    private void drawPacman(Graphics2D g2d) {
        // меняем картинки(анимации) ранее загруженные в зависимости от движения
        if (game.req_dx == -1) {    // если пакмен движется влево
            g2d.drawImage(left, game.pacman_x + 1, game.pacman_y + 1, this);
        } else if (game.req_dx == 1) { // если пакмен движется вправо
            g2d.drawImage(right, game.pacman_x + 1, game.pacman_y + 1, this);
        } else if (game.req_dy == -1) { // если пакмен движется вверх
            g2d.drawImage(up, game.pacman_x + 1, game.pacman_y + 1, this);
        } else { // если пакмен движется вниз
            g2d.drawImage(down, game.pacman_x + 1, game.pacman_y + 1, this);
        }
    }
    // отрисовка карты
    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;
        // перебираем все элементы игрового окна
        for (y = 0; y < game.SCREEN_SIZE; y += game.BLOCK_SIZE) {
            for (x = 0; x < game.SCREEN_SIZE; x += game.BLOCK_SIZE) {
                // устанавливаем светло-коричневый цвет
                g2d.setColor(new Color(124, 72, 66));
                g2d.setStroke(new BasicStroke(5));
                // рисуем камни
                if ((game.levelData[i] == 2)) {
                    // закрашенный квадрат
                    g2d.fillRect(x, y, game.BLOCK_SIZE, game.BLOCK_SIZE);
                }
                // рисуем пасхалку
                if ((game.screenData[i] == 4)) {
                    // цвет желтый
                    g2d.setColor(new Color(240, 190, 45));
                    // закрашенный овал
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }
                // рисуем "траву" - объекты за которые получаем очки
                if (game.screenData[i] == 16) {
                    // цвет зеленый
                    g2d.setColor(new Color(0, 255, 0));
                    // закрашенный овал
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }
    // функция инициализации игры
    private void initGame() {
        // количетво жизне 3
        game.lives = 3;
        // количество очков 0
        game.score = 0;
        // инициализация уровня - карты
        initLevel();
        // количество призраков 6
        game.N_GHOSTS = 6;
        // скорость игры 3
        game.currentSpeed = 3;
    }
    // инициализация уровня
    private void initLevel() {
        // объект для генерации случайных чисел
        Random rnd = new Random();
        // количество пасхальных яиц - 1
        int eg = 1;
        // расстановка элементов на карте
        for (int i = 0, j = 0; i < 15; i++) {
            for (int k = 0; k < 15; k++, j++) {
                // если выпало 1, то это камень, иначе трава
                if (rnd.nextInt() % 4 == 1)
                    game.levelData[j] = 2;
                else
                    game.levelData[j] = 16;
                // если первый элемент, то устанавливаем как траву
                if (j == 0) game.levelData[j] = 16;
                // устанавливаем пасхальное яйцо
                if (eg == 1 && rnd.nextInt() % 4 == 1
                        && j > 2 && game.levelData[j - 1] != 2 && game.levelData[j + 1] != 2) {
                    game.levelData[j] = 4;
                    game.egpos = j;
                    eg--;
                }
            }
        }
        // записываем данные с сгенерированной карты в игровую, обновляемую
        for (int i = 0; i < game.N_BLOCKS * game.N_BLOCKS; i++) {
            game.screenData[i] = game.levelData[i];
        }
        // продолжение уровня
        gamePlay.continueLevel();
    }
    // функция отрисовки в панели
    public void paintComponent(Graphics g) {
        // передаем компонент
        super.paintComponent(g);
        // создем новый компонент 2д графики
        Graphics2D g2d = (Graphics2D) g;
        // устанавливаем цвет
        g2d.setColor(Color.black);
        // рисуем квадрат
        g2d.fillRect(0, 0, d.width, d.height);
        // рисуем карту
        drawMaze(g2d);
        // рисуем интерфейс
        drawScore(g2d);
        // рисуем остальные игровые элементы
        if (game.inGame) {
            try {
                playGame(g2d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showIntroScreen(g2d); // если игра не начата, рисуем приветственный элемент
        }
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    // Обработка нажатий для игры и движений пакмена
    class InputAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // получаем код
            int key = e.getKeyCode();
            // в зависиомсти от кода, если мы в игре, реализуем изменение визуализации, ускорение, запуск игры, остановку пакмена
            if (game.inGame) {
                // движение влево
                if (key == KeyEvent.VK_LEFT) {  // движение влево
                    game.req_dx = -1;
                    game.req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) { // движение вправо
                    game.req_dx = 1;
                    game.req_dy = 0;
                } else if (key == KeyEvent.VK_UP) { // движение вверх
                    game.req_dx = 0;
                    game.req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) { // движение вниз
                    game.req_dx = 0;
                    game.req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) { // отмена игры
                    game.inGame = false;
                } else if (key == KeyEvent.VK_A) { // ускорение или замедление
                    game.isfaster = !game.isfaster;
                } else if (key == KeyEvent.VK_S) { // остановка или движение
                    game.stoppac=!game.stoppac;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) { // запуск игры
                    game.inGame = true; // устанеавливаем что игра запущена
                    gamePlay.initGame(); // инициализируем игру
                }
            }
        }
    }
    // стандартная функция перерисовки в панеле
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    // функции реализующая сохранения игры в файл
    public void SaveFile(String path) throws IOException {
        // Создаем объект для работы с json форматом
        Gson gson = new Gson();
        // создаем объект файл для создания файла указывая путь в качестве аргумента
        File file = new File(path);
        // создаем файл
        file.createNewFile();
        // преобразуем в json формат посредством объекта gson
        String json = gson.toJson(game);
        // Создаем объект для буферизированной записи в файл указывая объект FileWriter с путем
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            // производим запись в файл
            bw.write(json);
        }
    }
    // функция загрузки сохранения
    public void LoadSave(String path) throws FileNotFoundException {
        // Создаем объект gson для дессериализации
        Gson gson = new Gson();
        // получаем объект Game благодаря объекту Gson, указывая в качестве аргументов путь и тип класса
        game = gson.fromJson(new FileReader(path), Game.class);
    }
    // получение всех лидеров с файла
    private Leader[] getLeadFromFile() throws IOException {
        // Создаем объект Gson для дессириализации с json в объект
        Gson gson = new Gson();
        // открывае файл для чтения
        BufferedReader br = new BufferedReader(new FileReader("leaders.json"));
        // читаем одну строку
        String json = br.readLine();
        // закрываем файл
        br.close();
        // возвращаем массив лидеров с помощью объекта gson одной строкой
        return gson.fromJson(json, Leader[].class);
    }
    // функция записи лидеров
    private void setLeader(String name, int value) throws IOException {
        int j = 0;
        leaders = getLeadFromFile(); // получаем общее количество лидеров с файла
        // получаем реальное количество лидеров с файла, т.к. некоторые объекты записаны как null
        for (Leader leader : leaders) {
            if (leader != null) {
                j++;
            } else {
                break;
            }
        }
        // созадем новый объект и записываем имя и очки
        leaders[j] = new Leader(name, value);
        // создаем объект для сериализации
        Gson gson = new Gson();
        // открываем файл
        File file = new File("leaders.json");
        if (file.exists()) {
            // очищаем файл
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.setLength(0);
        }
        // делаем json из объекта
        String json = gson.toJson(leaders);
        // открываем файл для записи
        BufferedWriter bw = new BufferedWriter(new FileWriter("leaders.json"));
        // записываем json объект
        bw.write(json);
        // закрываем файл
        bw.close();
    }
    // функция сортировки лидеров по убыванию(очков)
    private void sortLeaders() throws IOException {
        // получение лидеров с файла
        leaders = getLeadFromFile();
        // счетчик
        int k = 0;
        // получения количества объектов не нулевых в массиве лидеров
        for (Leader leader : leaders) {
            if (leader != null) {
                k++;
            } else {
                break;
            }
        }
        // сортировка пузырьком
        for (int i = k - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                // если количество очков лидера под j меньше чем по j+1, то меняем их местами
                if (leaders[j].score < leaders[j + 1].score) {
                    Leader tmp = leaders[j];
                    leaders[j] = leaders[j + 1];
                    leaders[j + 1] = tmp;
                }
            }
        }
    }
    // Функция отрытия таблицы лидеров, из 3 лучших со всего списка
    public void OpenLeaders() throws IOException {
        // сортировка лидеров по убыванию(очков)
        sortLeaders();
        // вывод диалогового сообщения о трех лучших
        // https://vk.com/ze_great - подписываемся, не расходимся
        JOptionPane.showMessageDialog(null,
                new String[]{"Лучшая тройка игроков:",
                        " имя:" + leaders[0].name + " очки:" + leaders[0].score,
                        " имя:" + leaders[1].name + " очки:" + leaders[1].score,
                        " имя:" + leaders[2].name + " очки:" + leaders[2].score},
                "Таблица",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
