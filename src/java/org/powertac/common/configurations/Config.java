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

package org.powertac.common.configurations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The configuration class creates an instance that is capable of reading the
 * configuration file of our household models, which contains the variables
 * needed to create realistic and autonomous models of persons and appliances
 * 
 * @author Antonios Chrysopoulos
 * @since 0.5
 * @version 1 Last Updated: 12.03.2011
 */
public class Config {

  /** the scanner variable user to read the configuration file line by line **/
  Scanner fileScanner;

  /**
   * the hashmap variable that contains the variables and their values as read
   * from configuration file
   **/
  HashMap variablesHashMap = new HashMap();

  /**
   * Function that allows read the configuration file by finding the variables
   * contained inside one by one and creating pairs of keys and values for the
   * data to be stored
   */
  public void readConf(String configFile) throws FileNotFoundException {

    // Reading the configuration file
    fileScanner = new Scanner(new File("../powertac-household-customer/"
        + configFile));

    // Finding the variables one by one
    this.findVariable("PublicVacationDuration");
    // this.findVariable("NumberOfHouses");
    this.findVariable("NumberOfVillages");
    this.findVariable("OnePersonConsumption");
    this.findVariable("TwoPersonsConsumption");
    this.findVariable("ThreePersonsConsumption");
    this.findVariable("FourPersonsConsumption");
    this.findVariable("FivePersonsConsumption");
    this.findVariable("OnePerson");
    this.findVariable("TwoPersons");
    this.findVariable("ThreePersons");
    this.findVariable("FourPersons");
    this.findVariable("FivePersons");
    this.findVariable("PeriodicPresent");
    this.findVariable("MostlyPresent");
    this.findVariable("RandomlyAbsent");
    this.findVariable("SicknessMean");
    this.findVariable("SicknessDev");
    this.findVariable("PPLeisure");
    this.findVariable("MPLeisure");
    this.findVariable("RALeisure");
    this.findVariable("LeisureDev");
    this.findVariable("LeisureDurationMean");
    this.findVariable("LeisureDurationDev");
    this.findVariable("OneDay");
    this.findVariable("TwoDays");
    this.findVariable("ThreeDays");
    this.findVariable("FourDays");
    this.findVariable("FiveDays");
    this.findVariable("SixDays");
    this.findVariable("SevenDays");
    this.findVariable("WorkingDurationMean");
    this.findVariable("WorkingDurationDev");
    this.findVariable("VacationDurationMean");
    this.findVariable("VacationDurationDev");
    this.findVariable("VacationAbsence");
    this.findVariable("RefrigeratorSaturation");
    this.findVariable("FreezerSaturation");
    this.findVariable("DishwasherSaturation");
    this.findVariable("DishwasherWeeklyTimes");
    this.findVariable("StoveSaturation");
    this.findVariable("StoveDailyTimes");
    this.findVariable("WashingMachineSaturation");
    this.findVariable("WashingMachineWeeklyTimes");
    this.findVariable("DryerSaturation");
    this.findVariable("DryerWeeklyTimes");
    this.findVariable("ConsumerElectronicsSaturation");
    this.findVariable("ConsumerElectronicsDailyTimes");
    this.findVariable("ICTSaturation");
    this.findVariable("ICTDailyTimes");
    this.findVariable("WaterHeaterSaturation");
    this.findVariable("InstantHeater");
    this.findVariable("StorageHeater");
    this.findVariable("InstantHeaterDailyTimes");
    this.findVariable("CirculationPumpSaturation");
    this.findVariable("CirculationPumpPercentage");
    this.findVariable("SpaceHeaterSaturation");
    this.findVariable("SpaceHeaterPercentage");
    this.findVariable("LightsDailyTimes");
    this.findVariable("OthersDailyTimes");
    this.findVariable("NotShiftingCustomers");
    this.findVariable("RandomlyShiftingCustomers");
    this.findVariable("RegularlyShiftingCustomers");
    this.findVariable("SmartShiftingCustomers");

    fileScanner.close();
  }

  /**
   * The parsing function. It searches for the variable named after the string
   * we import and creates the pair of values for the HashMap.
   */
  public void findVariable(String str) throws FileNotFoundException {

    // Creating the dummy variables for our purpose
    String test1 = "";
    String test2 = "";
    String test3 = "";

    // Loop through each line of the configuration file
    while (fileScanner.hasNext()) {
      test1 = fileScanner.next();
      if (test1.equals(str)) {
        test2 = fileScanner.next();
        test3 = fileScanner.next();
        variablesHashMap.put(str, new Float(test3));
        break;
      }
    }

  }

}