import spectacole.model.SpectacoleEntity;
import spectacole.services.IServices;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class Cumparator {

    private IServices services;

    public Cumparator(IServices services) {
        this.services = services;
    }

    public void buyTickets() {
        Timer timer = new Timer();
        timer.schedule(new BuyTicket(services), 0, 2000);
    }

}

class BuyTicket extends TimerTask {

    private IServices services;

    public BuyTicket(IServices services) {
        this.services = services;
    }

    public void run() {
        CompletableFuture<List<SpectacoleEntity>> completableSpectacoleEntities
                = CompletableFuture.supplyAsync(() -> {
            try {
                return services.getSpectacole();
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

        SpectacoleEntity spectacol = spectacoleEntities.get(ThreadLocalRandom.current().nextInt(0, spectacoleEntities.size()));
        int nrBilete = ThreadLocalRandom.current().nextInt(1, 9);
        List<Integer> locuri = new ArrayList<>();
        for (int i = 0; i < nrBilete; i++) {
            locuri.add(ThreadLocalRandom.current().nextInt(1, 101));
        }
        CompletableFuture<String> completableRez
                = CompletableFuture.supplyAsync(() -> {
            try {
                return services.buyTickets(locuri, spectacol);
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
            System.out.println("Cumparator out");
            cancel();
            return;
        } else {
            System.out.println(rez);
        }
    }

}
