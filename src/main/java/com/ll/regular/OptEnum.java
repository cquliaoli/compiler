package com.ll.regular;

/**
 * Created by liaoli
 * date: 2019/6/25
 * time: 11:36
 *
 * @author: liaoli
 */
public enum OptEnum {
    /**
     *  |
     */
    Alt(1,'|'),
    /**
     *  a b
     */
    CAT(2,' '),
    /**
     *  *
     */
    STAR(3,'*'),
    /**
     *  (
     */
    LEFT_PAREN(4,'('),
    /**
     *  )
     */
    RIGHT_PAREN(-1,')');

    private int priority;

    private char opt;

    OptEnum(int priority, char opt) {
        this.priority = priority;
        this.opt = opt;
    }

    public char getOpt() {
        return opt;
    }

    public void setOpt(char opt) {
        this.opt = opt;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
