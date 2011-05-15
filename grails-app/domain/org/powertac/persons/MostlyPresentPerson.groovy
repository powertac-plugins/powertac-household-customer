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


package org.powertac.persons

import java.math.*
import java.util.HashMap
import java.util.Vector

import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.Status

/**
 * This is the instance of the person type that spents most of its time
 * inside the house. Such types are children or elderly people. These persons
 * don't work at all, so they have more time for leisure activities.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class MostlyPresentPerson extends Person {


  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the person as it should for this type.
   * @param AgentName
   * @param hm
   * @param publicVacationVector
   * @return
   */
  def initialize(String AgentName, HashMap hm, Vector publicVacationVector, Random gen) {
    // Variables Taken from the configuration file
    float sicknessMean = ((float)hm.get("SicknessMean"))
    float sicknessDev = ((float)hm.get("SicknessDev"))
    float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
    float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
    float MPLeisure = ((int)hm.get("MPLeisure"))

    // Filling the main variables
    name = AgentName
    status = Status.Normal
    sicknessVector = createSicknessVector(sicknessMean, sicknessDev,gen)
    this.publicVacationVector = publicVacationVector
    int x = (int) (gen.nextGaussian() + MPLeisure)
    leisureVector = createLeisureVector(x,gen)
    leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
  }


  @ Override
  def showInfo() {
    // Printing base variables
    System.out.println("Name = " + name)

    // Printing Sickness variables
    System.out.println("Sickness Days = ")
    ListIterator iter = sicknessVector.listIterator();
    while (iter.hasNext()) System.out.println(iter.next());

    // Printing Leisure variables
    System.out.println("Leisure Days of Week = ");
    iter = leisureVector.listIterator();
    while (iter.hasNext()) System.out.println(iter.next());
    System.out.println("Leisure Duration = " + leisureDuration);

    // Printing Public Vacation Variables
    System.out.println("Public Vacation of Year = ");
    iter = publicVacationVector.listIterator();
    while (iter.hasNext()) System.out.println(iter.next());

    // Printing Weekly Schedule
    System.out.println("Weekly Routine Length : " + weeklyRoutine.size())
    System.out.println("Weekly Routine : ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i))
      iter = weeklyRoutine.get(i).listIterator();
      for (int j =0;j < Constants.QUARTERS_OF_DAY;j++) System.out.println("Quarter : " + (j+1) + " Status : " + iter.next())
    }
  }

  @ Override
  void refresh(HashMap hm, Random gen)
  {

    // Renew Variables
    float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
    float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
    float MPLeisure = ((int)hm.get("MPLeisure"))
    float vacationAbsence = ((float)hm.get("VacationAbsence"))
    int x = (int) (gen.nextGaussian() + MPLeisure)
    leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
    leisureVector = createLeisureVector(x,gen)
    for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
      fillDailyRoutine(i,vacationAbsence, gen)
      weeklyRoutine.add(dailyRoutine)
    }
  }

  static constraints = {
  }
}
