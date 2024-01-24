package com.lycoon.clashbot.utils.database;

import com.lycoon.clashbot.core.ClashBotMain;
import com.lycoon.clashbot.utils.database.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Optional;

public class DatabaseHandler
{
    private static final StandardServiceRegistry registry;
    private static final SessionFactory sessionFactory;

    static
    {
        registry = new StandardServiceRegistryBuilder().build();

        try
        {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClasses(User.class)
                    .buildMetadata()
                    .buildSessionFactory();
        }
        catch (Exception e)
        {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError(e);
        }
    }

    protected static Optional<User> getUser(long id)
    {
        try (Session session = sessionFactory.openSession())
        {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
        catch (EntityNotFoundException e) { return Optional.empty(); }
        catch (Exception e) { ClashBotMain.LOGGER.error(e.getMessage()); return Optional.empty(); }
    }

    protected static void saveUser(User user)
    {
        try (Session session = sessionFactory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        }
        catch (Exception e) { ClashBotMain.LOGGER.error(e.getMessage()); }
    }

    protected static void removeUser(long id)
    {
        try (Session session = sessionFactory.openSession())
        {
            Transaction transaction = session.beginTransaction();
            session.remove(new User(id));
            transaction.commit();
        }
        catch (EntityNotFoundException ignored) {}
        catch (Exception e) { ClashBotMain.LOGGER.error(e.getMessage()); }
    }
}
