public class Pcode {
    private Operator F;
    private int L;
    private int A;

    Pcode(){}
    Pcode(Operator _F, int _L, int _A) {
        F = _F;
        L = _L;
        A = _A;
    }

    public void setF(Operator _F) {
        F = _F;
    }

    public void setL(int _L) {
        L = _L;
    }

    public void setA(int _A) {
        A = _A;
    }

    public Operator getF() {
        return F;
    }

    public int getL() {
        return L;
    }

    public int getA() {
        return A;
    }

}
