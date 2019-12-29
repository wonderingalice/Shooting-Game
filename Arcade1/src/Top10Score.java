 /*
* File: Top10Score.java
* Description: File I/O for top 10 scores
* Author: Alice Yu
* Date-written:  3/6/2019
*/
import javafx.util.Pair;
import sun.rmi.server.InactiveGroupException;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Top10Score{
    private static final String FILE_NAME = "top10scores.txt";
    private static final int TOP_SCORE_LIST = 10;
    
    private ArrayList<Integer> topScore = new ArrayList<Integer>();
    private ArrayList<String> topScoreNames = new ArrayList<String>();

    public void loadScore()  {
        try {
            Scanner reader = new Scanner(new File(FILE_NAME));

            int i = 0;
            while (reader.hasNext() && i < TOP_SCORE_LIST) {
                topScoreNames.add(reader.nextLine());
                topScore.add(reader.nextInt());
                reader.nextLine();
                i++;
            }
            reader.close();
        } catch (Exception e)   {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, FILE_NAME+ " is missing", "Score Records",
                    JOptionPane.INFORMATION_MESSAGE, null);
        }
    }    
    
    public void saveScore(int score, String name) throws Exception {
        addScore(score, name);
        PrintWriter writer = new PrintWriter(new File(FILE_NAME));
        for(int i = 0; i < topScore.size(); i++ )
        {
            writer.println(topScoreNames.get(i));
            writer.println(topScore.get(i));
        }
        writer.close();
    }

    public String getHighestScore(){
        if( topScore.size() != 0)
            return String.valueOf(topScore.get(0)); //sorted, the highest score is on the top
        else
            return "0";
    };

    private void sortScore(){
        int len = topScore.size();
        for(int i = 0; i < len; i++){
            for(int j = 0; j < len-i-1; j++){
                if( topScore.get(j)  < topScore.get(j+1)){
                    int temp = topScore.get(j);
                    topScore.set(j, topScore.get(j+1));
                    topScore.set(j+1,temp);
                    
                    String tempN = topScoreNames.get(j);
                    topScoreNames.set(j, topScoreNames.get(j+1));
                    topScoreNames.set(j+1,tempN);
                }
            }
        }
    }
    
    private void addScore(int score, String name){
        topScore.add(score);
        topScoreNames.add(name);
        sortScore();
    }
    
    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i < topScore.size(); i++ )
        {
            s = s + topScoreNames.get(i) + ": ";
            s = s + topScore.get(i) + "\n";
        }
        return s;
    }
}