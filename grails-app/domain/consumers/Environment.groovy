package consumers

import java.util.HashMap;
import java.util.Vector;
import org.powertac.common.*
import org.powertac.common.enumerations.*

class Environment {

	Vector publicVacationVector = new Vector()
	HashMap hm = new HashMap()
	
	static hasMany = [villages:Village]
	
	
	def createPublicVacationVector(int days) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				Vector v = new Vector(days)
				Random r = new Random()
				
				// This is a loop.
				for (int i = 0; i < days; i++) {
		
					
					// This is a task.
					int x = 1 + r.nextInt(364)
					ListIterator iter = v.listIterator();
		
					// This is a loop.
					while (iter.hasNext()) {
		
						// This is a task.
						int temp = (int)iter.next()
		
						// This is an agent decision.
						if (x == temp) {
		
							// This is a task.
							x = x + 1
							iter = v.listIterator();
		
						} else  {
		
		
						}
		
					}
		
					// This is a task.
					v.add(x)
		
				}
		
				// This is a task.
				java.util.Collections.sort(v);
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def initialize(HashMap hash) {
		
		
				// This is a task.
				setHm(hash)
				int number = (int)hm.get("NumberOfVillages")
				int days = (int)hm.get("PublicVacationDuration")
				setPublicVacationVector(createPublicVacationVector(days))
				float vacationAbsence = (float)hm.get("VacationAbsence")

				for (int i = 1; i < number+1;i++){
				
					def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerType: PowerType.CONSUMPTION)
					villageInfo.save()
					def village = new Village(CustomerInfo: villageInfo)					
					village.initialize(hash)
					village.init()
					village.save()
					this.addToVillages(village)		
					village.fillAggWeeklyLoad()
					village.showAggWeeklyLoad()
				}
	
				System.out.println("End of initialization")
				System.out.println()
		 		
				this.save()
		
	}

	def step(int counter) {
		
				// Define the return value variable.
				def returnValue

				// This is a task.
				//int counter = (int) GetTickCount();
				int day = (int) (counter / 96)+1
				int quarter = (int) (counter %  96)
				int week = (int) (day / 7)+1
				int weekday = (int) (day %  7)
				// This is a task.
				int dayOfWeek
		
				// This is an agent decision.
				if (weekday == 0) {
		
					// This is a task.
					dayOfWeek = 7
		
				} else  {
		
					// This is a task.
					dayOfWeek = weekday
		
				}
		
				// This is an agent decision.
				if (quarter ==0) {
		
					// This is a task.
					quarter = 96
					dayOfWeek--
					day--
		
				} else  {
		
		
				}
				
				this.villages.each{
				
					// This is a task.
					System.out.print("Day: " + day + " Week: " + week + " Weekday: " + dayOfWeek + "  Quarter: " + quarter)
					System.out.println()
					it.step(weekday,quarter)
				
					// This is an agent decision.
					if (quarter == 96) {
		
						// This is a task.
						System.out.println()
						System.out.println("Summary of Daily Load for day " + day)
					
						it.printDailyLoad(weekday)
		
					} else  {
		
		
					}
		
					// This is an agent decision.
					if (dayOfWeek == 6 && quarter == 96) {
		
						// This is a task.
						System.out.println("Refreshing Village Weekly Load")
						System.out.println()
						it.refresh(hm)
						it.fillAggWeeklyLoad()
						it.showAggWeeklyLoad()
		
					} else  {
		
		
					}
				}
				
				// Return the results.
				return returnValue
		
	}
	
	static auditable = true
	
    static constraints = {
    }
}
