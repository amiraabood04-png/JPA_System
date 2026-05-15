package dao;

import config.JPAUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import models.Student;

public class StudentDAO {

    public List<Integer> getAllStudentIds() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager(); 
            return em.createQuery("SELECT s.studentId FROM Student s", Integer.class).getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public Student findById(int studentId) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            return em.find(Student.class, studentId);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
