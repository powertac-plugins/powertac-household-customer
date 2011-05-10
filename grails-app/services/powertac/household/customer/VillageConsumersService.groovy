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

import org.powertac.consumers.Household
import org.powertac.consumers.Village

/**
 * Stores Households in each category of consumers and consumption vectors on behalf of Household Customers, bypassing the database.
 * @author Antonios Chrysopoulos
 */
class VillageConsumersService {

  static transactional = true

  Map households = [:]
  Map consumptions = [:]

  // manage rate maps
  void createHouseholdsMap (Village village, int types, int population)
  {
    log.info "create household map for Household Customer ${village.toString()} [${types}]"
    households[village.customerInfo.name] = new Household[types][population]
  }

  // manage tier lists
  def getHouseholds(Village village, int type)
  {
    def householdMap = households[village.customerInfo.name]

    if (householdMap == null) {
      log.error "could not find household map for Village ${village.toString()}"
      return
    }
    return householdMap[type]
  }

  void setHousehold(Village village, int type, int index, Household house)
  {
    def householdMap = households[village.customerInfo.name]
    if (householdMap == null) {
      log.error "could not find Household map for village ${village.toString()}"
      return
    }
    householdMap[type][index] = house
  }


  void createConsumptionsMap (Village village, int types)
  {
    log.info "create consumption map for Household Customer ${village.id} [${types}]"
    consumptions[village.customerInfo.name] = new BigDecimal[types][7][24]
  }

  def getConsumptions(Village village, int type)
  {
    return consumptions[village.customerInfo.name][type]
  }

  void setConsumption(Village village, int type, int weekday, int hour, BigDecimal value)
  {
    def consumptionMap = consumptions[village.customerInfo.name]
    if (consumptionMap == null) {
      log.error "could not find Consumption map for village ${village.toString()}"
      return
    }
    consumptionMap[type][weekday][hour] = value
  }

}

