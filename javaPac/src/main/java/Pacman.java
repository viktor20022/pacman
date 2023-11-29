import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Pacman extends JFrame {
    // меню
    JMenuBar jm;
    JMenu file;
    // элементы меню в окне
    JMenuItem fileSave;
    JMenuItem loadSave;
    JMenuItem openLeades;
    JMenuItem openHelp;
    // панель на которой разворачивается игра
    Panel game;

    public Pacman() {
        // созадем панель игры
        game = new Panel();
        // добавляем в окно
        add(game);
        // созадем меню
        CreateMenu();
    }

    public static void main(String[] argc) {
        // создаем класс нашего объекта унаследованного у окна
        Pacman pc = new Pacman();
        // делаем видимым
        pc.setVisible(true);
        // задаем титульное название окна
        pc.setTitle("Pacman");
        // задаем размеры окна
        pc.setSize(380, 450);
        // стандартная операция на закрытие
        pc.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // центрируем окно на экране
        pc.setLocationRelativeTo(null);
    }

    private void CreateMenu() {
        // создаем верхнее меню
        jm = new JMenuBar();
        // создаем опции для меню сверху
        file = new JMenu("Опции");
        // подэлементы опции
        fileSave = new JMenuItem("Сохранить");
        loadSave = new JMenuItem("Загрузить");
        openLeades = new JMenuItem("Лидеры");
        openHelp = new JMenuItem("Помощь");
        // добавляем подэлементы в опции
        file.add(fileSave);
        file.add(loadSave);
        file.add(openLeades);
        file.add(openHelp);
        // добавляем опцию в меню
        jm.add(file);
        // устанавливаем меню
        setJMenuBar(jm);
        // действия на нажатие пункат - помощь
        openHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Помощь и советы");
                // вызываем диалоговое окно в котором расписываем хаки игры )
                JOptionPane.showMessageDialog(null,
                        new String[]{"Сап анонимус, вот тебе несколько советов",
                        "1) Нажмимай клавишу - A, если хочешь быть быстрее(В сторону движения задаешь стрелками);",
                        "2) Желтый кружок - пасхалка. Дает +50 очков;",
                        "3) Бот может проходить свозь стены, т.к. он призрак сечешь :);",
                        "4) Не натыкайся на фиолетовые камни, береги жизнь;",
                        "5) Чтобы остановиться используй клавишу - S. Чтобы снова идти нажми ее же;",
                        "6) Оставь о себе след в истории - напиши свое имя после смерти в игре!",
                        "7) Ничто не истина, все дозволено(Завершай игру на ESC)",
                        "https://vk.com/ze_great - подписывайтемся"},
                        "Помощь и советы",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // действия на пункте - Лидеров
        openLeades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Лидеры");
                // в классе прописан способ считывания с файла лидеров
                try {
                    game.OpenLeaders();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // Действия на пункте - сохранения
        fileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Сохранение игры");
                // создаем диалоголое окно файлоа
                FileDialog fd = new FileDialog(new JFrame());
                // делаем видимым
                fd.setVisible(true);
                // получае файлы
                File[] f = fd.getFiles();
                // если было прописано что либо
                if (f.length > 0) {
                    System.out.println(fd.getFiles()[0].getAbsolutePath());
                    try {
                        // передаем путь в класс в котором осуществляется запись сохранения
                        game.SaveFile(fd.getFiles()[0].getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // действия на пункт загрузки сохранения
        loadSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Открытие файла сохранения");
                try {
                    // открываем диалоговое окно - проводник
                    FileDialog fd = new FileDialog(new JFrame());
                    fd.setVisible(true);
                    // получаем файл
                    File[] f = fd.getFiles();
                    // если он был выбран
                    if (f.length > 0) {
                        // получае путь к нему и передаем в класс который производит чтение с файла сохранения и запуск
                        System.out.println(fd.getFiles()[0].getAbsolutePath());
                        game.LoadSave(fd.getFiles()[0].getAbsolutePath());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
