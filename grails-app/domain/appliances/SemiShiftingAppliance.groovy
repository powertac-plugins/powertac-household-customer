package appliances

import java.util.Vector;

class SemiShiftingAppliance extends Appliance {

	Vector days = new Vector()
	
	def createDailyOperationVector(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector(96)
		
				// This is a loop.
				for (int i = 0;i < 96;i++) {
		
					// This is a task.
					v.add(false)
		
				}
		
		
				// This is an agent decision.
			 	if (days.contains(weekday) && ((this instanceof Dryer) == false)) {
		
					// This is a task.
					Random r = new Random()
					int quarter = 1 + r.nextInt(84)
					v.set(quarter-1,true)
					// This is a task.
					returnValue = v
		
				} else  {
		
					// This is a task.
					returnValue = v
		
				}
				// Return the results.
				return returnValue
		
	}	
	
	def createWeeklyOperationVector(int times) {
		
				// This is a task.
				fillDays(times)
		
				// This is a loop.
				for (int i=0;i < 7;i++) {
		
					// This is a task.
					operationVector.add(createDailyOperationVector(i))
		
				}
		
	}
	
	def fillWeeklyFunction() {
		
		
				// This is a loop.
				for (int i = 0;i < 7; i++) {
		
					// This is a task.
					fillDailyFunction(i)
		
				}
		
	}
	
	def fillDays(int times) {
		
				// Define the return value variable.
				def returnValue

		
				// This is a loop.
				for (int i=0; i < times; i++) {
		
					// This is a task.
					Random r = new Random()
					int day = r.nextInt(6)
					ListIterator iter = days.listIterator();
		
					// This is a loop.
					while (iter.hasNext()) {
		
						// This is a task.
						int temp = (int)iter.next()
		
						// This is an agent decision.
						if (day == temp) {
		
							// This is a task.
							day = day + 1
							iter = days.listIterator();
		
						} else  {
		
		
						}
		
					}
		
					// This is a task.
					days.add(day)
					java.util.Collections.sort(days);
		
				}
		
				// This is a task.
				java.util.Collections.sort(days);
				ListIterator iter = days.listIterator();
				// Return the results.
				return returnValue
		
	}
	
	
    static constraints = {
    }
}
