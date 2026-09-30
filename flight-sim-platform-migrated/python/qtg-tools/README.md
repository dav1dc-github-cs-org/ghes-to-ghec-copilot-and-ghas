# qtg-tools

`qtg-tools` compares FS-200 simulator Qualification Test Guide (QTG) run output with reference flight-test data and creates engineering reports for review.

## Install

Use Python 3.10 or newer:

```bash
python -m pip install -r requirements-dev.txt
python -m pip install -e .
```

## Generate reference data

Reference data lives in [reference-data/](reference-data/) and is generated deterministically:

```bash
python scripts/generate_reference_data.py
```

The reference CSVs are stored with Git LFS. After cloning this repository, run:

```bash
git lfs pull
```

If LFS is not enabled, tests will fail because the CSV files are pointer files rather than data.

## Compare a simulator run

```bash
qtg compare 1.b.1 path/to/simulator-output.csv --report out/1b1.html
```

The command exits with status `0` when the run is within tolerance and `1` when any channel fails.

## View results

Start the internal viewer from this folder:

```bash
QTG_RESULTS_DIR=results qtg viewer --host 127.0.0.1 --port 8085
```

Open `http://127.0.0.1:8085/` to browse the catalogue and stored result files.

## Import a results pack

```bash
qtg import-pack path/to/results-pack.tar.gz --dest results/imported
```

Result packs are produced by FS-200 simulator stations after QTG runs and contain reports, plots, and metadata.
