package persons

import java.util.HashMap;
import java.util.Vector;

class WorkingPerson extends Person{

	Vector workingDays = null
	int workingDuration = 0
	int vacationDuration = 0
	Vector vacationVector = null
	int workingStartHour = 0
	
	def createWorkingDaysVector(int days) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector(days)
		
				// This is an agent decision.
				if (days < 5) {
		
		
					// This is a loop.
					for (int i = 0; i < days; i++) {
		
						// This is a task.
						Random r = new Random()
						int x =  (r.nextInt(1) * 4) + 1
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
		
				} else  {
		
					// This is a task.
					v.add(1)
					v.add(2)
					v.add(3)
					v.add(4)
					v.add(5)
		
					// This is an agent decision.
					if (days == 5) {
		
		
					} else  {
		
		
						// This is an agent decision.
						if (days == 7) {
		
							// This is a task.
							v.add(6)
							v.add(0)
		
						} else  {
		
							Random r = new Random();
							// This is an agent decision.
							if (r.nextFloat() > 0.5 ) {
		
								// This is a task.
								v.add(6)
		
							} else  {
		
								// This is a task.
								v.add(0)
		
							}
		
						}
		
					}
					// This is a task.
					java.util.Collections.sort(v);
					returnValue = v
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def createVacationVector(int duration) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				Vector v = new Vector(duration)
				int counter = duration
				int counter2 = 0
		
				// This is a loop.
				while (counter > 0) {
		
					// This is a task.
					Random r = new Random()
					int x = (int) r.nextInt(364) + 1
					counter2 = 1 + (int)(r.nextInt(counter))
					ListIterator iter = v.listIterator()
		
					// This is a loop.
					while (counter2 > 0) {
		
						// This is a task.
						v.add(x)
						counter = counter - 1
						counter2 = counter2 - 1
						x = x + 1
		
					}
		
		
				}
		
				// This is a task.
				java.util.Collections.sort(v);
				returnValue = v
				// Return the results.
				return returnValue
		
	}

	def workingDaysRandomizer(HashMap hm) {
		
				// Define the return value variable.
				def returnValue
		
				// This is a task.
				int oneDay = ((int)hm.get("OneDay"))
				int twoDays = ((int)hm.get("TwoDays"))
				int threeDays = ((int)hm.get("ThreeDays"))
				int fourDays = ((int)hm.get("FourDays"))
				int fiveDays = ((int)hm.get("FiveDays"))
				// This is a task.
				int sixDays = ((int)hm.get("SixDays"))
				int sevenDays = ((int)hm.get("SevenDays"))
				Random r = new Random()
				int x = (int) r.nextInt(100)
		
				// This is an agent decision.
				if (x < fiveDays) {
		
					// This is a task.
					returnValue = 5
		
				} else  {
		
		
					// This is an agent decision.
					if (x >= fiveDays & x < (fiveDays + sixDays)) {
		
						// This is a task.
						returnValue = 6
		
					} else  {
		
		
						// This is an agent decision.
						if (x >= (fiveDays + sixDays) & x < (fiveDays + sixDays + fourDays)) {
		
							// This is a task.
							returnValue = 4
		
						} else  {
		
		
							// This is an agent decision.
							if (x >= (fiveDays + sixDays + fourDays) & x < (fiveDays + sixDays + fourDays + threeDays)) {
		
								// This is a task.
								returnValue = 3
		
							} else  {
		
		
								// This is an agent decision.
								if (x >= (fiveDays + sixDays + fourDays + threeDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays)) {
		
									// This is a task.
									returnValue = 2
		
								} else  {
		
		
									// This is an agent decision.
									if (x >= (fiveDays + sixDays + fourDays + threeDays+twoDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays+sevenDays)) {
		
										// This is a task.
										returnValue  = 7
		
									} else  {
		
										// This is a task.
										returnValue = 1
		
									}
		
								}
		
							}
		
						}
		
					}
		
				}
				// Return the results.
				return returnValue
		
	}
	
	def showStatus() {
		
		
				// This is a task.
				System.out.println("Name = " + name)
				//System.out.println("Member Of = " + getMemberOf().getName())
				System.out.println("Sickness Days = ")
				ListIterator iter = sicknessVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Leisure Days of Week = ");
				iter = leisureVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Leisure Duration = " + leisureDuration);
				System.out.println("Working Days = ");
				iter = workingDays.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Working Duration = " + workingDuration);
				System.out.println("Working Starting Hour = " + workingStartHour)
				System.out.println("Vacation Duration = " + vacationDuration);
				System.out.println("Vacation Days = ");
				iter = vacationVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Public Vacation of Year = ");
				iter = pVacationVector.listIterator();
		
				// This is a loop.
				while (iter.hasNext()) {
		
					// This is a task.
					System.out.println(iter.next());
		
				}
		
				// This is a task.
				System.out.println("Weekly Routine : ")
				iter = weeklyRoutine.get(0).listIterator();
		
				// This is a loop.
				for (int i = 0; i < 7;i++) {
		
					// This is a task.
					System.out.println("Day " + (i))
					iter = weeklyRoutine.get(i).listIterator();
		
					// This is a loop.
					for (int j =0;j < 96;j++) {
		
						// This is a task.
						System.out.println("Quarter : " + (j+1) + " Status : " + iter.next())
		
					}
		
		
				}

		
	}
	
	def fillWork() {}
	
    static constraints = {
    }
}
