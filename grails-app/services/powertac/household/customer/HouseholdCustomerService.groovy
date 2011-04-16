/*
 * Copyright 2011 the original author or authors.
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

package powertac.household.customer

import org.joda.time.Instant
import org.powertac.common.interfaces.TimeslotPhaseProcessor
import org.powertac.consumers.Village

class HouseholdCustomerService implements TimeslotPhaseProcessor {

  static transactional = true

  def timeService // autowire
  def competitionControlService


  void init()
  {
    competitionControlService.registerTimeslotPhase(this, 1)
    competitionControlService.registerTimeslotPhase(this, 2)
  }

  void activate(Instant now, int phase) {
    log.info "Activate"
    def villageList = Village.list()
    villageList*.step()
  }
}
