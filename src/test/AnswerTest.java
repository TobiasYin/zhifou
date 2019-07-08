package test;

import beans.Answer;
import beans.User;

import java.util.ArrayList;

public class AnswerTest {
    public static void main(String[] args) {
        User u = User.getById("bb105db1addc44ad83a60de84b0863cd");
        ArrayList<Answer> answers = Answer.getAnswersBySearch("abc", 0, 20);
        if (answers != null)
            answers.forEach(System.out::println);
    }
}
