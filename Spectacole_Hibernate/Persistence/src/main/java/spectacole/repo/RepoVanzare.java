package spectacole.repo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import spectacole.model.SpectacoleEntity;
import spectacole.model.VanzareEntity;

import java.util.ArrayList;
import java.util.List;

public class RepoVanzare implements IRepoVanzare {

    HibernateUtils utils;
    public RepoVanzare(HibernateUtils utils){
        this.utils = utils;
    }

    @Override
    public synchronized VanzareEntity save(VanzareEntity vanzare) {
        SessionFactory sessionFactory = utils.getInstance();
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(vanzare);
            session.getTransaction().commit();
            return vanzare;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public synchronized List<VanzareEntity> findVanzariBySpectacol(SpectacoleEntity spectacoleEntity) {
        List<VanzareEntity> rez = new ArrayList<>();
        SessionFactory sessionFactory = utils.getInstance();
        Session session = sessionFactory.openSession();
        try {
            session.getTransaction();
            Query query = session.createQuery("from  VanzareEntity WHERE spectacol = :mySpectacol");
            query.setParameter("mySpectacol", spectacoleEntity);
            List result = query.list();
            rez = (List<VanzareEntity>)result;
        }finally {
            session.close();
        }
        return rez;
    }

    @Override
    public synchronized VanzareEntity findLastTransaction() {
        List<VanzareEntity> rez = new ArrayList<>();
        VanzareEntity last = null;
        SessionFactory sessionFactory = utils.getInstance();
        Session session = sessionFactory.openSession();
        try {
            session.getTransaction();
            Query query = session.createQuery("from  VanzareEntity");
            List result = query.list();
            rez = (List<VanzareEntity>)result;
            Long maxi = new Long((long) 0.0);
            for(VanzareEntity v:rez){
                if(v.getIdTranzactie() > maxi){
                    maxi = v.getIdTranzactie();
                    last = v;
                }
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }finally {
            session.close();
        }
        return last;
    }

    @Override
    public int size() {
        SessionFactory sessionFactory = utils.getInstance();
        Integer nr = 0;
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            Query query = session.createQuery("select count (*) from VanzareEntity");
            Long count = (Long) query.uniqueResult();
            System.out.println("Rez=" + count);
            nr = Integer.parseInt(count.toString());
            session.getTransaction().commit();
        }
        return nr;
    }
}
