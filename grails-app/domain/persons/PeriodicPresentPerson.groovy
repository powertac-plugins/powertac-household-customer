package persons

import java.util.HashMap;
import java.util.Vector;

class PeriodicPresentPerson extends WorkingPerson {

	def initialize(String AgentName, HashMap hm, Vector publicVacationVector) {
		
				// This is a task.
				float sicknessMean = ((float)hm.get("SicknessMean"))
				float sicknessDev = ((float)hm.get("SicknessDev"))
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float PPLeisure = ((int)hm.get("PPLeisure"))
				// This is a task.
				name = AgentName
				status = Status.Normal
				// This is a task.
				sicknessVector = createSicknessVector(sicknessMean,sicknessDev)
				pVacationVector = publicVacationVector
				// This is a task.
				Random rand = new Random();
				int x = (int) (rand.nextGaussian() + PPLeisure)
				leisureVector = createLeisureVector(x)
				// This is a task.
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
				// This is a task.
				workingStartHour = 29
				int work = workingDaysRandomizer(hm)
				workingDays = createWorkingDaysVector(work)
				// This is a task.
				int workingDurationMean = ((int)hm.get("WorkingDurationMean"))
				int workingDurationDev = ((int)hm.get("WorkingDurationDev"))
				workingDuration = (int) (workingDurationDev * rand.nextGaussian() + workingDurationMean)
				// This is a task.
				int vacationDurationMean = ((int)hm.get("VacationDurationMean"))
				int vacationDurationDev = ((int)hm.get("VacationDurationDev"))
				vacationDuration = (int) (vacationDurationDev * rand.nextGaussian() + vacationDurationMean)
				// This is a task.
				vacationVector = createVacationVector(vacationDuration)
	}
	
	
	def addLeisureWorking(int weekday) {
		

				// This is a task.
				ListIterator iter = leisureVector.listIterator();
				Status st
		
				// This is a loop.
				while (iter.hasNext()) {
		
		
					// This is an agent decision.
					if (iter.next() == weekday) {
		
						// This is a task.
						int start = workingStartHour + workingDuration
						Random r = new Random()
						//System.out.println("Start = " + workingStartHour + "+" + workingDuration)
						int startq = r.nextInt(Math.max(0,75 - start)) + start
		
						// This is a loop.
						for (int i = startq;i < startq +leisureDuration;i++) {
		
							// This is a task.
							st = Status.Leisure
							dailyRoutine.set(i-1,st)
		
						}
		
		
					} else  {
		
		
					}
		
				}
		
	}
	
	def fillWork() {
		

				// This is a task.
				Status st
		
				// This is a loop.
				for (int i = workingStartHour;i < workingStartHour + workingDuration;i++) {
		
					// This is a task.
					st = Status.Working
					dailyRoutine.set(i-1,st)
		
				}
		
	}
	
	def refresh(HashMap hm) {

		
				// This is a task.
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float PPLeisure = ((int)hm.get("PPLeisure"))
				float vacationAbsence = ((float)hm.get("VacationAbsence"))
				// This is a task.
				Random rand = new Random();
				int x = (int) (rand.nextGaussian() + PPLeisure)
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
				leisureVector = createLeisureVector(x)
				weeklyRoutine = fillWeeklyRoutine(vacationAbsence)

		
			}
	
	
    static constraints = {
    }
}
