import java.util.Locale;

public class Local {

    public static void main(String args[]){

        Locale american = new Locale("en", "US");
        Locale.setDefault(american);
        System.out.println(Locale.getDefault());
    }

}
