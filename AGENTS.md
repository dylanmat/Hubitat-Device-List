# Context and Corrections

## Persistent project rules
- App is written in Groovy for Hubitat and must run as an installed Hubitat App.
- README.md must always include basic usage info and a changelog.
- TODO.md is the source of pending fixes and should be reviewed before new work.
- Prefer simple, readable implementations with minimal complexity.
- Every branch update and pull request must increment the app version.

## Corrections log
- 2026-02-25: Corrected implementation direction to Hubitat-installed app only (not a CLI app).
  - Prevention: Keep Hubitat install/usage language in README and avoid command-line workflow assumptions.
