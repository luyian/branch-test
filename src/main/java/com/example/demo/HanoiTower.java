package com.example.demo;

import java.util.Stack;

public class HanoiTower {
    public static void main(String[] args) {
        Hanoi("A","B","C",3);
    }
    public static void Hanoi(String a,String b,String c,int num){
        Stack<State> starks = new Stack<> ();
        State state = new State(a,b,c,num,false);
        starks.push (state);
        while (starks.size ()>0){
            if(starks.peek ().title){
                HanoiPrint(starks.pop ());
            }
            else {
                State NumLinShi=starks.pop ();
                if (NumLinShi.Num==1){
                    HanoiPrint(NumLinShi);
                }
                else {
                    State NumLinA_B = new State (NumLinShi.A,NumLinShi.C,NumLinShi.B,NumLinShi.Num-1,false);
                    State NumLinA_C = new State (NumLinShi.A,NumLinShi.B,NumLinShi.C,NumLinShi.Num,true);
                    State NumLinB_C = new State (NumLinShi.B,NumLinShi.A,NumLinShi.C,NumLinShi.Num-1,false);
                    starks.push (NumLinB_C);
                    starks.push (NumLinA_C);
                    starks.push (NumLinA_B);
                }
            }
        }
    }
    public static void HanoiPrint(State str){
        System.out.println ("把编号为\t" + str.Num + "\t的圆盘从\t" + str.A + "\t移动到\t" + str.C + "\t盘子上");
    }
}
class State {
    public String A;
    public String B;
    public String C;
    public int Num;
    public boolean title;
    public State(String A, String B, String C , int Num, boolean title)
    {
        this.A = A;
        this.B = B;
        this.C = C;
        this.Num = Num;
        this.title = title;
    }
}
