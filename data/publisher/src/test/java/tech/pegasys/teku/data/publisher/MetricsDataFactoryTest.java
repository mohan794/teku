/*
 * Copyright 2021 ConsenSys AG.
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

package tech.pegasys.teku.data.publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.hyperledger.besu.metrics.prometheus.PrometheusMetricsSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tech.pegasys.teku.infrastructure.time.StubTimeProvider;
import tech.pegasys.teku.test.data.publisher.StubMetricsPublisherSource;

class MetricsDataFactoryTest {
  private final PrometheusMetricsSystem prometheusMetricsSystem =
      mock(PrometheusMetricsSystem.class);
  private final StubTimeProvider timeProvider = StubTimeProvider.withTimeInSeconds(10_000);

  @ParameterizedTest(name = "Total_{0}_Active_{1}")
  @MethodSource("getValidatorParams")
  void shouldIncludeValidatorMetricsInPublish(
      final int validatorsTotal, final int validatorsActive, final boolean isValidatorActive) {

    final MetricsDataFactory factory =
        new MetricsDataFactory(prometheusMetricsSystem, timeProvider);
    MetricsPublisherSource source =
        StubMetricsPublisherSource.builder()
            .validatorsActive(validatorsActive)
            .validatorsTotal(validatorsTotal)
            .build();

    final List<BaseMetricData> data = factory.getMetricData(source);
    assertThat(data.size()).isEqualTo(isValidatorActive ? 1 : 0);
    data.forEach(element -> assertThat(element).isInstanceOf(ValidatorMetricData.class));
  }

  @Test
  void shouldIncludeBeaconMetricsInPublish() {
    final MetricsDataFactory factory =
        new MetricsDataFactory(prometheusMetricsSystem, timeProvider);
    MetricsPublisherSource source =
        StubMetricsPublisherSource.builder().isBeaconNodePresent(true).build();

    final List<BaseMetricData> data = factory.getMetricData(source);
    assertThat(data.size()).isEqualTo(1);
    data.forEach(element -> assertThat(element).isInstanceOf(BeaconNodeMetricData.class));
  }

  public static java.util.stream.Stream<Arguments> getValidatorParams() {
    return java.util.stream.Stream.of(
        Arguments.of(1, 2, true),
        Arguments.of(0, 1, true),
        Arguments.of(1, 0, true),
        Arguments.of(0, 0, false));
  }
}
