/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.util;

import org.hyperledger.besu.ethereum.permissioning.LocalPermissioningConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PermissioningConfigurationValidator {

  public static void areAllNodesAreInAllowlist(
      final Collection<URI> nodeURIs,
      final LocalPermissioningConfiguration permissioningConfiguration)
      throws Exception {

    if (permissioningConfiguration.isNodeAllowlistEnabled() && nodeURIs != null) {
      final List<URI> allowlistNodesWithoutQueryParam =
          permissioningConfiguration.getNodeAllowlist().stream()
              .map(PermissioningConfigurationValidator::removeQueryFromURI)
              .collect(Collectors.toList());

      final List<URI> nodeURIsNotInAllowlist =
          nodeURIs.stream()
              .map(PermissioningConfigurationValidator::removeQueryFromURI)
              .filter(uri -> !allowlistNodesWithoutQueryParam.contains(uri))
              .collect(Collectors.toList());

      if (!nodeURIsNotInAllowlist.isEmpty()) {
        throw new Exception(
            "Specified node(s) not in nodes-allowlist " + enodesAsStrings(nodeURIsNotInAllowlist));
      }
    }
  }

  private static URI removeQueryFromURI(final URI uri) {
    try {
      return new URI(
          uri.getScheme(),
          uri.getUserInfo(),
          uri.getHost(),
          uri.getPort(),
          uri.getPath(),
          null,
          uri.getFragment());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static Collection<String> enodesAsStrings(final List<URI> enodes) {
    return enodes.parallelStream().map(URI::toASCIIString).collect(Collectors.toList());
  }
}
