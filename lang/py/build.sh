#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

usage() {
  echo "Usage: $0 {lint|test|dist|clean}"
  exit 1
}

main() {
  local target
  (( $# )) || usage
  for target; do
    case "$target" in
      lint)
        ./setup.py isort lint
        ;;
      test)
        ./setup.py test
        ;;
      dist)
        ./setup.py dist
        ;;
      clean)
        ./setup.py clean
        rm -rf userlogs/
        ;;
      interop-data-generate)
        ./setup.py generate_interop_data
        ;;
      interop-data-test)
        python -m unittest avro.test.test_datafile_interop
        ;;
      *)
        usage
        ;;
    esac
  done
}

main "$@"
