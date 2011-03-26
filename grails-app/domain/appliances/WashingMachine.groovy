package appliances

import java.util.HashMap;

class WashingMachine extends SemiShiftingAppliance{

	Mode mode = Mode.One
	Reaction reaction = Reaction.Strong
	
	
	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is a task.
				name = "Washing Machine"
				saturation = (float)hm.get("WashingMachineSaturation")
				Random rand = new Random()
				times = (int)hm.get("WashingMachineWeeklyTimes")
				// This is a task.
				consumptionShare = (float) (0.01 * (0.6 * rand.nextGaussian() + 3.5))
				baseLoadShare = 0.02
				power = (int) (100 * rand.nextGaussian() + 600)
				cycleDuration = 9
				// This is a task.
				od = true
				inUse = false
				probabilitySeason = fillSeason(30,35,35)
				probabilityWeekday = fillDay(14,14,16)
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
		
		
				// This is a loop.
				for (int i = 0;i < 96;i++) {
		
		
					// This is an agent decision.
					if (operation.get(i) == true) {
		
						// This is a task.
						boolean flag = true
		
						// This is a loop.
						while (flag && i < 96) {
		
							// This is a task.
							boolean empty = checkHouse(i)
		
							// This is an agent decision.
							if (empty == false) {
		
		
								// This is a loop.
								for (int k = i;k < i + 9;k++) {
		
									// This is a task.
									//System.out.println("k = " + k)
									//System.out.println("Empty = " + empty)
									loadVector.set(k,power)
									dailyOperation.set(k,true)
		
								}
		
								// This is a task.
								i = 96
								flag = false
		
							} else  {
		
								// This is a task.
								i++
		
							}
		
						}
		
		
					} else  {
		
		
					}
		
				}
		
				// This is a task.
				weeklyLoadVector.add(loadVector)
				weeklyOperation.add(dailyOperation)
				// Return the results.
				return returnValue
		
	}
	
	def checkHouse(int hour) {

        // Define the return value variable.
        def returnValue


        // This is a task.
        boolean empty = true
        int j = hour

        // This is a loop.
        while ((j < hour + 8) && (empty == true) && (j < 96)) {

            // This is a task.
            empty = empty & applianceOf.isEmpty(j+1)
            j++

        }

        // This is a task.
        returnValue = empty
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
				System.out.println("Operation Mode = " + mode)
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
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				createWeeklyOperationVector((int)(times + applianceOf.members.size() / 2))
				fillWeeklyFunction()
				System.out.println("Washing Machine refreshed")
				// Return the results.
				return returnValue
		
	}
	
	
    static constraints = {
    }
}
