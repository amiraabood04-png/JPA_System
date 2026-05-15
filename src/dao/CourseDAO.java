package dao;

import config.JPAUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import models.Course;

public class CourseDAO {

    public List<Integer> getAllCourseIds() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                    "SELECT c.courseId FROM Course c",
                    Integer.class
            ).getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public Course findById(int courseId) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();

            return em.find(Course.class, courseId);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
