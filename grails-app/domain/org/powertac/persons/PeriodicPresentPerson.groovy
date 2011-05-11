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

import java.util.HashMap
import java.util.Vector

import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.Status

/**
 * This is the instance of the person type that works in a regular basis for
 * a number of days in the . The standard program gives space for some leisure
 * activities.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 **/

class PeriodicPresentPerson extends WorkingPerson {

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
    float PPLeisure = ((int)hm.get("PPLeisure"))
    int workingDurationMean = ((int)hm.get("WorkingDurationMean"))
    int workingDurationDev = ((int)hm.get("WorkingDurationDev"))
    int vacationDurationMean = ((int)hm.get("VacationDurationMean"))
    int vacationDurationDev = ((int)hm.get("VacationDurationDev"))
    // Filling the main variables
    name = AgentName
    status = Status.Normal

    // Filling the sickness and public Vacation Vectors
    sicknessVector = createSicknessVector(sicknessMean,sicknessDev, gen)
    this.publicVacationVector = publicVacationVector
    // Filling the leisure variables
    int x = (int) (gen.nextGaussian() + PPLeisure)
    leisureVector = createLeisureVector(x, gen)
    leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
    // Filling Working variables
    workingStartHour = Constants.START_OF_WORK
    int work = workingDaysRandomizer(hm, gen)
    workingDays = createWorkingDaysVector(work, gen)
    workingDuration = (int) (workingDurationDev * gen.nextGaussian() + workingDurationMean)
    // Filling Vacation Variables
    vacationDuration = (int) (vacationDurationDev * gen.nextGaussian() + vacationDurationMean)
    vacationVector = createVacationVector(vacationDuration,gen)
  }


  /** This function fills out the leisure activities in the daily schedule
   * of the person in question.
   * @param weekday
   * @return
   */
  def addLeisureWorking(int weekday, Random gen) {

    // Create auxiliary variables
    ListIterator iter = leisureVector.listIterator();
    Status st

    // Check each day on leisure vector
    while (iter.hasNext()) {
      if (iter.next() == weekday) {
        int start = workingStartHour + workingDuration
        int startq = gen.nextInt(Math.max(1 ,75 - start)) + start
        for (int i = startq;i < startq +leisureDuration;i++) {
          st = Status.Leisure
          dailyRoutine.set(i,st)
          if (i == Constants.QUARTERS_OF_DAY - 1) break
        }
      }
    }
  }

  @ Override
  def fillWork()
  {
    // Create auxiliary variables
    Status st
    for (int i = workingStartHour;i < workingStartHour + workingDuration;i++) {
      st = Status.Working
      dailyRoutine.set(i,st)
    }
  }

  @ Override
  void refresh(HashMap hm, Random gen)
  {

    // Renew Variables
    float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
    float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
    float PPLeisure = ((int)hm.get("PPLeisure"))
    float vacationAbsence = ((float)hm.get("VacationAbsence"))
    int x = (int) (gen.nextGaussian() + PPLeisure)
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
