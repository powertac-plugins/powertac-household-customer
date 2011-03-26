package appliances

class NotShiftingAppliance extends Appliance {

	def createDailyOperationVector(int times) {

        // Define the return value variable.
        def returnValue

        // This is a task.
        Random rand = new Random()
        Vector v = new Vector(96)

        // This is a loop.
        for (int i = 0;i < 96;i++) {

            // This is a task.
            v.add(false)

        }


        // This is a loop.
        for (int i = 0;i < times;i++) {

            // This is a task.
			Random r = new Random()
            int quarter = 1 + r.nextInt(96)
            v.set(quarter-1,true)

        }

        // This is a task.
        returnValue = v
        // Return the results.
        return returnValue

    }
	
	def createWeeklyOperationVector(int times) {


        // This is a loop.
        for (int i = 0;i < 7; i++) {

            // This is a task.
            operationVector.add(createDailyOperationVector(times))

        }

    }
	
	def fillWeeklyFunction() {
		
		
				// This is a loop.
				for (int i = 0;i < 7; i++) {
		
					// This is a task.
					fillDailyFunction(i)
		
				}
		
	}
	
	
    static constraints = {
    }
}
