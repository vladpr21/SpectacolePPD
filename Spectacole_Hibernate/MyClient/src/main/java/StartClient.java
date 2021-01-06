//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import spectacole.services.IServices;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StartClient {
//    public static void main(String[] args) {
//        int nrClienti = 5;
//        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
//        IServices server = (IServices) factory.getBean("SpectacolService");
//        List<Cumparator> clienti = new ArrayList<>();
//        for (int i = 0; i < nrClienti; i++) {
//            clienti.add(new Cumparator(server));
//        }
//        for (Cumparator cumparator : clienti) {
//            cumparator.buyTickets();
//        }
//    }
//}


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spectacole.model.SpectacoleEntity;
import spectacole.services.IServices;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StartClient {
    public static void main(String[] args) {
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
        IServices server = (IServices) factory.getBean("SpectacolService");
        CompletableFuture<List<SpectacoleEntity>> completableSpectacoleEntities
                = CompletableFuture.supplyAsync(() -> {
            try {
                return server.getSpectacole();
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        });

        List<SpectacoleEntity> spectacoleEntities = new ArrayList<>();
        try {
            spectacoleEntities = completableSpectacoleEntities.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //System.out.println(spectacoleEntities);

        boolean serverOn = true;

        while (serverOn) {
            List<Integer> locuri = new ArrayList<>();
            Random random = new Random();
            Integer nrLocuri = random.nextInt(10);
            for (int i = 0; i < nrLocuri; i++) {
                locuri.add(random.nextInt(100));
            }
            SpectacoleEntity spectacoleEntity = spectacoleEntities.get(random.nextInt(spectacoleEntities.size()));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Intrerupt");
                System.out.println(ex.getMessage());
            }

            CompletableFuture<String> completableRez
                    = CompletableFuture.supplyAsync(() -> {
                try {
                    return server.buyTickets(locuri, spectacoleEntity);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            String rez = "";
            try {
                rez = completableRez.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (rez.equals("Sold-out")) {
                serverOn = false;
                System.out.println("Clientul s-a oprit");
            } else {
                System.out.println(rez);
            }
        }

    }
}
