import java.util.ArrayList;
import java.util.List;

public class AllPcode {
    List<Pcode> allPcode;
    private int ptr = 0;


    public AllPcode() {
        allPcode = new ArrayList<Pcode>();
        for(int i=0;i<200;i++)
            allPcode.add(new Pcode());
    }

    public List<Pcode> getAllPcode() {
        return allPcode;
    }

    public int getPcodePtr() {
        return ptr;
    }

    public void gen(Pcode pcode) {
        allPcode.add(pcode);
        ptr++;
    }

    public void gen(Operator F, int L, int A) {
        allPcode.get(ptr).setF(F);
        allPcode.get(ptr).setL(L);
        allPcode.get(ptr).setA(A);
        //System.out.println("A:"+ A);
//        allPcode.add(new Pcode(F,L,A));
        ptr++;
    }
}

