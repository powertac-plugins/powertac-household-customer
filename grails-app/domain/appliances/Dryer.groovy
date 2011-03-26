package appliances

import java.util.HashMap;

class Dryer extends SemiShiftingAppliance{

	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				name = "Dryer"
				saturation = (float)hm.get("DryerSaturation")
				Random rand = new Random()
				// This is a task.
				consumptionShare = (float) (0.01 * (0.4 * rand.nextGaussian() + 2.5))
				baseLoadShare = 0.015
				power = (int) (235 * rand.nextGaussian() + 1410)
				cycleDuration = 7
				times = (int)hm.get("DishwasherWeeklyTimes")
				// This is a task.
				od = true
				inUse = false
				probabilitySeason = fillSeason(25,38,37)
				probabilityWeekday =fillDay(14,14,16)
				createWeeklyOperationVector((int)(times + applianceOf.members.size() / 2))
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
				for (int l = 0;l < 96; l++) {
		
					// This is a task.
					loadVector.add(0)
					dailyOperation.add(false)
		
				}
		
				// This is a task.
				int start = washingEnds(weekday)
				//System.out.println("Starting at " + start)
		
				// This is an agent decision.
				if (start > 0) {
		
		
					// This is a loop.
					for (int i = start;i < 96;i++) {
		
		
						// This is an agent decision.
						if (applianceOf.isEmpty(i+1) == false) {
		
							// This is a task.
							operation.set(i, true)
		
							// This is a loop.
							for (int j = i;j < i + 3;j++) {
		
								// This is a task.
								loadVector.set(j,power)
								dailyOperation.set(j,true)
		
							}
		
		
							// This is a loop.
							for (int k = i+3;k < i+6;k++) {
		
								// This is a task.
								loadVector.set(k,power - 250*(k - (i+2)))
								dailyOperation.set(k,true)
		
							}
		
							// This is a task.
							i = 96
		
						} else  {
		
		
						}
		
					}
		
					// This is a task.
					weeklyLoadVector.add(loadVector)
					weeklyOperation.add(dailyOperation)
					operationVector.set(weekday, operation)
		
				} else  {
		
					// This is a task.
					weeklyLoadVector.add(loadVector)
					weeklyOperation.add(dailyOperation)
					operationVector.set(weekday, operation)
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def washingEnds(int weekday) {
		
				// Define the return value variable.
				def returnValue
		

				// This is a task.
				Vector v = new Vector()
				int start = 0
				
		
				// This is a loop.
				this.applianceOf.appliances.each {
		
					// This is a task.
					Object o = (Object) it
		
					// This is an agent decision.
					if (o instanceof WashingMachine) {
		
						// This is a task.
						v = o.getWeeklyOperation().get(weekday)
		
					} else  {
		
		
					}
		
				}
		
		
				// This is a loop.
				for (int i = 95;i > 0;i--) {
		
		
					// This is an agent decision.
					if (v.get(i) == true) {
		
						// This is a task.
						start = i+1
						i = 0
		
					} else  {
		
		
					}
		
				}
		
				// This is a task.
				returnValue = start
				// Return the results.
				return returnValue
		
	}
	
	def showStatus() {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is a task.
				System.out.println("Name = " + name)
				System.out.println("Saturation = " + saturation)
				System.out.println("Consumption Share = " + consumptionShare)
				System.out.println("Base Load Share = " + baseLoadShare)
				System.out.println("Power = " + power)
				// This is a task.
				System.out.println("Cycle Duration = "+ cycleDuration)
				System.out.println("Occupancy Dependence = "+ od)
				System.out.println("In Use = " + inUse)
				// This is a task.
				Set set = probabilitySeason.entrySet();
				Iterator it = set.iterator();
				System.out.println("Probability Season = ")
		
				// This is a loop.
				while (it.hasNext()) {
		
					// This is a task.
					Map.Entry me = (Map.Entry)it.next();
					System.out.println(me.getKey() + " : " + me.getValue() );
		
				}
		
				// This is a task.
				set = probabilityWeekday.entrySet();
				it = set.iterator();
				System.out.println("Probability Weekday = ")
		
				// This is a loop.
				while (it.hasNext()) {
		
					// This is a task.
					Map.Entry me = (Map.Entry)it.next();
					System.out.println(me.getKey() + " : " + me.getValue() );
		
				}
		
				// This is a task.
				ListIterator iter = days.listIterator();
				System.out.println("Days Vector = ")
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println("Day  " + iter.next())
		
				}
		
				// This is a task.
				iter = operationVector.listIterator();
				System.out.println("Operation Vector = ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					iter =operationVector.get(i).listIterator();
		
					// This is a loop.
					for (int j = 0;j < 96; j++) {
		
						// This is a task.
						System.out.println("Quarter : " + (j+1) + "  " + iter.next())
		
					}
		
		
				}
		
				// This is a task.
				System.out.println("Weekly Operation Vector and Load = ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					 iter = weeklyOperation.get(i).listIterator();
					ListIterator iter2 = weeklyLoadVector.get(i).listIterator();
		
					// This is a loop.
					for (int j = 0;j < 96; j++) {
		
						// This is a task.
						System.out.println("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())
		
					}
		
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	def fillDays(int times) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				
				boolean flag = true
		
				// This is a loop.
				this.applianceOf.appliances.each {
		
					// This is a task.
					Object o = it
		
					// This is an agent decision.
					if (o instanceof WashingMachine) {
		
						// This is a task.
						days = o.getDays()
						flag = false
		
					} else  {
		
		
					}
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				createWeeklyOperationVector((int)(times + applianceOf.members.size() / 2))
				fillWeeklyFunction()
				System.out.println("Dryer refreshed")
				// Return the results.
				return returnValue
		
	}
	
    static constraints = {
    }
}
