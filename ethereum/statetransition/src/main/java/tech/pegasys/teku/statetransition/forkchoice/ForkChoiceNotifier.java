/*
 * Copyright 2022 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.statetransition.forkchoice;

import java.util.Collection;
import java.util.Optional;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.infrastructure.ssz.type.Bytes8;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.datastructures.operations.versions.bellatrix.BeaconPreparableProposer;
import tech.pegasys.teku.spec.executionengine.ForkChoiceState;
import tech.pegasys.teku.spec.executionengine.ForkChoiceUpdatedResult;

public interface ForkChoiceNotifier {
  void onUpdatePreparableProposers(Collection<BeaconPreparableProposer> proposers);

  SafeFuture<Optional<ForkChoiceUpdatedResult>> onForkChoiceUpdated(
      ForkChoiceState forkChoiceState);

  void onAttestationsDue(UInt64 slot);

  void onSyncingStatusChanged(boolean inSync);

  SafeFuture<Optional<Bytes8>> getPayloadId(Bytes32 parentBeaconBlockRoot, UInt64 blockSlot);

  void onTerminalBlockReached(Bytes32 executionBlockHash);

  PayloadAttributesCalculator getPayloadAttributesCalculator();
}
