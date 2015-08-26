
import com.acme.gen.domain.Credential;
import com.acme.service.impl.TokenServiceImpl;

public class GenerateToken {

    public static void main(String[] args){
        Credential credential = new Credential();
        credential.setSubjectId("test");
        credential.setRoleId("user");
        credential.setPassword("56cc66bac5b167db62297f5a6ee06af3b5efce10aa8bcc6a70ea2ffc785961c7e852257a1796e4ab9fd9737928c5bbea40225564868341cf8db69e2ae599dcc8");
        System.out.println(new TokenServiceImpl().createExpToken(credential, (long) (24*60*60*1000)));
    }

}
