---
applyTo: "python/**"
---

# Python conventions (qtg-tools)

- Python 3.10 or later; CI and the cloud agent use 3.12. Type hints on public functions and
  dataclasses for results.
- Tests use pytest and never make network calls.
- Reference data lives in Git LFS under `reference-data/`. Do not regenerate or edit it in a pull
  request; `scripts/generate_reference_data.py` only rebuilds the synthetic data set.
- Tolerances come from `qtg/testcases.py`; never hard-code them in a comparison.
- Flask views: parameterised SQL, `yaml.safe_load`, no `shell=True`, and templates with
  autoescape.
