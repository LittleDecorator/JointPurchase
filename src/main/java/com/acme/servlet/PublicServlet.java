//package com.acme.servlet;
//
//import com.acme.exception.TemplateException;
//import com.acme.model.Credential;
//import com.acme.model.Subject;
//import com.acme.repository.CredentialRepository;
//import com.acme.repository.SubjectRepository;
//import com.acme.service.EmailService;
//import com.acme.service.SubjectService;
//import com.acme.service.TokenService;
//import com.acme.util.PasswordHashing;
//import com.google.common.base.Strings;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.DefaultTransactionDefinition;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//
//import javax.mail.MessagingException;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Date;
//
///**
// * No generation here. Just update user status and redirect to app result page.
// * There will be login
// */
//public class PublicServlet extends HttpServlet {
//
//	@Value("${app.home}")
//	private String appMainPage;
//	@Value("${app.registration.result}")
//	private String registrationResultPage;
//	@Value("${app.restore.result}")
//	private String restoreResultPage;
//
//    @Autowired
//    TokenService tokenService;
//
//    @Autowired
//    SubjectRepository subjectRepository;
//
//	@Autowired
//	SubjectService subjectService;
//
//    @Autowired
//    EmailService emailService;
//
//	@Autowired
//	PlatformTransactionManager transactionManager;
//
//    @Autowired
//    CredentialRepository credentialRepository;
//
//    private WebApplicationContext springContext;
//
//    @Override
//    public void init(final ServletConfig config) throws ServletException {
//        super.init(config);
//        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
//        final AutowireCapableBeanFactory beanFactory = springContext.getAutowireCapableBeanFactory();
//        beanFactory.autowireBean(this);
//    }
//
//    @Override
//    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        // получаем токен
//        final String token = req.getParameter("jwt");
//        res.setStatus(HttpServletResponse.SC_FOUND);
//
//        // если пытаемся пройти без токена вообще
//        if(Strings.isNullOrEmpty(token)){
//            res.sendRedirect(appMainPage);
//            return;
//        }
//        // определим цель запроса
//        String page = req.getPathInfo().contains("restore") ? restoreResultPage : registrationResultPage;
//
//        // парсим токен
//        try {
//            final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();
//            // если токен просрочен, будет брошен exception
//            if(claims.getExpiration().after(new Date())){
//				Subject subject = subjectRepository.findOne(claims.getId());
//				// если не нашли клиента, то ошибка
//				if (subject == null) {
//					res.sendRedirect(page + "?confirmed=false");
//					return;
//				}
//				// получаем credential
//				Credential credential = credentialRepository.findOne(subject.getId());
//
//                // если цель восстановление пароля
//                if(page.contentEquals(registrationResultPage)){
//					addTokenHeader(res, credential);
//					if (subject.getEnabled()) {
//						// если пользователь уже активирован
//						res.sendRedirect(appMainPage);
//					} else {
////							subjectService.enableSubject(subject);
////							TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
////							try{
//								subjectRepository.enableById(subject.getId());
////								subject = subjectRepository.findOne(claims.getId());
////								subject.setEnabled(true);
////								subject.setMiddleName("HYI");
////								subjectRepository.save(subject);
////								System.out.println(status.isCompleted());
////								System.out.println(status.isNewTransaction());
////								System.out.println(status.isRollbackOnly());
////								transactionManager.commit(status);
////								System.out.println(status.isCompleted());
////								System.out.println(status.isNewTransaction());
////								System.out.println(status.isRollbackOnly());
////							emailService.sendRegistrationConfirm(subject.getEmail());
////								res.sendRedirect(page + "?confirmed=true");
////							} catch (Exception ex){
////								System.out.println(Arrays.toString(ex.getStackTrace()));
////								transactionManager.rollback(status);
////								System.out.println(status.isCompleted());
////								System.out.println(status.isNewTransaction());
////								System.out.println(status.isRollbackOnly());
////							}
//							// если регистрация прошла успешно, то отправим письмо о результате
////							emailService.sendRegistrationConfirm(subject.getEmail());
////							res.sendRedirect(page + "?confirmed=true");
//					}
//                } else {
//					TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//					try{
//						credential.setPassword(PasswordHashing.hashPassword(String.valueOf(claims.get("password"))));
//						credentialRepository.save(credential);
//						transactionManager.commit(status);
////						emailService.sendPassChangeConfirm(subject.getEmail());
//						res.sendRedirect(page + "?confirmed=true");
//					} catch (Exception ex){
//						System.out.println(Arrays.toString(ex.getStackTrace()));
//						transactionManager.rollback(status);
//					}
//                }
//            }
//        } catch (ExpiredJwtException e) {
//            res.sendRedirect(page + "?confirmed=false");
//        }
//
//	}
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
//    }
//
//	private void addTokenHeader(HttpServletResponse response, Credential credential){
//		response.addHeader("Authorization", tokenService.createToken(credential));
//	}
//}
//
