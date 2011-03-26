package appliances

import java.util.HashMap;

class CirculationPump extends NotShiftingAppliance {

	BigDecimal percentage
	
	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				name = "CirculationPump"
				saturation = (float)hm.get("CirculationPumpSaturation")
				percentage = ((float)hm.get("CirculationPumpPercentage"))
				Random rand = new Random()
				// This is a task.
				consumptionShare = (float) (0.01 * (rand.nextGaussian() + 6))
				baseLoadShare = 0.07
				power = (int) (15 * rand.nextGaussian() + 90)
				cycleDuration = 65
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
				Vector v = new Vector()
		
				// This is a loop.
				for (int i = 0;i < 96;i++) {
		
		
					// This is an agent decision.
					Random r = new Random()
					if (applianceOf.isEmpty(i+1) == false && (r.nextFloat() > percentage)) {
		
						// This is a task.
						loadVector.add(power)
						dailyOperation.add(true)
						v.add(true)
		
					} else  {
		
						// This is a task.
						loadVector.add(0)
						dailyOperation.add(false)
						v.add(false)
		
					}
		
				}
		
				// This is a task.
				weeklyLoadVector.add(loadVector)
				weeklyOperation.add(dailyOperation)
				operationVector.add(v)
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
	
		
				// This is a task.
				fillWeeklyFunction()
				System.out.println("Circulation Pump refreshed")
				// Return the results.
				return returnValue
		
	}
	
	
    static constraints = {
		
		name()
		saturation()
		cycleDuration()
		od()
		inUse()
		currentLoad()
		
    }
}
