import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spectacole.services.IServices;

import java.util.Arrays;
import java.util.List;

public class StartClient {
    public static void main(String[] args) {
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
        IServices server = (IServices) factory.getBean("SpectacolService");
        List<Cumparator> clienti = Arrays.asList(new Cumparator(server), new Cumparator(server), new Cumparator(server), new Cumparator(server), new Cumparator(server));
        for (Cumparator cumparator : clienti) {
            cumparator.buyTickets();
        }
    }
}
