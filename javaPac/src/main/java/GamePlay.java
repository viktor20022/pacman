import java.awt.*;
import java.util.Random;

public class GamePlay {

    public Game game;

    public GamePlay() {

    }

    public void setXPac(int x){
        this.game.pacman_x  = x;

    }

    public void setGame(Game game){
        this.game = game;
    }

    public Game getGame(){
        return game;
    }

    public GamePlay(Game game) {
        this.game = game;
    }

    // функция инициализации игры
    public boolean initGame() {
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
        return game.inGame;
    }

    // инициализация переменных
    public void initVariables() {
        // инициализируем массив карты
        game.screenData = new int[game.N_BLOCKS * game.N_BLOCKS];
        // инициализируем массивы призарков
        game.ghost_x = new int[game.MAX_GHOSTS];
        game.ghost_dx = new int[game.MAX_GHOSTS];
        game.ghost_y = new int[game.MAX_GHOSTS];
        game.ghost_dy = new int[game.MAX_GHOSTS];
        game.ghostSpeed = new int[game.MAX_GHOSTS];
        // инициализируем переменны для изменения пакмена по x и y
        game.dx = new int[4];
        game.dy = new int[4];
    }
    // движение пакмена
    public void movePacman() {
        int pos;
        int ch;
        if (game.pacman_x % game.BLOCK_SIZE == 0 && game.pacman_y % game.BLOCK_SIZE == 0) {
            // получение позиции
            pos = (game.pacman_x / game.BLOCK_SIZE) + (game.N_BLOCKS * (int) (game.pacman_y / game.BLOCK_SIZE));
            // получение числа с массива данными карты
            ch = game.screenData[pos];
            // если настигнуто пасхальное яйцо
            if (pos == game.egpos) {
                // значит убираем его
                game.egpos = -1;
                // размещаем там камень
                game.screenData[pos] = 2;
                game.levelData[pos] = 2;
                // увеличиваем количество очков на 50
                game.score += 50;
            }
            // если попал на камень
            if ((ch & 2) != 0) {
                game.dying = true;
            }
            // если попал на еду
            if ((ch & 16) != 0) {
                game.screenData[pos] = (ch & 15);
                game.score++;
            }
            // перемещение (сначала проверка на изменение анимации)
            if (game.req_dx != 0 || game.req_dy != 0) {
                game.pacmand_x = game.req_dx;
                game.pacmand_y = game.req_dy;
            }
            // Коллизия, проверка на возможный выход за пределы экрана
            if ((game.pacmand_x == 0 && game.pacmand_y == -1 && (pos > -1 && pos < 15) ||
                    game.pacmand_x == -1 && game.pacmand_y == 0 && (pos == 0 || pos % 15 == 0) ||
                    game.pacmand_x == 1 && game.pacmand_y == 0 && (pos == 14 || pos == 29 || pos == 44 || pos == 59 || pos == 74
                            || pos == 89 || pos == 104 || pos == 119 || pos == 134 || pos == 149 || pos == 164 || pos == 179 || pos == 194
                            || pos == 209 || pos == 224) ||
                    game.pacmand_x == 0 && game.pacmand_y == 1 && pos > 209 && pos < 225) ||
                    game.stoppac) {
                // если это возможно, то указываем 0 перемещение
                game.pacmand_x = 0;
                game.pacmand_y = 0;
            }
        }
        // если скорость активирована, меняем ее
        game.PACMAN_SPEED = game.isfaster ? 6 : 3;
        // получаем новый координаты исходя из скорости умноженной на перемещение
        // (pacmand_x хранит значения -1, 0 или 1. Позволяет если -1 - повернуть влево, 0 - остановиться, 1 - повернуть вправо)
        // аналогично со второй переменной только по Y
        game.pacman_x = game.pacman_x + game.PACMAN_SPEED * game.pacmand_x;
        game.pacman_y = game.pacman_y + game.PACMAN_SPEED * game.pacmand_y;
    }
    // инициализация уровня
    public void initLevel() {
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
        continueLevel();
    }

    // продолжение уровня
    public void continueLevel() {
        int dx = 1;
        int random;
        for (int i = 0; i < game.N_GHOSTS; i++) {
            game.ghost_y[i] = 4 * game.BLOCK_SIZE; // стартовые позиции призарков
            game.ghost_x[i] = 4 * game.BLOCK_SIZE;
            game.ghost_dy[i] = 0;
            game.ghost_dx[i] = dx;
            dx = -dx;
            // случайные скорости призраков
            random = (int) (Math.random() * (game.currentSpeed + 1));
            if (random > game.currentSpeed) {
                random = game.currentSpeed;
            }
            game.ghostSpeed[i] = game.validSpeeds[random];
        }

        game.pacman_x = 0; // стартовая позиция пакмена
        game.pacman_y = 0;
        game.pacmand_x = 0; // направления пакмена
        game.pacmand_y = 0;
        game.req_dx = 0; // направления анимации
        game.req_dy = 0;
        game.dying = false; // пакмен живой
    }

    // проврека игры
    public void checkMaze() {

        int i = 0;
        boolean finished = true;
        // пробегаемся по блокам
        while (i < game.N_BLOCKS * game.N_BLOCKS && finished) {
            // если блоки не равны нулю, то игра не завершена
            if ((game.screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }
        // если игра завершена
        if (finished) {
            // Добавляем очков
            game.score += 50;
            // добавляем призарков
            if (game.N_GHOSTS < game.MAX_GHOSTS) {
                game.N_GHOSTS++;
            }
            // добавляем скорости игре
            if (game.currentSpeed < game.maxSpeed) {
                game.currentSpeed++;
            }
            // созадем новый уровень
            initLevel();
        }
    }

    // функция перемещения призраков
    public void moveGhosts() {
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
            // если призрак на позиции пакмена то он умирает
            if (game.pacman_x > (game.ghost_x[i] - 12) && game.pacman_x < (game.ghost_x[i] + 12) && game.pacman_y > (game.ghost_y[i] - 12)
                    && game.pacman_y < (game.ghost_y[i] + 12) && game.inGame) {
                game.dying = true;
            }
        }
    }
}
