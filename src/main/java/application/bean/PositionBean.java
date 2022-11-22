package application.bean;

import java.io.Serializable;
import java.util.Objects;

public class PositionBean implements Serializable {
    int r;
    int c;
    TYPE type;

    public PositionBean(int r, int c, TYPE type) {
        this.r = r;
        this.c = c;
        this.type = type;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }

    public TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PositionBean{" +
                "r=" + r +
                ", c=" + c +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionBean that = (PositionBean) o;
        return r == that.r && c == that.c;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, c);
    }
}
