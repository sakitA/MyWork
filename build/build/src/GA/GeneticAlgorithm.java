/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package GA;

import java.util.Random;
import type.Course;
import type.Teacher;
import util.Info;
import util.R;


/**
 * Genetic Algorithms (GA) were invented to mimic some of the processes
 * observed in natural evolution. GAs are adaptive heuristic search algorithm 
 * based on the evolutionary ideas of natural selection and genetics. The basic 
 * techniques of the Gas are designed to simulate processes in natural systems 
 * necessary for evolution, especially those follow the principles first laid 
 * down by Charles Darwin of “survival of the fittest.” I did not use crossover
 * only mutation is enough to solve our problem. In mutation operation we change
 * teacher's lecture time but keep the room same.
 * 
 * @author Sakit
 */
public class GeneticAlgorithm {

    private static final Random rd = new Random();
    public static TimeTable bestSolution = null;
    /**
     * Evaluate the given population
     * @param pop
     * @return evaluated population
     */
    public static Population evolvePopulation(Population pop) {

        /*during mutation we can lose the best solution so every mutation
         operation we hold the best solution*/
        bestSolution = pop.getFittest();
        
        for (int i = 0, size = pop.getPopulationSize(); i < size; i++) {
            mutate(pop.getTimeTable(i));
            TimeTable temp = pop.getFittest();
            if(bestSolution.getFitness()>temp.getFitness())
                bestSolution = temp;
        }

        return pop;
    }

    private static void mutate(TimeTable fittest) {
        Info info1, info2;
        int newD, newS, errorBeforeMutation, errorAfterMutation;
        for (int d = 1; d <= R.MAX_DAY; d++) {
            for (int s = 1; s <= R.MAX_SLOT; s++) {
                for (int r = 0, len = R.ALLR.size(); r < len; r++) {
                    newD = rd.nextInt(5) + 1;
                    newS = rd.nextInt(5) + 1;

                    info1 = fittest.getInfo(d, s, r);
                    info2 = fittest.getInfo(newD, newS, r);

                    if (info1 != null && info2 == null) {
                        if(info1.getError()==0)continue;
                        Teacher t = fittest.getTeacher(info1.getTeacherName());
                        Course c = fittest.getCourse(info1.getCourseName(), info1.getGroup());
                        if(c.isLectureTimeFixed()) continue;
                        if ((t != null && t.changeTeacherHour(d, s, newD, newS, info1.getHour())) || (t == null)) {
                            info1.setDay(newD);
                            info1.setSlot(newS);
                            c.changeCourseTime(newD, newS, info1.getSection());
                            fittest.setInfo(newD, newS, r, info1);
                            fittest.setInfo(d, s, r, null);
                            fittest.setFitness(0);
                            fittest.getFitness();
                        }
                    }
                }
            }
        }
    }
    
}
