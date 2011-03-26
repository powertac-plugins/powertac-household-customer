package appliances

import java.util.HashMap;

class SpaceHeater extends FullyShiftingAppliance{

	float percentage
	
	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				name = "SpaceHeater"
				saturation = (float)hm.get("SpaceHeaterSaturation")
				float percentage = (float)hm.get("SpaceHeaterPercentage")
				Random rand = new Random()
				// This is a task.
				consumptionShare = (float) (0.01 * (2.8 * rand.nextGaussian() + 17))
				baseLoadShare = 0
				power = (int) (300 * rand.nextGaussian() + 7000)
				cycleDuration = 14
				// This is a task.
				od = false
				inUse = false
				probabilitySeason = fillSeason(21,42,37)
				probabilityWeekday = fillDay(15,14,15)
				// Return the results.
				return returnValue
		
	}
	
	def fillDailyFunction(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				loadVector = new Vector()
				dailyOperation = new Vector()
		
				// This is an agent decision.
				Random r = new Random()
				if (applianceOf.isOnVacation(1) || r.nextFloat() > percentage) {
		
		
					// This is a loop.
					for (int i = 0;i < 96;i++) {
		
						// This is a task.
						loadVector.add(0)
						dailyOperation.add(false)
		
					}
		
					// This is a task.
					weeklyLoadVector.add(loadVector)
					weeklyOperation.add(dailyOperation)
					operationVector.add(dailyOperation)
		
				} else  {
		
		
					// This is a loop.
					for (int i = 0;i < 96;i++) {
		
						// This is a task.
						loadVector.add(0)
						dailyOperation.add(true)
		
					}
		
		
					// This is a loop.
					for (int i = 0;i < 9;i++) {
		
						// This is a task.
						loadVector.set(i,power)
		
					}
		
		
					// This is a loop.
					for (int i = 9;i < 16;i++) {
		
						// This is a task.
						loadVector.set(i,power - 750*(i - 8))
		
					}
		
		
					// This is a loop.
					for (int i = 16;i < 86; i++) {
		
						// This is a task.
						loadVector.set(i,power - 750*8)
		
					}
		
		
					// This is a loop.
					for (int i=86;i < 90;i++) {
		
						// This is a task.
						loadVector.set(i, power - 1500*(89 - i))
		
					}
		
		
					// This is a loop.
					for (int i= 90;i < 96;i++) {
		
						// This is a task.
						loadVector.set(i,power)
		
					}
		
					// This is a task.
					weeklyLoadVector.add(loadVector)
					weeklyOperation.add(dailyOperation)
					operationVector.add(dailyOperation)
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue

				// This is a task.
				fillWeeklyFunction()
				System.out.println("Space Heater refreshed")
				// Return the results.
				return returnValue
		
	}
	
    static constraints = {
    }
}
