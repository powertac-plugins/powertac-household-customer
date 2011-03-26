package consumers

import java.util.HashMap;
import java.util.Vector;

import appliances.*
import persons.*

class Household {

	String name
	Vector dailyLoad = new Vector()
	Vector weeklyLoad = new Vector()
	int yearConsumption
	Vector dailyLoadInHours = new Vector()
	Vector weeklyLoadInHours = new Vector()
	int currentLoad
	
	static hasMany = [members:Person, appliances:Appliance]
	
	static belongsTo = [village:Village]
	
	def initialize(String HouseName, HashMap hm, Vector publicVacationVector) {
		
				// This is a task.
				setName(HouseName)
				int persons = memberRandomizer(hm)
		
				// This is a loop.
				for (int i = 0;i < persons; i++) {
		
					// This is a task.
					addPerson(i+1,hm,publicVacationVector)
		
				}
		
				// This is a task.

				fillAppliances(hm)
		
				// This is a loop.
				for (int i =0;i < 7;i++) {
		
					// This is a task.
					setDailyLoad(fillDailyLoad(i))
					weeklyLoad.add(dailyLoad)
					setDailyLoadInHours(fillDailyLoadInHours())
					weeklyLoadInHours.add(dailyLoadInHours)
		
				}
		
				// This is a task.
			//	showStatus()
				

	}
	
	def addPerson(int counter, HashMap hm, Vector publicVacationVector) {
		
				// This is a task.
				int pp = (int)hm.get("PeriodicPresent")
				int mp = (int)hm.get("MostlyPresent")
				int ra = (int)hm.get("RandomlyAbsent")
				float va = (float)hm.get("VacationAbsence")
				Random r = new Random()
				int x = r.nextInt(100);
		
				// This is an agent decision.
				if (x < pp) {
		
					// This is a task.
					PeriodicPresentPerson ppp = new PeriodicPresentPerson()
					ppp.initialize("PPP" + counter,hm,publicVacationVector)
					
					ppp.weeklyRoutine = ppp.fillWeeklyRoutine(va)
					this.addToMembers(ppp)
	
				} else  {
		
		
					// This is an agent decision.
					if (x >= pp & x < (pp + mp)) {
		
						// This is a task.
						MostlyPresentPerson mpp = new MostlyPresentPerson()
						mpp.initialize("MPP" + counter,hm,publicVacationVector)

						mpp.weeklyRoutine = mpp.fillWeeklyRoutine(va)
						this.addToMembers(mpp)
		
		
					} else  {
		
						// This is a task.
						RandomlyAbsentPerson rap = new RandomlyAbsentPerson()
						rap.initialize("RAP"+ counter,hm,publicVacationVector)
						
						rap.weeklyRoutine = rap.fillWeeklyRoutine(va)
						this.addToMembers(rap)
		
		
					}
		
				}
				

	}
	
	def memberRandomizer(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				int one = (int) hm.get("OnePerson")
				int two = (int) hm.get("TwoPersons")
				int three = (int) hm.get("ThreePersons")
				int four = (int) hm.get("FourPersons")
				int five = (int) hm.get("FivePersons")
				// This is a task.
				
				Random r = new Random()
				int x = r.nextInt(100);
				
				// This is an agent decision.
				if (x < one) {
		
					// This is a task.
					setYearConsumption((int) hm.get("OnePersonConsumption"))
					returnValue = 1
		
				} else  {
		
		
					// This is an agent decision.
					if (x >= one &  x < (one + two)) {
		
						// This is a task.
						setYearConsumption((int) hm.get("TwoPersonsConsumption"))
						returnValue = 2
		
					} else  {
		
		
						// This is an agent decision.
						if (x >= (one + two) & x < (one + two + three)) {
		
							// This is a task.
							setYearConsumption((int) hm.get("ThreePersonsConsumption"))
							returnValue = 3
		
						} else  {
		
		
							// This is an agent decision.
							if (x >= (one + two + three) & x < (one + two + three + four)) {
		
								// This is a task.
								setYearConsumption((int) hm.get("FourPersonsConsumption"))
								returnValue = 4
		
							} else  {
		
								// This is a task.
								setYearConsumption((int) hm.get("FivePersonsConsumption"))
								returnValue = 5
		
							}
		
						}
		
					}
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def checkProbability(Appliance app) {
		
				// This is a task.
				Random r = new Random()
				int x = r.nextInt(100);
				int threshold = app.saturation * 100
		
				// This is an agent decision.
			if (x < threshold) {
		
					// This is a task.
					
					app.fillWeeklyFunction()
				
				} else  {
		
					this.appliances.remove(app);
			}
	}
	
	def fillAppliances(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
		
				// Refrigerator
				Refrigerator ref = new Refrigerator();
				this.addToAppliances(ref)
				ref.initialize(hm);
				ref.fillWeeklyFunction()
				
				// Washing Machine
				WashingMachine wm = new WashingMachine();
				this.addToAppliances(wm)
				wm.initialize(hm);
				wm.fillWeeklyFunction()
				
				// Consumer Electronics
				ConsumerElectronics ce = new ConsumerElectronics();
				this.addToAppliances(ce)
				ce.initialize(hm);
				ce.fillWeeklyFunction()
				
				// ICT
				ICT ict = new ICT();
				this.addToAppliances(ict)
				ict.initialize(hm);
				ict.fillWeeklyFunction()
				
				
				// Lights
				Lights lights = new Lights();
				this.addToAppliances(lights)
				lights.initialize(hm);
				lights.fillWeeklyFunction()
				
				//Others
				Others others = new Others();
				this.addToAppliances(others)
				others.initialize(hm);
				others.fillWeeklyFunction()
		
				// Freezer
				Freezer fr = new Freezer()
				fr.initialize(hm)
				checkProbability(fr)
				
				// Dishwasher
				Dishwasher dw = new Dishwasher()
				this.addToAppliances(dw)
				dw.initialize(hm)
				checkProbability(dw)
				
				//Stove
				Stove st = new Stove()
				this.addToAppliances(st)
				st.initialize(hm)
				checkProbability(st)
				
				//Dryer
				Dryer dr = new Dryer()
				this.addToAppliances(dr)
				dr.initialize(hm)
				checkProbability(dr)
				
				
				//Water Heater
				WaterHeater wh = new WaterHeater()
				this.addToAppliances(wh)
				wh.initialize(hm)
				checkProbability(wh)
				
				//Circulation Pump
				CirculationPump cp = new CirculationPump()
				this.addToAppliances(cp)
				cp.initialize(hm)
				checkProbability(cp)
				
				//Space Heater
				SpaceHeater sh = new SpaceHeater()
				this.addToAppliances(sh)
				sh.initialize(hm)
				checkProbability(sh)
			
				// Return the results.
				return returnValue
		
	}
	
	
	def isEmpty(int quarter) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				boolean x = true
		
				// This is a loop.
				this.members.each {
		
		
					// This is an agent decision.
					if (it.getDailyRoutine().get(quarter-1) == Status.Normal || it.getDailyRoutine().get(quarter-1) == Status.Sick) {
		
						// This is a task.
						x = false
		
					} else  {
		
		
					}
		
				}
		
				// This is a task.
				returnValue = x
				// Return the results.
				return returnValue
		
	}
	
	def showStatus() {
				
		
				// This is a task.
				System.out.println("HouseHold Name : " + name)
				System.out.println("HouseHold Yearly Consumption : " + yearConsumption)
				System.out.println("Number of Persons : " + members.size())
				Iterator iter = members.iterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					iter.next().showStatus();
		
				}
		
				// This is a task.
				iter = appliances.iterator();
				System.out.println(" Number Of Appliances = ")
				System.out.println(appliances.size())
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// Show member's status
					iter.next().showStatus();
		
				}
		
				// This is a task.
				System.out.println(" Weekly Load = ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					ListIterator iter2 = weeklyLoad.get(i).listIterator();
		
					// This is a loop.
					for (int j = 0;j < 96; j++) {
		
						// This is a task.
						System.out.println("Quarter : " + (j+1) + " Load : " + iter2.next())
		
					}
		
		
				}
		
				// This is a task.
				System.out.println(" Weekly Load In Hours = ")
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					ListIterator iter2 = weeklyLoadInHours.get(i).listIterator();
		
					// This is a loop.
					for (int j = 0;j < 24; j++) {
		
						// Print Daily Load
						System.out.println("Hours : " + (j+1) + " Load : " + iter2.next())
		
					}
	
		
				}
		

		
	}
	
	def fillDailyLoad(int weekday) {
		
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
					this.appliances.each {
		
						// This is a task.
						sum = sum + it.weeklyLoadVector.get(weekday).get(i)
		
					}
		
					// This is a task.
					v.add(sum)
		
				}
		
				// This is a task.
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def isOnVacation(int quarter) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				boolean x = false
		
				// This is a loop.
				this.members.each {
		
		
					// This is an agent decision.
					if (it.getDailyRoutine().get(quarter-1) == Status.Vacation) {
		
						// This is a task.
						x = true
		
					} else  {
		
		
					}
		
				}
		
				// This is a task.
				returnValue = x
				// Return the results.
				return returnValue
		
	}
	
	def step(int weekday, int quarter) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				System.out.println()
				System.out.println("House: " + name)
				System.out.println("Person Quarter Status")
			
		
				// This is a loop.
				this.members.each{ 
		
					// This is a task.
					System.out.println("Name: " + it.getName() + " Status: " + it.getWeeklyRoutine().get(weekday).get(quarter-1))
		
				}
		
				// This is a task.
				System.out.println("Appliances Quarter Status")
		
				// This is a loop.
				this.appliances.each{
		
					// This is a task.
					System.out.println("Name: " + it.getName() + " Status: " + it.getWeeklyOperation().get(weekday).get(quarter-1) + " Load: " +  it.getWeeklyLoadVector().get(weekday).get(quarter-1))
		
				}
		
				// This is a task.
				setCurrentLoad(weekday,quarter)
				System.out.println("Current Load: " + currentLoad)
				System.out.println()
				// Return the results.
				return returnValue
		
	}
	
	def fillDailyLoadInHours() {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector(24)
				int sum = 0
		
				// This is a loop.
				for (int i = 0;i < 24; i++) {
		
					// This is a task.
					sum = 0
					sum = dailyLoad.get(i*4) + dailyLoad.get(i*4 +1) + dailyLoad.get(i*4+2) + dailyLoad.get(i*4+3)
					v.add(sum)
		
				}
		
				// This is a task.
				returnValue = v
				// Return the results.
				return returnValue
		
	}
	
	def setCurrentLoad(int weekday, int quarter) {
		
				// This is a task.
				setCurrentLoad(weeklyLoad.get(weekday).get(quarter-1))
	}
	
	def refresh(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				System.out.println()
				System.out.println("Refresh Weekly Routine Of House " + name)
				System.out.println()
				
				System.out.println("Refresh Weekly Routine Of Household Members")
		
				// This is a loop.
				this.members.each {
		
					// This is a task.
					it.refresh(hm)
		
				}
		
				// This is a task.
				
				System.out.println()
				System.out.println("Refresh Weekly Functions of Appliances")
		
				// This is a loop.
				this.appliances.each {
		
					// This is a task.
					System.out.println()
					it.refresh()
		
				}
		
				// This is a task.
				weeklyLoad.removeAllElements()
				weeklyLoadInHours.removeAllElements()
		
				// This is a loop.
				for (int i =0;i < 7;i++) {
		
					// This is a task.
					setDailyLoad(fillDailyLoad(i))
					weeklyLoad.add(dailyLoad)
					setDailyLoadInHours(fillDailyLoadInHours())
					weeklyLoadInHours.add(dailyLoadInHours)
		
				}
		
				// Return the results.
				return returnValue
		
	}
	
	def printDailyLoad(int weekday) {
		
				// Define the return value variable.
				def returnValue
		
		
				// This is a task.
				ListIterator iter = weeklyLoadInHours.get(weekday).listIterator()
				System.out.println()
				System.out.println("Summary of Daily Load of House " + name)
		
				// This is a loop.
				for (int j = 0;j < 24; j++) {
		
					// This is a task.
					System.out.println("Hour : " + (j+1) + " Load : " + iter.next())
		
				}
		
				// Return the results.
				return returnValue
		
	}
	

	
    static constraints = {
    }
}
