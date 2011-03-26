package persons

import java.util.Vector;
import consumers.*


class Person {

	String name
	Status status
	Vector pVacationVector = new Vector()
	Vector sicknessVector = new Vector()
	int leisureDuration = 0
	Vector dailyRoutine = new Vector()
	Vector leisureVector = new Vector()
	Vector weeklyRoutine = new Vector()
	
	static belongsTo = [memberOf:Household]
	
	def isSleeping() {
		
		if (status == Status.Sleeping) {
			return 1
		}
		else return 0
	}
	
	def isAtWork() {
		
		if (status == Status.Working) {
			return 1
		}
		else return 0
	}
	
	def isLeisure() {
		
		if (status == Status.Leisure) {
			return 1
		}
		else return 0
	}
	
	def isVacation() {
		
		if (status == Status.Vacation) {
			return 1
		}
		else return 0
	}
	
	def isSick() {
		
		if (status == Status.Sick) {
			return 1
		}
		else return 0
	}
	
	
	def createLeisureVector(int counter) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector()
		
				// This is a loop.
				for (int i = 0; i < counter; i++) {
		
					// This is a task.
					Random r = new Random()
					int day = r.nextInt(7)
					v.add(day)
		
				}
		
				// This is a task.
				java.util.Collections.sort(v);
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def fillDailyRoutine(int day, float vacationAbsence) {
				
				// This is a task.
				Vector v = new Vector(96)
				int weekday = day % 7
				setDailyRoutine(new Vector())
				Status st
				//System.out.println("Day = " + day + " WeekDay = " + weekday)
		
				// This is an agent decision.
				if (sicknessVector.contains(day)) {
		
					// This is a task.
					fillSick()
		
				} else  {
		
		
					// This is an agent decision.
					if (pVacationVector.contains(day) || (this instanceof WorkingPerson && vacationVector.contains(day))) {
		
		
						// This is an agent decision.
						Random r = new Random()
						if (r.nextFloat() < vacationAbsence ) {
		
		
							// This is a loop.
							for (int i = 1;i < 97; i++) {
		
								// This is a task.
								st = Status.Vacation
								dailyRoutine.add(st)
		
							}
		
		
						} else  {
		
							// This is a task.
							normalFill()
							addLeisure(weekday)
		
						}
		
					} else  {
		
						// This is a task.
						normalFill()
		
						// This is an agent decision.
						if (this instanceof WorkingPerson) {
		
							// This is a task.
							int index = workingDays.indexOf(weekday)
		
							// This is an agent decision.
							if (index > -1) {
		
								// This is a task.
								fillWork()
								addLeisureWorking(weekday)
		
							} else  {
		
								// This is a task.
								addLeisure(weekday)
		
							}
		
						} else  {
		
							// This is a task.
							addLeisure(weekday)
		
						}
		
					}
		
				}
				
				
	}
	
	def createSicknessVector(float mean, float dev) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				Random rand = new Random()
				int days = (int) (dev * rand.nextGaussian() + mean)
				Vector v = new Vector(days)
		
				// This is a loop.
				for (int i = 0; i < days; i++) {
		
					// This is a task.
					Random r = new Random();
					int x = r.nextInt(364) + 1;
					ListIterator iter = v.listIterator();
		
					// This is a loop.
					while (iter.hasNext()) {
		
						// This is a task.
						int temp = (int)iter.next()
		
						// This is an agent decision.
						if (x == temp) {
		
							// This is a task.
							x = x + 1
							iter = v.listIterator();
		
						} else  {
		
		
						}
		
					}
		
					// This is a task.
					v.add(x)
		
				}
		
				// This is a task.
				java.util.Collections.sort(v);
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def showStatus() {	}
	
	def addLeisure(int weekday) {
		
		
				// This is a task.
				ListIterator iter = leisureVector.listIterator();
				Status st
		
				// This is a loop.
				while (iter.hasNext()) {
		
		
					// This is an agent decision.
					if (iter.next() == weekday) {
		
						// This is a task.
						Random r = new Random()
						int start = 29 + r.nextInt(46)
		
						// This is a loop.
						for (int i = start;i < start +leisureDuration;i++) {
		
							// This is a task.
							st = Status.Leisure
							dailyRoutine.set(i-1,st)
		
						}
		
		
					} else  {
		
		
					}
		
				}
		
		
	}
	
	def normalFill() {
		

				// This is a task.
				Status st
		
				// This is a loop.
				for (int i = 1;i < 25;i++) {
		
					// This is a task.
					st = Status.Sleeping
					dailyRoutine.add(st)
		
				}
		
		
				// This is a loop.
				for (int i = 25;i < 91;i++) {
		
					// This is a task.
					st = Status.Normal
					dailyRoutine.add(st)
		
				}
		
		
				// This is a loop.
				for (int i = 91;i < 97;i++) {
		
					// This is a task.
					st = Status.Sleeping
					dailyRoutine.add(st)
		
				}
		
		
	}
	
	def fillSick() {
		
		
				// This is a task.
				Status st
		
				// This is a loop.
				for (int i = 1;i < 25;i++) {
		
					// This is a task.
					st = Status.Sleeping
					dailyRoutine.add(st)
		
				}
		
		
				// This is a loop.
				for (int i = 25;i < 91; i++) {
		
					// This is a task.
					st = Status.Sick
					dailyRoutine.add(st)
		
				}
		
		
				// This is a loop.
				for (int i = 91;i < 97;i++) {
		
					// This is a task.
					st = Status.Sleeping
					dailyRoutine.add(st)
		
				}
		
	}
	
	def fillWeeklyRoutine(float vacationAbsence) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector()
		
				// This is a loop.
				for (int i = 0;i < 7;i++) {
		
					// This is a task.
					fillDailyRoutine(i,vacationAbsence)
					v.add(dailyRoutine)
					
				}
		
				// This is a task.
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {}
	
    static constraints = {
		
		name()
		// Household()
		status()
		
    }
	
	static mapping = {
		sort "name"
		}
	
	String toString(){
		"${name}, ${Household} (${status})"
	}
	
}
