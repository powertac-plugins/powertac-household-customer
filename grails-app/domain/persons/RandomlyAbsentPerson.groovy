package persons

import java.util.HashMap;
import java.util.Vector;

class RandomlyAbsentPerson extends WorkingPerson {

	
	def initialize(String AgentName, HashMap hm, Vector publicVacationVector) {
		
				// This is a task.
				float sicknessMean = ((float)hm.get("SicknessMean"))
				float sicknessDev = ((float)hm.get("SicknessDev"))
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float RALeisure = ((int)hm.get("RALeisure"))
				// This is a task.
				name = AgentName
				status = Status.Normal
				// This is a task.
				sicknessVector = createSicknessVector(sicknessMean,sicknessDev)
				pVacationVector = publicVacationVector
				// This is a task.
				Random rand = new Random();
				int x = (int) (rand.nextGaussian() + RALeisure)
				leisureVector = createLeisureVector(x)
				// This is a task.
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
				// This is a task.
				int work = workingDaysRandomizer(hm)
				workingDays = createWorkingDaysVector(work)
				workingStartHour = createWorkingStartHour()
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
	
	def createWorkingStartHour() {
		
				// Define the return value variable.
				def returnValue

				// This is a task.
				Random r = new Random()
				int x = r.nextInt(3)
				returnValue = (x * 8 * 4) + 1
				// Return the results.
				return returnValue
		
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
		
						// This is an agent decision.
						if (workingStartHour == 1) {
		
							// This is a task.
							Random rand = new Random()
							int startq = rand.nextInt(47 - start) + (start + 33)
						
		
							// This is a loop.
							for (int i = startq;i < startq + leisureDuration;i++) {
		
								// This is a task.
								st = Status.Leisure
								dailyRoutine.set(i-1,st)
		
							}
		
		
						} else  {
		
		
							// This is an agent decision.
							if (workingStartHour == 33) {
		
								// This is a task.
								Random r = new Random()
								int startq = start + r.nextInt(80 - start)
		
								// This is a loop.
								for (int i = startq;i < startq + leisureDuration;i++) {
		
									// This is a task.
									st = Status.Leisure
									dailyRoutine.set(i-1,st)
		
								}
		
		
							} else  {
		
								// This is a task.
								Random r = new Random()
								int startq = 33 + r.nextInt(workingStartHour - 45)
		
								// This is a loop.
								for (int i = startq;i < startq +leisureDuration;i++) {
		
									// This is a task.
									st = Status.Leisure
									dailyRoutine.set(i-1,st)
		
								}
		
		
							}
		
						}
		
					} else  {
		
		
					}
		
				}
		
		
	}
	
	def fillWork() {

		
				// This is a task.
				Status st
		
				// This is an agent decision.
				if (workingStartHour == 1) {
		
		
					// This is a loop.
					for (int i = 1;i < workingDuration + 1;i++) {
		
						// This is a task.
						st = Status.Working
						dailyRoutine.set(i-1,st)
		
					}
		
		
					// This is a loop.
					for (int i = workingDuration + 1;i < workingDuration + 33 ;i++) {
		
						// This is a task.
						st = Status.Sleeping
						dailyRoutine.set(i-1,st)
		
					}
		
		
					// This is a loop.
					for (int i = workingDuration + 33;i < 97;i++) {
		
						// This is a task.
						st = Status.Normal
						dailyRoutine.set(i-1,st)
		
					}
		
		
				} else  {
		
		
					// This is an agent decision.
					if (workingStartHour == 33) {
		
		
						// This is a loop.
						for (int i = 1;i < 25;i++) {
		
							// This is a task.
							st = Status.Sleeping
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is a loop.
						for (int i = 25;i < 33;i++) {
		
							// This is a task.
							st = Status.Normal
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is a loop.
						for (int i = 33;i < workingDuration + 33 ;i++) {
		
							// This is a task.
							st = Status.Working
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is a loop.
						for (int i = workingDuration + 33;i < 91;i++) {
		
							// This is a task.
							st = Status.Normal
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is a loop.
						for (int i = 91;i < 97;i++) {
		
							// This is a task.
							st = Status.Sleeping
							dailyRoutine.set(i-1,st)
		
						}
		
		
					} else  {
		
		
						// This is a loop.
						for (int i = 1;i < 29;i++) {
		
							// This is a task.
							st = Status.Sleeping
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is a loop.
						for (int i = 33;i < 65 ;i++) {
		
							// This is a task.
							st = Status.Normal
							dailyRoutine.set(i-1,st)
		
						}
		
		
						// This is an agent decision.
						if (workingDuration > 32) {
		
		
							// This is a loop.
							for (int i = 65;i < 97;i++) {
		
								// This is a task.
								st = Status.Working
								dailyRoutine.set(i-1,st)
		
							}
		
		
						} else  {
		
		
							// This is a loop.
							for (int i = 65;i < 65 + workingDuration;i++) {
		
								// This is a task.
								st = Status.Working
								dailyRoutine.set(i-1,st)
		
							}
		
		
							// This is a loop.
							for (int i =  65 + workingDuration;i < 97;i++) {
		
								// This is a task.
								st = Status.Sleeping
								dailyRoutine.set(i-1,st)
		
							}
		
		
						}
		
					}
		
				}

	}
	
	def refresh(HashMap hm) {
		
				// This is a task.
				float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
				float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
				float RALeisure = ((int)hm.get("RALeisure"))
				float vacationAbsence = ((float)hm.get("VacationAbsence"))
				// This is a task.
				int work = workingDaysRandomizer(hm)
				workingDays = createWorkingDaysVector(work)
				workingStartHour = createWorkingStartHour()
				// This is a task.
				Random rand = new Random();
				int x = (int) (rand.nextGaussian() + RALeisure)
				leisureDuration = (int) (leisureDurationDev * rand.nextGaussian() + leisureDurationMean)
				leisureVector = createLeisureVector(x)
				weeklyRoutine = fillWeeklyRoutine(vacationAbsence)

		
	}
	
    static constraints = {
    }
}
