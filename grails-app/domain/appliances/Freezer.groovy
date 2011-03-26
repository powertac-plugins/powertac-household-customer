package appliances

import java.util.HashMap;

class Freezer extends FullyShiftingAppliance{

	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is a task.
				name = "Freezer"
				saturation = (float)hm.get("FreezerSaturation")
				Random rand = new Random()
				// This is a task.
				consumptionShare = (float) (0.01 * (1.2 * rand.nextGaussian() + 7.1))
				baseLoadShare = 0
				power = (int) (18 * rand.nextGaussian() + 106)
				cycleDuration = 1
				// This is a task.
				od = false
				inUse = false
				probabilitySeason = fillSeason(35,30,35)
				probabilityWeekday = fillDay(14,14,16)
				// Return the results.
				return returnValue
		
	}
	
	def fillDailyFunction(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is a task.
				loadVector = new Vector()
				dailyOperation = new Vector()
		
				// This is a loop.
				for (int i = 0;i < 96;i++) {
		
		
					// This is an agent decision.
					if (i % 2 == 0) {
		
						// This is a task.
						loadVector.add(power)
						dailyOperation.add(true)
		
					} else  {
		
						// This is a task.
						loadVector.add(0)
						dailyOperation.add(false)
		
					}
		
				}
		
				// This is a task.
				weeklyLoadVector.add(loadVector)
				weeklyOperation.add(dailyOperation)
				operationVector.add(dailyOperation)
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				fillWeeklyFunction()
				System.out.println("Freezer refreshed")
				// Return the results.
				return returnValue
		
	}
	
    static constraints = {
    }
}
