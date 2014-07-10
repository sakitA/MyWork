/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package GA;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import type.Course;
import type.Teacher;
import util.Logger;
import util.R;
import util.ShowMessage;


/**
 * Groups of all individuals. Individuals is any possible solution. 
 * In GAs individual represents chromosome.
 * @author Sakit
 */
public class Population implements Serializable {

    private final TimeTable[] timeTable;

    /**
     * Constructor to initialize population within populationSize
     * @param populationSize how many population will be created
     * @param initialize if true, then population created randomly, otherwise we apply only mutation.
     */
    public Population(int populationSize, boolean initialize) {
        timeTable = new TimeTable[populationSize];

        if (initialize) {
            for (int i = 0; i < populationSize; i++) {
                
                TimeTable tt = new TimeTable();

                Collection c = R.TEACHERS.values();
                Iterator it = c.iterator();

                try {
                    while (it.hasNext()) {
                        tt.addTeacher(((Teacher) it.next()).clone());
                    }

                    for (Course cc : R.ALLC) {
                        tt.addCourse(cc.clone());
                    }
                } catch (CloneNotSupportedException ex) {
                    Logger.LOGS(ex.getMessage());
                    ShowMessage.showError(ex);
                }
                tt.generateTimeTable();
                saveTimeTable(tt, i);
            }
        }
    }

    public void saveTimeTable(TimeTable timeTable, int index) {
        this.timeTable[index] = timeTable;
    }

    /**
     * 
     * @param index
     * @return time table which is the found solution.
     */
    public TimeTable getTimeTable(int index) {
        return timeTable[index];
    }

    /**
     * 
     * @return best solution which less constraints violated
     */
    public TimeTable getFittest() {
        TimeTable bestFittest = timeTable[0];

        for (int i = 1, len = getPopulationSize(); i < len; i++) {
            if (bestFittest.getFitness() > timeTable[i].getFitness()) {
                bestFittest = timeTable[i];
            }
        }
        return bestFittest;
    }

    /**
     * 
     * @return how many population exist.
     */
    public int getPopulationSize() {
        return timeTable.length;
    }

    /**
     * 
     * @return all found solution
     */
    public TimeTable[] getTimeTable() {
        return timeTable;
    }
}
