/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powertac.household.customer

import org.powertac.appliances.Appliance

/**
 * Stores Households in each category of consumers and consumption vectors on behalf of Household Customers, bypassing the database.
 * @author Antonios Chrysopoulos
 */
class HouseholdConsumersService {

  static transactional = true

  /** This variable contains the operating vectors of each appliance during the competition period. */
  Map appliancesOperations = [:]

  /** This variable contains the load of the appliances usage throughout the competition */
  Map appliancesLoads = [:]

  /** This variable contains the possible operation times of each appliance for the competition's duration. */
  Map appliancesPossibilityOperations = [:]

  /** This variable contains the operation days of each appliance for the competition's duration. */
  Map appliancesOperationDays = [:]


  void createAppliancesOperationsMap (Appliance appliance) {
    log.info "create appliance Operations map for Appliance ${appliance.toString()}"
    appliancesOperations[appliance.name] = new boolean[63][96]
  }

  void createAppliancesLoadsMap (Appliance appliance) {
    log.info "create appliance load map for Appliance ${appliance.toString()}"
    appliancesLoads[appliance.name] = new long[63][96]
  }

  void createAppliancesPossibilityOperationsMap (Appliance appliance) {
    log.info "create appliance Possibility Operations map for Appliance ${appliance.toString()}"
    appliancesPossibilityOperations[appliance.name] = new boolean[63][96]
  }

  void createAppliancesOperationDaysMap (Appliance appliance) {
    log.info "create appliance operation days map for Appliance ${appliance.toString()}"
    appliancesOperationDays[appliance.name] = new boolean[63]
  }

  void setApplianceOperation(Appliance appliance, int day, int quarter, Boolean function) {
    def applianceOperationMap = appliancesOperations[appliance.name]
    if (applianceOperationMap == null) {
      log.error "could not find Appliance Operation map for appliance ${appliance.toString()}"
      return
    }
    applianceOperationMap[day][quarter] = function
  }

  void setApplianceLoad(Appliance appliance, int day, int quarter, long value) {
    def applianceLoadMap = appliancesLoads[appliance.name]
    if (applianceLoadMap == null) {
      log.error "could not find Appliance Load map for Appliance ${appliance.toString()}"
      return
    }
    applianceLoadMap[day][quarter] = value
  }

  void setAppliancePossibilityOperation(Appliance appliance, int day, int quarter, Boolean function) {
    def appliancePossibilityOperationMap = appliancesPossibilityOperations[appliance.name]
    if (appliancePossibilityOperationMap == null) {
      log.error "could not find Appliance Operation map for Appliance ${appliance.toString()}"
      return
    }
    appliancePossibilityOperationMap[day][quarter] = function
  }

  void setApplianceOperationDay(Appliance appliance, int day, Boolean function) {
    def applianceOperationDaysMap = appliancesOperationDays[appliance.name]
    if (applianceOperationDaysMap == null) {
      log.error "could not find Appliance Operation Days map for Appliance ${appliance.toString()}"
      return
    }
    applianceOperationDaysMap[day] = function
  }

  def getApplianceOperations(Appliance appliance) {
    return appliancesOperations[appliance.name]
  }

  def getApplianceOperations(Appliance appliance, int day) {
    return appliancesOperations[appliance.name][day]
  }

  def getApplianceOperations(Appliance appliance, int day, int quarter) {
    return appliancesOperations[appliance.name][day][quarter]
  }

  def getApplianceLoads(Appliance appliance) {
    return appliancesLoads[appliance.name]
  }

  def getApplianceLoads(Appliance appliance, int day) {
    return appliancesLoads[appliance.name][day]
  }

  def getApplianceLoads(Appliance appliance, int day, int quarter) {
    return appliancesLoads[appliance.name][day][quarter]
  }

  def getAppliancePossibilityOperations(Appliance appliance) {
    return appliancesPossibilityOperations[appliance.name]
  }

  def getAppliancePossibilityOperations(Appliance appliance, int day) {
    return appliancesPossibilityOperations[appliance.name][day]
  }

  def getAppliancePossibilityOperations(Appliance appliance, int day, int quarter) {
    return appliancesPossibilityOperations[appliance.name][day][quarter]
  }

  def getApplianceOperationDays(Appliance appliance) {
    return appliancesOperationDays[appliance.name]
  }
  def getApplianceOperationDays(Appliance appliance,int day) {
    return appliancesOperationDays[appliance.name][day]
  }
}

