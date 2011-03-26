package appliances

import java.util.HashMap;
import java.util.Vector;
import consumers.*

class Appliance {

	String name
	float saturation
	float consumptionShare
	float baseLoadShare
	int power
	int cycleDuration
	boolean od
	boolean inUse
	HashMap probabilitySeason
	HashMap probabilityWeekday
	HashMap probabilityDaytime
	Vector operationVector = new Vector()
	Vector loadVector = new Vector()
	Vector dailyOperation = new Vector()
	Vector weeklyOperation = new Vector()
	Vector weeklyLoadVector = new Vector()
	int times

	static belongsTo = [applianceOf:Household]
	
	def createOperationVector(int times) {}
	
	def getProbability(String season, String day, int hour) {

        // Define the return value variable.
        def returnValue


        // This is a task.
        float pseason = (float) probabilitySeason.get(season);
        float pday = (float) probabilityWeekday.get(day);
        float phour = (float) probabilityDaytime.get(hour);
        // This is a task.
        returnValue = pseason * pday * phour
        // Return the results.
        return returnValue

    }
	
	def initialize() {}
	
	def shiftingOperation(Vector v) {}
	
	def fillDay(float sunday, float workingday, float saturday) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				HashMap hm = new HashMap();
				hm.put("Saturday", new Float(saturday));
				hm.put("Sunday", new Float(sunday));
				// This is a task.
				hm.put("Monday", new Float(workingday));
				hm.put("Tuesday", new Float(workingday));
				hm.put("Wednesday", new Float(workingday));
				hm.put("Thursday", new Float(workingday));
				hm.put("Friday", new Float(workingday));
				// This is a task.
				returnValue = hm
				// Return the results.
				return returnValue
		
	}
	
	def fillSeason(float summer, float winter, float transition) {

        // Define the return value variable.
        def returnValue

        // This is a task.
        HashMap hm = new HashMap();
        hm.put("Winter", new Float(winter));
        hm.put("Transition", new Float(transition));
        hm.put("Summer", new Float(summer));
        returnValue = hm
        // Return the results.
        return returnValue

    }
	
	def fillHour() {}
	
	def showStatus() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				System.out.println("Name = " + name)
				System.out.println("Member Of = " + applianceOf.getName())
				System.out.println("Saturation = " + saturation)
				System.out.println("Consumption Share = " + consumptionShare)
				System.out.println("Base Load Share = " + baseLoadShare)
				// This is a task.
				System.out.println("Power = " + power)
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
	
	def fillDailyFunction() {}
	
	def refresh() {}
	
     static constraints = {
		
		name()
		applianceOf()
//		saturation()
//		consumptionShare()
//		baseLoadShare()
		power()
		cycleDuration()
//		od()
		inUse()
		currentLoad()
		
    }
	
	static mapping = {
		sort "name"
		}
	
	String toString(){
		"${name}, ${Household} (${inUse})"
	}
	
}
