package appliances

import java.util.HashMap;


class WaterHeater extends FullyShiftingAppliance{

	HeaterType type
	
	def fillDailyFunction(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				int start = 0
				int temp = 0
				loadVector = new Vector()
				dailyOperation = new Vector()
				Vector operation = new Vector()
		
				// This is an agent decision.
				if (type == HeaterType.InstantHeater) {
		
					// This is a task.
					operation = operationVector.get(weekday)
		
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
		
				} else  {
		
		
					// This is a loop.
					for (int i = 0;i < 96;i++) {
		
						// This is a task.
						operation.add(false)
						dailyOperation.add(false)
						loadVector.add(0)
		
					}
		
		
					// This is an agent decision.
					Random r = new Random()
					if (r.nextFloat() > 0.8) {
		
						// This is a task.
						
						start = 21 + r.nextInt(19)
		
					} else  {
		
						// This is a task.
						start = 1 + r.nextInt(20)
		
					}
		
					// This is a loop.
					for (int i = start;i < start + 8;i++) {
		
						// This is a task.
						operation.set(i,true)
						dailyOperation.set(i,true)
						loadVector.set(i, power)
						temp = i
		
					}
		
		
					// This is a loop.
					for (int j = 1;j < 4; j++) {
		
						// This is a task.
						operation.set((temp + 16*j),true)
						dailyOperation.set((temp + 16*j),true)
						loadVector.set((temp + 16*j), power)
		
					}
		
					// This is a task.
					weeklyLoadVector.add(loadVector)
					weeklyOperation.add(dailyOperation)
					operationVector.add(operation)
		
				}
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
				System.out.println("Heater Type = " + type)
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
				System.out.println("Weekly Operation Vector = ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					ListIterator iter =operationVector.get(i).listIterator();
		
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
					ListIterator iter = weeklyOperation.get(i).listIterator();
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
	
	def initialize(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		

				// This is a task.
				name = "WaterHeater"
				saturation = (float)hm.get("WaterHeaterSaturation")
				Random rand = new Random()
				int x = 1 + rand.nextInt(100)
				int limit = (int)hm.get("InstantHeater")
		
				// This is an agent decision.
				if ( x < limit) {
		
					// This is a task.
					consumptionShare = (float) (0.01 * (2.2* rand.nextGaussian() + 13))
					baseLoadShare = 0
					power = (int) (2000 * rand.nextGaussian() + 12000)
					cycleDuration = 1
					setType(HeaterType.InstantHeater)
					// This is a task.
					times = (float)hm.get("InstantHeaterDailyTimes")
					// This is a task.
					od = true
					inUse = false
					probabilitySeason = fillSeason(24,38,38)
					probabilityWeekday = fillDay(16,14,14)
					createWeeklyOperationVector( (int)(times + applianceOf.members.size()/2))
		
				} else  {
		
					// This is a task.
					consumptionShare = (float) (0.01 * (2.2 * rand.nextGaussian() + 13))
					baseLoadShare = 0
					power = (int) (500 * rand.nextGaussian() + 3000)
					cycleDuration = 8
					setType(HeaterType.StorageHeater)
					// This is a task.
					od = false
					inUse = false
					probabilitySeason = fillSeason(24,38,38)
					probabilityWeekday =fillDay(16,14,14)
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def refresh() {
		
				// Define the return value variable.
				def returnValue
		
	
				// This is an agent decision.
				if (type == HeaterType.InstantHeater) {
		
					// This is a task.
					times = (float)hm.get("InstantHeaterDailyTimes")
					createWeeklyOperationVector( (int)(times + getMemberOf().members.size()/2))
					fillWeeklyFunction()
					System.out.println("Instant Water Heater refreshed")
		
				} else  {
		
					// This is a task.
					fillWeeklyFunction()
					System.out.println("Storage Water Heater refreshed")
		
				}
				// Return the results.
				return returnValue
		
	}
	
	
    static constraints = {
    }
}
