import com.acme.util.PasswordHashing;

public class PassHash {

    public static void main(String[] args){
        System.out.println(PasswordHashing.hashPassword("12345678"));
        System.out.println(PasswordHashing.hashPassword("25oct87!"));
    }

}
