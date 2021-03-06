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

import java.util.Vector

import org.powertac.common.configurations.HouseholdConstants
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
   * @param conf
   * @param publicVacationVector
   * @param gen
   * @return
   */
  def initialize(String AgentName,ConfigObject conf, Vector publicVacationVector, Random gen) {
    // Variables Taken from the configuration file
    float sicknessMean = conf.household.sickness.SicknessMean
    float sicknessDev = conf.household.sickness.SicknessDev
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float PPLeisure = conf.household.leisure.numberByType.PPLeisure
    int workingDurationMean = conf.household.work.duration.WorkingDurationMean
    int workingDurationDev = conf.household.work.duration.WorkingDurationDev
    int vacationDurationMean = conf.household.vacation.VacationDurationMean
    int vacationDurationDev = conf.household.vacation.VacationDurationDev
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
    workingStartHour = HouseholdConstants.START_OF_WORK
    int work = workingDaysRandomizer(conf, gen)
    workingDays = createWorkingDaysVector(work, gen)
    workingDuration = (int) (workingDurationDev * gen.nextGaussian() + workingDurationMean)
    // Filling Vacation Variables
    vacationDuration = (int) (vacationDurationDev * gen.nextGaussian() + vacationDurationMean)
    vacationVector = createVacationVector(Math.max(0,vacationDuration),gen)
  }


  /** This function fills out the leisure activities in the daily schedule
   * of the person in question.
   * @param weekday
   * @param gen
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
        int startq = gen.nextInt(Math.max(1 ,HouseholdConstants.LEISURE_END_WINDOW - start)) + start
        for (int i = startq;i < startq +leisureDuration;i++) {
          st = Status.Leisure
          dailyRoutine.set(i,st)
          if (i == HouseholdConstants.QUARTERS_OF_DAY - 1) break
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
  void refresh(ConfigObject conf, Random gen)
  {

    // Renew Variables
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float PPLeisure = conf.household.leisure.numberByType.PPLeisure
    float vacationAbsence = conf.household.vacation.VacationAbsence
    int x = (int) (gen.nextGaussian() + PPLeisure)
    leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
    leisureVector = createLeisureVector(x,gen)
    for (int i =0;i < HouseholdConstants.DAYS_OF_WEEK;i++) {
      fillDailyRoutine(i,vacationAbsence, gen)
      weeklyRoutine.add(dailyRoutine)
    }
  }

  static constraints = {
  }
}
