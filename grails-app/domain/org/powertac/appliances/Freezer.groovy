/*
* Copyright 2009-2010 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an
* "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
* either express or implied. See the License for the specific language
* governing permissions and limitations under the License.
*/


package org.powertac.appliances

import java.util.HashMap;
import org.powertac.common.configurations.Constants

/**
* Freezer is the utilized in combination with the fridge in the household. 
* This appliance can automatically change the freezing cyles in order to 
* save energy, without problem without tenants manipulation. So this is 
* a fully shifting appliance.
*
* @author Antonios Chrysopoulos
* @version 1, 13/02/2011
*/

class Freezer extends FullyShiftingAppliance{

	@ Override
	def initialize(HashMap hm) {
		
		// Creating Auxiliary Variables
		Random gen = ensureRandomSeed()
		
		// Filling the base variables
		name = "Freezer"
		saturation = (float)hm.get("FreezerSaturation")

		consumptionShare = (float) (Constants.PERCENTAGE * (Constants.FREEZER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.FREEZER_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.FREEZER_BASE_LOAD_SHARE
    power = (int) (Constants.FREEZER_POWER_VARIANCE * gen.nextGaussian() + Constants.FREEZER_POWER_MEAN)
    cycleDuration = Constants.FREEZER_DURATION_CYCLE

    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.FREEZER_POSSIBILITY_SEASON_1,Constants.FREEZER_POSSIBILITY_SEASON_2,Constants.FREEZER_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.FREEZER_POSSIBILITY_DAY_1,Constants.FREEZER_POSSIBILITY_DAY_2,Constants.FREEZER_POSSIBILITY_DAY_3)
    
	}
	
	@ Override
	def fillDailyFunction(int weekday) {
		
		// Initializing Variables
		loadVector = new Vector()
		dailyOperation = new Vector()
    Random gen = ensureRandomSeed()
		int k = 0;
    
    if (gen.nextFloat() > 0.5) k = 1
    
		for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
		    
			// Working the half quarters of a day
			if (i+k % 2 == 0) {
		
				// This is a task.
				loadVector.add(power)
				dailyOperation.add(true)
		
			} else  {
		
				loadVector.add(0)
				dailyOperation.add(false)
		
			}
		
		}
		
		// Save the vectors just created
		weeklyLoadVector.add(loadVector)
		weeklyOperation.add(dailyOperation)
		operationVector.add(dailyOperation)
		
	}
	
	@ Override
	def refresh() {

		fillWeeklyFunction()
		System.out.println("Freezer refreshed")
		
	}
	
  static constraints = {
  }
  
}

