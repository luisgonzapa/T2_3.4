/*
 * Luís González Palomo
 * DI_T2 Agenda App
 */
package agendaapp;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Luis
 */
//Prueba la consulta a la base de datos.
public class ConsultaProvincias 
{
    
    public static void main(String[] args) 
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AgendaAppPU");
        EntityManager em = emf.createEntityManager();
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();
        em.getTransaction().begin();
        for (Provincia provinciaCadiz : listProvinciasCadiz) 
        {
            provinciaCadiz.setCodigo("11");
            em.merge(provinciaCadiz);
        }

        em.getTransaction().commit();

        Provincia provinciaId15 = em.find(Provincia.class, 15);
        
        if (provinciaId15 != null) 
        {
            System.out.print(provinciaId15.getId() + ": ");
            System.out.println(provinciaId15.getNombre());
        } 
        else 
        {
            System.out.println("No hay ninguna provincia con ID=2");
        }

        if (provinciaId15 != null) 
        {
            em.remove(provinciaId15);
        } 
        else 
        {
            System.out.println("No hay ninguna provincia con ID=2");
        }
        //Corta el flujo de datos de las clases EntityManager.
        em.close();
        emf.close();
        try 
        {
            //Cierra la conexión la base de datos.
            DriverManager.getConnection("jdbc:derby:BDAgendaContactos;shutdown=true");
        } 
        catch (SQLException ex) 
        {
            //No realizará nada.
        }
        
    }//Fin Main-Consulta
}//Fin clase
