/*
 * Copyright 2020 ConsenSys AG.
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

package tech.pegasys.teku.api;

import tech.pegasys.teku.api.response.v1.node.Syncing;
import tech.pegasys.teku.beacon.sync.SyncService;
import tech.pegasys.teku.beacon.sync.events.SyncStateProvider;
import tech.pegasys.teku.beacon.sync.events.SyncingStatus;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public class SyncDataProvider {

  private final SyncService syncService;

  public SyncDataProvider(SyncService syncService) {
    this.syncService = syncService;
  }

  public Syncing getSyncing() {
    SyncingStatus syncStatus = syncService.getSyncStatus();
    return new Syncing(
        syncStatus.getCurrentSlot(), getSlotsBehind(syncStatus), syncStatus.isSyncing());
  }

  public long subscribeToSyncStateChanges(SyncStateProvider.SyncStateSubscriber subscriber) {
    return syncService.subscribeToSyncStateChanges(subscriber);
  }

  public boolean unsubscribeFromSyncStateChanges(long subscriberId) {
    return syncService.unsubscribeFromSyncStateChanges(subscriberId);
  }

  // Sync state can be influenced by needing to sync from nodes, but also from
  // starting up and having a lack of peers. Consider both 'syncing' and 'startup'
  // as 'isSyncing', as both of these states will mean that you can't perform duties
  public boolean isSyncing() {
    return !syncService.getCurrentSyncState().isInSync();
  }

  private UInt64 getSlotsBehind(final SyncingStatus syncingStatus) {
    if (syncingStatus.isSyncing() && syncingStatus.getHighestSlot().isPresent()) {
      final UInt64 highestSlot = syncingStatus.getHighestSlot().get();
      return highestSlot.minusMinZero(syncingStatus.getCurrentSlot());
    }
    return UInt64.ZERO;
  }
}
