package appliances

import java.util.HashMap;

class Lights extends NotShiftingAppliance{

	
	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
		    	// This is a task.
				name = "Lights"
				saturation = 1.0
				Random rand = new Random()
				// This is a task.
				consumptionShare = (float) (0.01 * (1.3 * rand.nextGaussian() + 8))
				baseLoadShare = 0
				power = (int) (58 * rand.nextGaussian() + 350)
				cycleDuration = 1
				times = (float)hm.get("LightsDailyTimes")
				// This is a task.
				od = true
				inUse = false
				probabilitySeason = fillSeason(23,39,38)
				probabilityWeekday = fillDay(12,15,13)
				createWeeklyOperationVector(times + applianceOf.members.size())
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
							if (applianceOf.isEmpty(i+1) == false) {
		
								// This is a task.
								loadVector.add(power)
								dailyOperation.add(true)
								counter--
		
								// This is an agent decision.
								if (counter < 0) {
		
									// This is a task.
									flag = false
		
								} else  {
		
		
								}
		
							} else  {
		
								// This is a task.
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
				createWeeklyOperationVector(times + applianceOf.members.size())
				fillWeeklyFunction()
				System.out.println("Lights refreshed")
				// Return the results.
				return returnValue
		
	}
	
    static constraints = {
    }
}
