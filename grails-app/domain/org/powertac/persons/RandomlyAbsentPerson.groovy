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

import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.Status

/**
 * This is the instance of the person type that works in shifts that may vary
 * form week to week or from month to month. The consequence is that he has
 * little time for leisure activities.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 **/

class RandomlyAbsentPerson extends WorkingPerson {


  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the person as it should for this type.
   * @param AgentName
   * @param conf
   * @param publicVacationVector
   * @param gen
   * @return
   */
  def initialize(String AgentName, ConfigObject conf, Vector publicVacationVector, Random gen) {
    // Variables Taken from the configuration file
    float sicknessMean = conf.household.sickness.SicknessMean
    float sicknessDev = conf.household.sickness.SicknessDev
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float RALeisure = conf.household.leisure.numberByType.RALeisure
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
    int x = (int) (gen.nextGaussian() + RALeisure)
    leisureVector = createLeisureVector(x, gen)
    leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
    // Filling Working variables
    int work = workingDaysRandomizer(conf,gen)
    workingDays = createWorkingDaysVector(work, gen)
    workingStartHour = createWorkingStartHour(gen)
    workingDuration = (int) (workingDurationDev * gen.nextGaussian() + workingDurationMean)
    // Filling Vacation Variables
    vacationDuration = (int) (vacationDurationDev * gen.nextGaussian() + vacationDurationMean)
    vacationVector = createVacationVector(vacationDuration, gen)
  }

  /** This function selects the shift of the worker. There three different shifts: 00:00 - 08:00
   * 08:00 - 16:00 and 16:00 - 24:00.
   * @param gen
   * @return
   */

  def createWorkingStartHour(Random gen) {

    int x = gen.nextInt(Constants.NUMBER_OF_SHIFTS)
    return (x * Constants.HOURS_OF_SHIFT_WORK * Constants.QUARTERS_OF_HOUR)
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
    while (iter.hasNext()) {
      if (iter.next() == weekday) {
        int start = workingStartHour + workingDuration
        if (workingStartHour == Constants.SHIFT_START_1) {
          int startq = gen.nextInt((Constants.LEISURE_WINDOW+1) - start) + (start + Constants.SHIFT_START_2)
          for (int i = startq;i < startq + leisureDuration;i++) {
            st = Status.Leisure
            dailyRoutine.set(i,st)
            if (i == Constants.QUARTERS_OF_DAY - 1) break
          }
        } else  {
          if (workingStartHour == Constants.SHIFT_START_2) {
            int startq = start + gen.nextInt(Constants.LEISURE_WINDOW_SHIFT - start)
            for (int i = startq;i < startq + leisureDuration;i++) {
              st = Status.Leisure
              dailyRoutine.set(i,st)
              if (i == Constants.QUARTERS_OF_DAY - 1) break
            }
          } else  {
            int startq = Constants.SHIFT_START_2 + gen.nextInt(Constants.SHIFT_START_3 - (Constants.LEISURE_WINDOW - 1))
            for (int i = startq;i < startq +leisureDuration;i++) {
              st = Status.Leisure
              dailyRoutine.set(i,st)
              if (i == Constants.QUARTERS_OF_DAY - 1) break
            }
          }
        }
      }
    }
  }

  @ Override
  def fillWork()
  {
    // Create auxiliary variables
    Status st
    if (workingStartHour == Constants.SHIFT_START_1) {
      for (int i = Constants.SHIFT_START_1;i < workingDuration;i++) {
        st = Status.Working
        dailyRoutine.set(i,st)
      }
      for (int i = workingDuration;i < workingDuration + Constants.SHIFT_START_2 ;i++) {
        st = Status.Sleeping
        dailyRoutine.set(i,st)
      }
      for (int i = workingDuration + Constants.SHIFT_START_2;i < Constants.QUARTERS_OF_DAY;i++) {
        st = Status.Normal
        dailyRoutine.set(i,st)
      }
    } else  {
      if (workingStartHour == Constants.SHIFT_START_2) {
        for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
          st = Status.Sleeping
          dailyRoutine.set(i,st)
        }
        for (int i = Constants.END_OF_SLEEPING_1;i < Constants.SHIFT_START_2;i++) {
          st = Status.Normal
          dailyRoutine.set(i,st)
        }
        for (int i = Constants.SHIFT_START_2;i < workingDuration + Constants.SHIFT_START_2 ;i++) {
          st = Status.Working
          dailyRoutine.set(i,st)
        }
        for (int i = workingDuration + Constants.SHIFT_START_2;i < Constants.START_OF_SLEEPING_1;i++) {
          st = Status.Normal
          dailyRoutine.set(i,st)
        }
        for (int i = Constants.START_OF_SLEEPING_1;i < Constants.QUARTERS_OF_DAY;i++) {
          st = Status.Sleeping
          dailyRoutine.set(i,st)
        }
      } else  {
        for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
          st = Status.Sleeping
          dailyRoutine.set(i,st)
        }
        for (int i = Constants.END_OF_SLEEPING_1;i < Constants.SHIFT_START_3 ;i++) {
          st = Status.Normal
          dailyRoutine.set(i,st)
        }
        if (workingDuration > Constants.HOURS_OF_SHIFT_WORK * Constants.QUARTERS_OF_HOUR) {
          for (int i = Constants.SHIFT_START_3;i < Constants.QUARTERS_OF_DAY;i++) {
            st = Status.Working
            dailyRoutine.set(i,st)
          }
        } else  {
          for (int i = Constants.SHIFT_START_3;i < Constants.SHIFT_START_3 + workingDuration;i++) {
            if (i >= Constants.QUARTERS_OF_DAY) break
              st = Status.Working
            dailyRoutine.set(i,st)
          }
          for (int i =  Constants.SHIFT_START_3 + workingDuration;i < Constants.QUARTERS_OF_DAY;i++) {
            st = Status.Sleeping
            dailyRoutine.set(i,st)
          }
        }
      }
    }
  }

  @ Override
  void refresh(ConfigObject conf, Random gen)
  {
    // Renew Variables
    float leisureDurationMean = conf.household.leisure.duration.LeisureDurationMean
    float leisureDurationDev = conf.household.leisure.duration.LeisureDurationDev
    float RALeisure = conf.household.leisure.numberByType.RALeisure
    float vacationAbsence = conf.household.vacation.VacationAbsence
    int work = workingDaysRandomizer(conf,gen)
    workingDays = createWorkingDaysVector(work,gen)
    workingStartHour = createWorkingStartHour(gen)

    int x = (int) (gen.nextGaussian() + RALeisure)
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
