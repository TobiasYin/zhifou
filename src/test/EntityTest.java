package test;

import beans.Answer;
import beans.Question;
import beans.User;
import util.GetCaptcha;

import java.util.ArrayList;
import java.util.Random;

public class EntityTest {
    private static ArrayList<User> createUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            User user = User.register("Test" + i, GetCaptcha.GetRandomString(10));
            users.add(user);
        }
        return users;
    }

    private static ArrayList<User> getTestUsers() {

        ArrayList<User> users = createUsers();
        if (!users.get(0).isStatus()) {
            users.clear();
            for (int i = 0; i < 30; i++) {
                User user = new User("Test" + i);
                users.add(user);
            }
        }
        return users;
    }

//    public static ArrayList<Answer> get

    public static void main(String[] args) {
        Random r = new Random();
        ArrayList<User> users = getTestUsers();
        System.out.println(users.get(1));
        for (int i = 0; i < users.size(); i++) {
            User u1 = users.get(r.nextInt(users.size()));
            User u2 = users.get(r.nextInt(users.size()));
            u1.follow(u2);
            if (!u1.isFollow(u2))
                System.out.println("错误");
            u2.follow(u1);
            u1.unfollow(u1);
        }
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < users.size() * 5; i++) {
            questions.add(Question.addQuestion(users.get(r.nextInt(users.size())), "问题标题" +  GetCaptcha.GetRandomString(10), "问题描述" + GetCaptcha.GetRandomString(100)));
        }

        ArrayList<Answer> answers = new ArrayList<>();
        for (int i = 0; i <  users.size() * 10; i++) {
            answers.add(Answer.addAnswer(questions.get(r.nextInt(questions.size())), users.get(r.nextInt(users.size())), "回答详情" + GetCaptcha.GetRandomString(1000)));
        }

        for (int i =0; i < 100 * users.size() ; i++){
            User u = users.get(r.nextInt(users.size()));
            Answer a = answers.get(r.nextInt(answers.size()));
            a.agree(u);
            if (r.nextDouble() > 0.7){
                a.disAgree(u);
            }else if (r.nextDouble() < 0.2){
                a.cancelAgree(u);
            }
        }

        for (Answer a:answers){
            System.out.println("agree count: " + a.getAgreeCount());
        }
        for(Question q: questions){
            System.out.println("answer count: "+ q.getAnswerCount());
        }
        answers = Answer.getNewestAnswers(0, 5);
        if (answers == null) return;
        for (Answer a:answers) {
            System.out.println(a);
        }
        System.out.println("===============");
        answers = Answer.getHottestAnswers(0, 5);
        if (answers == null) return;
        for (Answer a:answers) {
            System.out.println(a);
        }
    }
}
