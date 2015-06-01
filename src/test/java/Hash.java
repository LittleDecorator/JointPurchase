import com.acme.util.CheckSum;

import java.security.NoSuchAlgorithmException;

public class Hash {
    public static void main(String[] args){
        String str = "BLA";
        System.out.println(str.hashCode());
        try {
            System.out.println(new CheckSum().SHAsum(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
