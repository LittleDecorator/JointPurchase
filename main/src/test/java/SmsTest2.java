import com.acme.service.SmsService;
import com.acme.service.impl.SmsServiceImpl;
import com.acme.sms.CredentialKey;
import com.acme.sms.SMSAccount;

import java.util.UUID;

public class SmsTest2 {

    public static void main(String[] args){
        SmsService service = new SmsServiceImpl();

        SMSAccount account = SMSAccount.instance().setId(UUID.randomUUID().toString())
                .putBuild(CredentialKey.EMAIL, "knpdeveloper@gmail.com")
                .putBuild(CredentialKey.PASSWORD, "25oct87!");

        service.sendCallback(account,"TestSrc","79263959143","Sample sms test");
    }

}
