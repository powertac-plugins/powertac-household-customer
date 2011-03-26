package appliances

import java.util.HashMap;

class Stove extends NotShiftingAppliance{

	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				name = "Stove"
				saturation = (float)hm.get("StoveSaturation")
				Random rand = new Random()
				times = (int)hm.get("StoveDailyTimes")
				// This is a task.
				consumptionShare = (float) (0.01 * (1.4 * rand.nextGaussian() + 8.1))
				baseLoadShare =0
				power = (int) (307 * rand.nextGaussian() + 1840)
				cycleDuration = 2
				// This is a task.
				od = true
				inUse = false
				probabilitySeason = fillSeason(29,36,35)
				probabilityWeekday = fillDay(17,13,18)
				createWeeklyOperationVector(times)
				// Return the results.
				return returnValue
		
	}
	
	def fillDailyFunction(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is a task.
				loadVector = new Vector()
				dailyOperation = new Vector()
				Vector operation = operationVector.get(weekday)
		
				// This is a loop.
				for (int i = 0;i < 96;i++) {
		
		
					// This is an agent decision.
					if (operation.get(i) == true) {
		
						// This is a task.
						boolean flag = true
						int counter = 0
		
						// This is a loop.
						while ((flag) && (i < 96) && (counter >= 0)) {
		
		
							// This is an agent decision.
							if (applianceOf.isEmpty(i+1) == false && applianceOf.isEmpty(i+2) == false) {
		
								// This is a task.
								loadVector.add(power)
								dailyOperation.add(true)
								loadVector.add(power)
								dailyOperation.add(true)
								// This is a task.
								counter--
								i = dailyOperation.size() - 1
		
								// This is an agent decision.
								if (counter < 0) {
		
									// This is a task.
									flag = false
		
								} else  {
		
		
								}
		
							} else  {
		
								// Set load and operation accordingly
								loadVector.add(0)
								dailyOperation.add(false)
								i++
		
								// This is an agent decision.
								if (i < 96 && operation.get(i) == true) {
		
									// This is a task.
									counter++
		
								} else  {
		
		
								}
		
							}
		
						}
		
		
					} else  {
		
						// This is a task.
						loadVector.add(0)
						dailyOperation.add(false)
		
					}
		
				}
		
				// This is a task.
				weeklyLoadVector.add(loadVector)
				weeklyOperation.add(dailyOperation)
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				createWeeklyOperationVector(times)
				fillWeeklyFunction()
				System.out.println("Stove refreshed")
				// Return the results.
				return returnValue
		
	}
	
    static constraints = {
    }
}
