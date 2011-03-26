package consumers

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
import org.powertac.common.*


class Village extends AbstractCustomer{

	int numberOfHouses
	Vector aggDailyLoad = new Vector()
	Vector aggWeeklyLoad = new Vector()
	Vector aggWeeklyLoadInHours = new Vector()
	Vector aggDailyLoadInHours = new Vector()
	Vector aggWeeklyCostInHours = new Vector()
	
	static hasMany = [houses:Household]
	static belongsTo = [environment:Environment]
	
	void initialize(HashMap hm){
		
	
		int houses = (int)hm.get("NumberOfHouses")
		
		setNumberOfHouses(houses)	
	
		Vector publicVacationVector = new Vector();
		publicVacationVector.add(10);
		publicVacationVector.add(15);
		
		
		for (i in 0..houses-1) {
			
			
						System.out.println("Initializing House " + i)
						def hh = new Household()
						this.addToHouses(hh)
						hh.initialize("House" + i,hm, publicVacationVector)
			
		}

		
	}
	
	
	def fillAggWeeklyLoad() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					setAggDailyLoad(fillAggDailyLoad(i))
					aggWeeklyLoad.add(aggDailyLoad)
					setAggDailyLoadInHours(fillAggDailyLoadInHours())
					aggWeeklyLoadInHours.add(aggDailyLoadInHours)
				
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	
	def showAggWeeklyLoad() {


        // This is a loop.
        for (int i = 0; i < 7;i++) {

            // This is a task.
            System.out.println("Day " + (i))
            ListIterator iter = aggWeeklyLoad.get(i).listIterator();

            // This is a loop.
            for (int j = 0;j < 96; j++) {

                // This is a task.
                System.out.println("Quarter : " + (j+1) + " Load : " + iter.next())

            }


        }


        // This is a loop.
        for (int i = 0; i < 7;i++) {

            // This is a task.
            System.out.println("Day " + (i))
            ListIterator iter = aggWeeklyLoadInHours.get(i).listIterator();
           

            // This is a loop.
            for (int j = 0;j < 24; j++) {

                // This is a task.
                System.out.println("Hour : " + (j+1) + " Load : " + iter.next())

            }


        }

    }
	
	void consumePower(){
		
		int serial = ((timeService.currentTime.millis - timeService.start)/3600000) + 1
		
		int day = (int) (serial / 24)+1
		int hour = (int) (serial % 24)
		int weekday = (int) (day % 7)

		println(serial + " " + hour + " " + weekday)
		
		double ran = this.aggWeeklyLoadInHours.get(weekday).getAt(hour)
		
		subscriptions.each {
			
				println(ran);
				it.usePower(ran)
			
		}

	}
	
		
	def fillAggDailyLoad(int weekday) {

        // Define the return value variable.
        def returnValue

        // This is a task.
        Vector v = new Vector(96)
        int sum = 0

        // This is a loop.
        for (int i = 0;i < 96; i++) {

            // This is a task.
            sum = 0 

            // This is a loop.
            this.houses.each {

                // This is a task.
                sum = sum + it.weeklyLoad.get(weekday).get(i)
				
            }
			
			
			
            // This is a task.
            v.add(sum)

        }

        // This is a task.
        returnValue = v
        // Return the results.
        return returnValue

    }
	
	
	def fillAggDailyLoadInHours() {

        // Define the return value variable.
        def returnValue


        // This is a task.
        Vector v = new Vector()
        int sum = 0

        // This is a loop.
        for (int i = 0;i < 24; i++) {

            // This is a task.
            sum = 0 
            sum = aggDailyLoad.get(i*4) + aggDailyLoad.get(i*4 +1) + aggDailyLoad.get(i*4+2) + aggDailyLoad.get(i*4+3)
            v.add(sum)

        }

        // This is a task.
        returnValue = v
        // Return the results.
        return returnValue

    }
	
	def refresh(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		

		
				// This is a loop.
				this.houses.each {
		
					// This is a task.
					it.refresh(hm)
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	def printDailyLoad(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a loop.
				this.houses.each {
		
					// This is a task.
					it.printDailyLoad(weekday)
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	def step(int weekday, int quarter) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a loop.
				this.houses.each {
		
					// This is a task.
					it.step(weekday,quarter)
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	static auditable = true
	
	public String toString() {
		return name
	  }
	
    static constraints = {
    }
}
