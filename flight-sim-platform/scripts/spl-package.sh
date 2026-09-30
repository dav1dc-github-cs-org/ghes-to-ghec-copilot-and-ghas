#!/usr/bin/env bash
# SPL step of the Jenkins pipeline (main branch only).
# The SPL client and its configuration are installed on the Jenkins build agents, not kept in
# this repository, and nothing about SPL changes with the move to GitHub.com.
set -euo pipefail

: "${SPL_CLIENT:?The SPL client is only available on the Jenkins build agents}"
exec "$SPL_CLIENT" "$@"
