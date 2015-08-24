import com.acme.util.PasswordHashing;

public class PassHash {

    public static void main(String[] args){
        System.out.println(PasswordHashing.hashPassword("nina21032013"));
        System.out.println(PasswordHashing.hashPassword("password"));
    }

}
