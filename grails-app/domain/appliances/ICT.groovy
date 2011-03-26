package appliances

import java.util.HashMap;

class ICT extends NotShiftingAppliance{

	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				name = "ICT"
				saturation = (float)hm.get("ICTSaturation")
				Random rand = new Random()
				// Set basic attributes
				consumptionShare = (float) (0.01 * (0.8 * rand.nextGaussian() + 5))
				baseLoadShare =0.6
				power = (int) (25 * rand.nextGaussian() + 150)
				cycleDuration = 1
				times = (float)hm.get("ICTDailyTimes")
				// This is a task.
				od = true
				inUse = false
				probabilitySeason = fillSeason(25,38,37)
				probabilityWeekday = fillDay(14,14,16)
				createWeeklyOperationVector(cycleDuration + applianceOf.members.size())
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
				System.out.println("ICT refreshed")
				// Return the results.
				return returnValue
		
	}
		
	
    static constraints = {
    }
}
