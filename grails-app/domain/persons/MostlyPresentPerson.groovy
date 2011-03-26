package persons

import java.util.HashMap;
import java.util.Vector;
import java.math.*;

class MostlyPresentPerson extends Person {

	def initialize(String AgentName, HashMap hm, Vector publicVacationVector) {
		
				// This is a task.
				float sicknessMean = ((float)hm.get("SicknessMean"))
				float sicknessDev = ((float)hm.get("SicknessDev"))
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float MPLeisure = ((int)hm.get("MPLeisure"))
				// This is a task.
				name = AgentName
				status = Status.Normal
				Random rand = new Random()
				// This is a task.
				sicknessVector = createSicknessVector(sicknessMean, sicknessDev)
				pVacationVector = publicVacationVector
				// This is a task.
				int x = (int) (rand.nextGaussian() + MPLeisure)
				leisureVector = createLeisureVector(x)
				// This is a task.
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
	
	}
	
	def showStatus() {
		
				
				// This is a task.
				System.out.println("Name = " + name)
				//System.out.println("Member Of = " + getMemberOf().getName());
				System.out.println("Sickness Days = ")
				ListIterator iter = sicknessVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Leisure Days of Week = ");
				iter = leisureVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Leisure Duration = " + leisureDuration);
				System.out.println("Public Vacation of Year = ");
				iter = pVacationVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
				System.out.println("Weekly Routine Length : " + weeklyRoutine.size())
				// This is a task.
				System.out.println("Weekly Routine : ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					iter = weeklyRoutine.get(i).listIterator();
		
					//
					for (int j =0;j < 96;j++) {
		
						// This is a task.
						System.out.println("Quarter : " + (j+1) + " Status : " + iter.next())
		
					}
		
		
				}
		
		
	}
	
	def refresh(HashMap hm) {
		
				
				// This is a task.
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float MPLeisure = ((int)hm.get("MPLeisure"))
				float vacationAbsence = ((float)hm.get("VacationAbsence"))
				// This is a task.
				Random rand = new Random()
				int x = (int) (rand.nextGaussian() + MPLeisure)
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
				leisureVector = createLeisureVector(x)
				weeklyRoutine = fillWeeklyRoutine(vacationAbsence)
				
		
	}
	
    static constraints = {
    }
}
