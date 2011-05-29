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
  def initialize(String AgentName, ConfigObject conf, Vector publicVacationVector, Random gen) {
    // Variables Taken from the configuration file
    float sicknessMean = conf.household.sickness.SicknessMean
    float sicknessDev = conf.household.sickness.SicknessDev
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float MPLeisure = conf.household.leisure.numberByType.MPLeisure

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
    log.info("Name = " + name)

    // Printing Sickness variables
    log.info("Sickness Days = ")
    ListIterator iter = sicknessVector.listIterator();
    while (iter.hasNext()) log.info(iter.next());

    // Printing Leisure variables
    log.info("Leisure Days of Week = ");
    iter = leisureVector.listIterator();
    while (iter.hasNext()) log.info(iter.next());
    log.info("Leisure Duration = " + leisureDuration);

    // Printing Public Vacation Variables
    log.info("Public Vacation of Year = ");
    iter = publicVacationVector.listIterator();
    while (iter.hasNext()) log.info(iter.next());

    // Printing Weekly Schedule
    log.info("Weekly Routine Length : " + weeklyRoutine.size())
    log.info("Weekly Routine : ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      iter = weeklyRoutine.get(i).listIterator();
      for (int j =0;j < Constants.QUARTERS_OF_DAY;j++) log.info("Quarter : " + (j+1) + " Status : " + iter.next())
    }
  }

  @ Override
  void refresh(ConfigObject conf, Random gen)
  {

    // Renew Variables
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float MPLeisure = conf.household.leisure.numberByType.MPLeisure
    float vacationAbsence = conf.household.vacation.VacationAbsence
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
