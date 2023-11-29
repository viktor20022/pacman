// класс лидеров
public class Leader {
    String name; // имя лидера
    int score; // его рекорд
    // конструктор класса
    Leader(String name, int score){
        setName(name);
        setScore(score);
    }
    // сеттеры и гетеры
    public void setName(String name){
        this.name = name;
    }
    public void setScore(int score){
        this.score = score;
    }
    public int getScore(){
        return score;
    }
    public String getName(){
        return name;
    }
}
