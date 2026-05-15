package dao;

import config.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import models.Enrollment;

public class EnrollmentDAO {

    public List<Enrollment> findAll() {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            return em.createQuery("SELECT e FROM Enrollment e", Enrollment.class).getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean insertOne(Enrollment e) {//new

        if (isDuplicateEnrollment(
                e.getStudent().getStudentId(),
                e.getCourse().getCourseId())) {
            return false;
        }

        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            em.getTransaction().begin();

            em.persist(e);//احفظلي ال e // new->Detached

            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            return false;

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean updateOne(Enrollment e) { //detached 
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            em.getTransaction().begin();

            em.merge(e);//Detached-> Managed

            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            return false;

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean deleteOne(Enrollment e) { //Detached
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            em.getTransaction().begin();

            Enrollment managedEnrollment = em.merge(e);//Detached-> Managed

            em.remove(managedEnrollment);

            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            return false;

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean isDuplicateEnrollment(int studentId, int courseId) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            List<Enrollment> list = em.createQuery(
                    "SELECT e FROM Enrollment e "
                    + "WHERE e.student.studentId = :studentId "
                    + "AND e.course.courseId = :courseId",
                    Enrollment.class
            )
                    .setParameter("studentId", studentId)
                    .setParameter("courseId", courseId)
                    .getResultList();

            return !list.isEmpty();

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}